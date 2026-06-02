use crate::dns::{http_get_header, tls_connect};
use crate::error::SslError;

/// Result of a server audit.
#[derive(Debug, Clone)]
pub struct AuditResult {
    pub ssl_active: bool,
    pub ssl_issuer: Option<String>,
    pub cloudflare_mode: String,   // "orange", "gray", "none"
    pub ns_servers: Vec<String>,
}

/// Audit a domain's server: extract NS from WHOIS, check SSL, detect Cloudflare.
pub fn audit_server(domain: &str, _ip: Option<&str>, raw_whois: Option<&str>) -> AuditResult {
    let mut ns_servers = Vec::new();

    if let Some(whois) = raw_whois {
        ns_servers = extract_nameservers(whois);
    }

    let mut ssl_active = false;
    let mut ssl_issuer: Option<String> = None;
    let mut is_orange = false;
    let mut is_gray = false;

    match http_get_header(domain) {
        Ok(response) => {
            let lower = response.to_lowercase();
            if lower.contains("server:") && lower.contains("cloudflare") {
                is_orange = true;
            }
        }
        Err(_) => {}
    }

    match tls_connect(domain) {
        Ok((issuer, _cn)) => {
            ssl_active = true;
            if issuer.to_lowercase().contains("cloudflare") {
                is_orange = true;
            }
            ssl_issuer = Some(issuer);
        }
        Err(_) => {}
    }

    let has_cf_ns = ns_servers.iter().any(|ns| ns.to_lowercase().contains("cloudflare"));
    if has_cf_ns && !is_orange {
        is_gray = true;
    }

    let cloudflare = if is_orange { "orange" } else if is_gray { "gray" } else { "none" };

    AuditResult {
        ssl_active,
        ssl_issuer,
        cloudflare_mode: cloudflare.to_string(),
        ns_servers,
    }
}

fn extract_nameservers(whois: &str) -> Vec<String> {
    let mut servers = Vec::new();
    for line in whois.lines() {
        let l = line.trim().to_lowercase();
        if l.starts_with("nserver:") || l.starts_with("name server:") || l.starts_with("nameserver:") {
            let parts: Vec<&str> = l.splitn(2, ':').collect();
            if parts.len() > 1 {
                let mut ns = parts[1].trim().to_string();
                if ns.ends_with('.') {
                    ns.pop();
                }
                if !ns.is_empty() && !servers.contains(&ns) {
                    servers.push(ns);
                }
            }
        }
    }
    servers
}
