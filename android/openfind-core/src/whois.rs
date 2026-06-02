use std::io::{BufRead, BufReader, Write};
use std::net::TcpStream;
use std::time::Duration;

use regex::Regex;
use once_cell::sync::Lazy;

use crate::error::WhoisError;

/// Parsed WHOIS response data.
#[derive(Debug, Clone, Default)]
pub struct WhoisData {
    pub status: String,          // "available", "taken", "unknown"
    pub registrar: Option<String>,
    pub creation_date: Option<String>,
    pub nameservers: Vec<String>,
    pub expiry_date: Option<String>,
}

/// Query a WHOIS server via raw TCP socket on port 43.
pub fn query_whois(domain: &str, server: &str) -> String {
    let addr = format!("{}:43", server);
    match TcpStream::connect_timeout(&addr, Duration::from_secs(8)) {
        Ok(mut stream) => {
            stream.set_read_timeout(Some(Duration::from_secs(8))).ok();

            let query = match server {
                "whois.verisign-grs.com" => format!("domain {}\r\n", domain),
                "whois.denic.de" => format!("-T dn {}\r\n", domain),
                _ => format!("{}\r\n", domain),
            };

            if stream.write_all(query.as_bytes()).is_err() {
                return format!("ERROR: Failed to send WHOIS query to {}", server);
            }

            let mut response = String::new();
            let reader = BufReader::new(&mut stream);
            for line in reader.lines() {
                if let Ok(l) = line {
                    response.push_str(&l);
                    response.push('\n');
                }
            }
            response
        }
        Err(e) => format!("ERROR: {}", e),
    }
}

/// Parse a WHOIS response string into structured data.
pub fn parse_whois(response: &str, tld: &str) -> WhoisData {
    let lower = response.to_lowercase();

    let is_available = AVAILABLE_PATTERNS.iter().any(|re| re.is_match(&lower));
    if is_available {
        return WhoisData {
            status: "available".into(),
            ..Default::default()
        };
    }

    let is_taken = TAKEN_PATTERNS.iter().any(|re| re.is_match(&lower))
        || response.len() > 250;

    if !is_taken {
        return WhoisData {
            status: "unknown".into(),
            ..Default::default()
        };
    }

    let mut registrar = None;
    let mut creation_date = None;
    let mut nameservers = Vec::new();
    let mut expiry_date = None;

    for line in response.lines() {
        let l = line.trim().to_lowercase();

        if registrar.is_none() {
            if let Some(val) = extract_field(&l, REGISTRAR_PATTERNS) {
                registrar = Some(val);
            }
        }

        if creation_date.is_none() {
            if let Some(val) = extract_field(&l, CREATION_PATTERNS) {
                creation_date = Some(val);
            }
        }

        if expiry_date.is_none() {
            if let Some(val) = extract_field(&l, EXPIRY_PATTERNS) {
                expiry_date = Some(val);
            }
        }

        if l.starts_with("nserver:") || l.starts_with("name server:") || l.starts_with("nameserver:") {
            if let Some(ns) = extract_ns(&l) {
                if !nameservers.contains(&ns) {
                    nameservers.push(ns);
                }
            }
        }
    }

    WhoisData {
        status: "taken".into(),
        registrar,
        creation_date,
        nameservers,
        expiry_date,
    }
}

const REGISTRAR_PATTERNS: &[&str] = &[
    "registrar:", "registrador:", "sponsoring registrar:",
    "registrar name:", "organisation:",
];

const CREATION_PATTERNS: &[&str] = &[
    "creation date:", "created:", "created on:", "domain registration date:",
    "registered on:", "fecha de creacion:", "registered:",
    "domain create date:", "created date:",
];

const EXPIRY_PATTERNS: &[&str] = &[
    "registry expiry date:", "expiry date:", "expires:", "expiration date:",
    "paid-till:", "domain expires:", "renewal date:",
];

fn extract_field(line: &str, patterns: &[&str]) -> Option<String> {
    for pat in patterns {
        if line.starts_with(pat) {
            let val = line[pat.len()..].trim();
            if !val.is_empty() {
                return Some(val.to_string());
            }
        }
    }
    None
}

fn extract_ns(line: &str) -> Option<String> {
    for prefix in &["nserver:", "name server:", "nameserver:"] {
        if line.starts_with(prefix) {
            let mut val = line[prefix.len()..].trim().to_string();
            if val.ends_with('.') {
                val.pop();
            }
            if !val.is_empty() {
                return Some(val);
            }
        }
    }
    None
}

static AVAILABLE_PATTERNS: Lazy<Vec<Regex>> = Lazy::new(|| {
    let patterns = [
        "no match", "not found", "available", "no entries found",
        "no data found", "free", "status: free", "incorrect domain name",
        "is free", "no registered", "not registered", "no object found",
        "domain not found", "is available", "no match for", "no se encuentra",
        "el dominio no existe", "no matching record", "object_not_found",
        "domain not registered", "queried object does not exist",
        "the domain has not been registered", "domain status: free",
        "domain_not_found", "not found:", "not found in registry",
        "domain is not registered", "the queried object does not exist",
        "no whois information found", "unknown domain",
    ];
    patterns.iter().map(|p| Regex::new(&format!("(?i){}", regex::escape(p))).unwrap()).collect()
});

static TAKEN_PATTERNS: Lazy<Vec<Regex>> = Lazy::new(|| {
    let patterns = [
        "registrar:", "creation date:", "domain name:", "registry domain id:",
        "status: registered", "registered:", "expir", "changed:", "owner:",
        "sponsoring registrar:", "registrant", "administrative contact:",
        "technical contact:", "name servers:", "name server:",
    ];
    patterns.iter().map(|p| Regex::new(&format!("(?i){}", regex::escape(p))).unwrap()).collect()
});
