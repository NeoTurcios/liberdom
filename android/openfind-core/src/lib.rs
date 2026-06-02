use std::collections::HashMap;
use std::sync::OnceLock;

pub mod dns;
pub mod whois;
pub mod ssl_audit;
pub mod generator;
pub mod brand_eval;
pub mod error;

pub use dns::resolve_dns;
pub use whois::{query_whois, parse_whois, WhoisData};
pub use ssl_audit::audit_server;
pub use generator::generate_names;
pub use brand_eval::evaluate_brand;

/// Result of a complete domain check.
#[derive(Debug, Clone)]
pub struct DomainResult {
    pub domain: String,
    pub status: String,         // "available", "taken", "unknown"
    pub detail: String,
    pub ip: Option<String>,
    pub registrar: Option<String>,
    pub creation_date: Option<String>,
    pub method: String,
    pub ssl_active: bool,
    pub ssl_issuer: Option<String>,
    pub cloudflare: String,     // "orange", "gray", "none"
    pub ns_servers: Vec<String>,
    pub brand_score: Option<f32>,
    pub brand_feedback: Option<String>,
}

/// Brand evaluation score.
#[derive(Debug, Clone)]
pub struct BrandScore {
    pub score: f32,
    pub feedback: String,
    pub length_score: f32,
    pub pronounce_score: f32,
    pub memory_score: f32,
    pub tld_score: f32,
}

/// Check a single domain's availability.
pub fn check_domain(domain: &str, do_audit: bool) -> DomainResult {
    let clean = domain.trim().to_lowercase()
        .replace("https://", "")
        .replace("http://", "")
        .replace("www.", "")
        .split('/')
        .next()
        .unwrap_or("")
        .to_string();

    match resolve_dns(&clean) {
        Some(ip) => {
            let mut result = DomainResult {
                domain: clean.clone(),
                status: "taken".into(),
                detail: "Registered (Active via DNS)".into(),
                ip: Some(ip.clone()),
                registrar: None,
                creation_date: None,
                method: "DNS Resolution".into(),
                ssl_active: false,
                ssl_issuer: None,
                cloudflare: "none".into(),
                ns_servers: vec![],
                brand_score: None,
                brand_feedback: None,
            };

            if do_audit {
                let audit = audit_server(&clean, Some(&ip), None);
                result.ssl_active = audit.ssl_active;
                result.ssl_issuer = audit.ssl_issuer;
                result.cloudflare = audit.cloudflare_mode;
                result.ns_servers = audit.ns_servers.clone();
            }

            result
        }
        None => {
            let tld = clean.split('.').last().unwrap_or("");
            let server = TLD_SERVERS.get(tld).copied().unwrap_or("whois.iana.org");

            let raw_whois = query_whois(&clean, server);
            if raw_whois.starts_with("ERROR:") {
                return DomainResult {
                    domain: clean,
                    status: "unknown".into(),
                    detail: format!("Network error: {}", &raw_whois[6..]),
                    ip: None,
                    registrar: None,
                    creation_date: None,
                    method: "Limit / WHOIS Network".into(),
                    ssl_active: false,
                    ssl_issuer: None,
                    cloudflare: "none".into(),
                    ns_servers: vec![],
                    brand_score: None,
                    brand_feedback: None,
                };
            }

            let (mut final_whois, _refer) = handle_referral(&clean, &raw_whois, &raw_whois);
            let whois_data = parse_whois(&final_whois, tld);

            match whois_data.status.as_str() {
                "available" => DomainResult {
                    domain: clean,
                    status: "available".into(),
                    detail: "Available for registration!".into(),
                    ip: None,
                    registrar: None,
                    creation_date: None,
                    method: "WHOIS Socket 43 Query".into(),
                    ssl_active: false,
                    ssl_issuer: None,
                    cloudflare: "none".into(),
                    ns_servers: vec![],
                    brand_score: None,
                    brand_feedback: None,
                },
                "taken" => {
                    let mut result = DomainResult {
                        domain: clean.clone(),
                        status: "taken".into(),
                        detail: "Registered (Confirmed by WHOIS)".into(),
                        ip: None,
                        registrar: whois_data.registrar.clone(),
                        creation_date: whois_data.creation_date.clone(),
                        method: "WHOIS Socket 43 Query".into(),
                        ssl_active: false,
                        ssl_issuer: None,
                        cloudflare: "none".into(),
                        ns_servers: whois_data.nameservers.clone(),
                        brand_score: None,
                        brand_feedback: None,
                    };

                    if do_audit {
                        let audit = audit_server(&clean, None, Some(&final_whois));
                        result.ssl_active = audit.ssl_active;
                        result.ssl_issuer = audit.ssl_issuer;
                        result.cloudflare = audit.cloudflare_mode;
                        result.ns_servers = audit.ns_servers.clone();
                    }

                    result
                }
                _ => DomainResult {
                    domain: clean,
                    status: "unknown".into(),
                    detail: "Could not be determined with certainty".into(),
                    ip: None,
                    registrar: None,
                    creation_date: None,
                    method: "WHOIS Socket 43 Query".into(),
                    ssl_active: false,
                    ssl_issuer: None,
                    cloudflare: "none".into(),
                    ns_servers: vec![],
                    brand_score: None,
                    brand_feedback: None,
                },
            }
        }
    }
}

fn handle_referral(domain: &str, raw: &str, original: &str) -> (String, Option<String>) {
    let lower = raw.to_lowercase();
    if let Some(line) = lower.lines().find(|l| l.trim().starts_with("refer:")) {
        let refer = line.split(':').nth(1).map(|s| s.trim().to_string());
        if let Some(ref refer_server) = refer {
            if !refer_server.is_empty() && refer_server != "whois.iana.org" {
                let redirect = query_whois(domain, refer_server);
                if !redirect.starts_with("ERROR:") {
                    return (redirect, refer);
                }
            }
        }
    }
    (original.to_string(), None)
}

static TLD_SERVERS: OnceLock<HashMap<&'static str, &'static str>> = OnceLock::new();

fn get_or_init_tld_servers() -> &'static HashMap<&'static str, &'static str> {
    TLD_SERVERS.get_or_init(|| {
        HashMap::from([
            ("com", "whois.verisign-grs.com"), ("net", "whois.verisign-grs.com"),
            ("org", "whois.pir.org"), ("info", "whois.afilias.net"), ("biz", "whois.nic.biz"),
            ("io", "whois.nic.io"), ("co", "whois.nic.co"), ("me", "whois.nic.me"),
            ("es", "whois.nic.es"), ("mx", "whois.nic.mx"), ("cl", "whois.nic.cl"),
            ("ar", "whois.nic.ar"), ("pe", "kero.yachay.pe"), ("us", "whois.nic.us"),
            ("la", "whois.nic.la"), ("tv", "whois.nic.tv"), ("cc", "whois.nic.cc"),
            ("br", "whois.registro.br"), ("ru", "whois.tcinet.ru"), ("uk", "whois.nic.uk"),
            ("fr", "whois.nic.fr"), ("de", "whois.denic.de"), ("it", "whois.nic.it"),
            ("nl", "whois.domain-registry.nl"), ("cn", "whois.cnnic.cn"), ("in", "whois.registry.in"),
            ("to", "whois.tonic.to"), ("eu", "whois.eu"), ("at", "whois.nic.at"),
            ("be", "whois.dns.be"), ("ca", "whois.cira.ca"), ("ch", "whois.nic.ch"),
            ("dk", "whois.dk-hostmaster.dk"), ("fi", "whois.fi"), ("gr", "whois.ics.forth.gr"),
            ("hu", "whois.nic.hu"), ("ie", "whois.domainregistry.ie"), ("il", "whois.isoc.org.il"),
            ("jp", "whois.jprs.jp"), ("kr", "whois.kr"), ("lt", "whois.domreg.lt"),
            ("lv", "whois.nic.lv"), ("no", "whois.norid.no"), ("nz", "whois.srs.net.nz"),
            ("pl", "whois.dns.pl"), ("pt", "whois.dns.pt"), ("ro", "whois.rotld.ro"),
            ("se", "whois.iis.se"), ("sg", "whois.sgnic.sg"), ("sk", "whois.sk-nic.sk"),
            ("tw", "whois.twnic.net.tw"), ("ua", "whois.ua"), ("ai", "whois.nic.ai"),
            ("app", "whois.nic.google"), ("dev", "whois.nic.google"), ("blog", "whois.nic.blog"),
            ("shop", "whois.nic.shop"), ("online", "whois.nic.online"), ("site", "whois.nic.site"),
            ("xyz", "whois.nic.xyz"), ("top", "whois.nic.top"), ("club", "whois.nic.club"),
            ("space", "whois.nic.space"), ("website", "whois.nic.website"), ("fun", "whois.nic.fun"),
            ("store", "whois.nic.store"), ("agency", "whois.donuts.co"), ("life", "whois.donuts.co"),
            ("news", "whois.donuts.co"), ("social", "whois.donuts.co"), ("today", "whois.donuts.co"),
            ("world", "whois.donuts.co"), ("digital", "whois.donuts.co"), ("cafe", "whois.donuts.co"),
            ("company", "whois.donuts.co"), ("email", "whois.donuts.co"), ("expert", "whois.donuts.co"),
            ("fitness", "whois.donuts.co"), ("gallery", "whois.donuts.co"), ("group", "whois.donuts.co"),
            ("guru", "whois.donuts.co"), ("institute", "whois.donuts.co"), ("international", "whois.donuts.co"),
            ("llc", "whois.donuts.co"), ("ltd", "whois.donuts.co"), ("management", "whois.donuts.co"),
            ("media", "whois.donuts.co"), ("network", "whois.donuts.co"), ("ninja", "whois.donuts.co"),
            ("photography", "whois.donuts.co"), ("solutions", "whois.donuts.co"), ("studio", "whois.donuts.co"),
            ("technology", "whois.donuts.co"), ("tools", "whois.donuts.co"), ("university", "whois.donuts.co"),
            ("ventures", "whois.donuts.co"), ("works", "whois.donuts.co"),
        ])
    })
}

// Init servers at startup
fn _init_servers() {
    get_or_init_tld_servers();
}
