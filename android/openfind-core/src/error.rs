use std::fmt;
use std::io;

#[derive(Debug)]
pub enum DnsError {
    NoIp,
    Io(io::Error),
    TlsError(String),
    Timeout,
}

#[derive(Debug)]
pub enum WhoisError {
    Connection(String),
    Parse(String),
}

#[derive(Debug)]
pub enum SslError {
    Connection(String),
    Handshake(String),
    CertValidation(String),
}

impl fmt::Display for DnsError {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            DnsError::NoIp => write!(f, "No IP address resolved"),
            DnsError::Io(e) => write!(f, "IO error: {}", e),
            DnsError::TlsError(e) => write!(f, "TLS error: {}", e),
            DnsError::Timeout => write!(f, "Connection timed out"),
        }
    }
}

impl fmt::Display for WhoisError {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            WhoisError::Connection(e) => write!(f, "WHOIS connection error: {}", e),
            WhoisError::Parse(e) => write!(f, "WHOIS parse error: {}", e),
        }
    }
}

impl fmt::Display for SslError {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            SslError::Connection(e) => write!(f, "SSL connection error: {}", e),
            SslError::Handshake(e) => write!(f, "SSL handshake error: {}", e),
            SslError::CertValidation(e) => write!(f, "SSL cert validation error: {}", e),
        }
    }
}

impl std::error::Error for DnsError {}
impl std::error::Error for WhoisError {}
impl std::error::Error for SslError {}

impl From<io::Error> for DnsError {
    fn from(e: io::Error) -> Self { DnsError::Io(e) }
}

impl From<rustls::Error> for DnsError {
    fn from(e: rustls::Error) -> Self { DnsError::TlsError(e.to_string()) }
}
