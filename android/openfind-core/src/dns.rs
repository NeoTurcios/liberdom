use std::io::{Read, Write};
use std::net::{TcpStream, ToSocketAddrs};
use std::time::Duration;

use crate::error::DnsError;

pub fn resolve_dns(domain: &str) -> Option<String> {
    let addr = format!("{}:0", domain);
    match addr.to_socket_addrs() {
        Ok(mut addrs) => addrs
            .find(|a| a.is_ipv4())
            .map(|a| a.ip().to_string()),
        Err(_) => None,
    }
}

pub fn http_get_header(domain: &str) -> Result<String, DnsError> {
    let addr = format!("{}:80", domain);
    let mut stream = TcpStream::connect_timeout(
        &addr.to_socket_addrs()?.next().ok_or(DnsError::NoIp)?,
        Duration::from_secs(3),
    )?;

    stream.set_read_timeout(Some(Duration::from_secs(2)))?;
    let request = format!(
        "HEAD / HTTP/1.1\r\nHost: {}\r\nUser-Agent: Mozilla/5.0\r\nConnection: close\r\n\r\n",
        domain
    );
    stream.write_all(request.as_bytes())?;

    let mut response = String::new();
    stream.read_to_string(&mut response)?;
    Ok(response)
}

pub fn tls_connect(domain: &str) -> Result<(String, String), DnsError> {
    use rustls::ClientConnection;
    use rustls::pki_types::ServerName;
    use std::sync::Arc;

    let addr = format!("{}:443", domain);
    let mut stream = TcpStream::connect_timeout(
        &addr,
        Duration::from_secs(3),
    )?;
    stream.set_read_timeout(Some(Duration::from_secs(3)))?;

    let mut root_store = rustls::RootCertStore::empty();
    for cert in webpki_roots::TLS_SERVER_ROOTS.iter() {
        root_store.add(cert.clone()).ok();
    }

    let config = rustls::ClientConfig::builder()
        .with_root_certificates(root_store)
        .with_no_client_auth();

    let server_name = ServerName::try_from(domain)
        .map_err(|_| DnsError::TlsError("Invalid server name".into()))?
        .to_owned();

    let mut conn = ClientConnection::new(Arc::new(config), server_name)?;
    conn.complete_io(&mut stream)?;

    let peer_certs = conn.peer_certificates().unwrap_or(&[]);
    let (issuer, cn, not_after) = if let Some(cert) = peer_certs.first() {
        let issuer = extract_cn_from_dn(&cert.issuer_der().to_vec());
        let cn = extract_cn_from_dn(&cert.subject_der().to_vec());
        let not_after = format!("{:?}", conn.peer_certificates());
        (issuer.unwrap_or_default(), cn.unwrap_or_default())
    } else {
        ("Unknown".into(), "Unknown".into())
    };

    Ok((issuer, cn))
}

fn extract_cn_from_dn(der: &[u8]) -> Option<String> {
    let mut pos = 0;
    while pos < der.len() {
        if der[pos] == 0x06 && pos + 1 < der.len() {
            let oid_len = der[pos + 1] as usize;
            let oid = &der[pos + 2..pos + 2 + oid_len];
            if oid == &[0x55, 0x04, 0x03] {
                if pos + 2 + oid_len + 1 < der.len() {
                    let val_start = pos + 2 + oid_len + 1;
                    if der[val_start] >= 0x0c {
                        let val_len = der[val_start] as usize;
                        if val_start + 1 + val_len <= der.len() {
                            return Some(String::from_utf8_lossy(
                                &der[val_start + 1..val_start + 1 + val_len],
                            )
                            .to_string());
                        }
                    }
                }
            }
            pos += 2 + oid_len;
        }
        pos += 1;
    }
    None
}
