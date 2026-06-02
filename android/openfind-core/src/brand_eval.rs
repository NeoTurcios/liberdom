use regex::Regex;

/// Brand score from heuristic evaluation.
use crate::BrandScore;

const COGNITIVE_KEYWORDS: &[&str] = &[
    "ai", "bot", "neo", "intel", "mind", "galaxy", "yi", "tech", "dev", "labs",
    "hub", "nexus", "core", "app", "flow", "think", "deep", "learn", "cloud", "net",
    "open", "find", "cyber", "data", "meta", "smart", "zen", "pixel", "layer", "matrix",
    "vertex", "vector", "crypt", "nova", "prime", "alpha", "beta", "sigma", "delta",
    "gamma", "theta", "ion", "atom", "wave", "force", "synth", "quant", "bit",
    "edge", "light", "dark", "echo", "pulse", "spark", "forge", "nest",
];

/// Evaluate a domain name for brand quality using heuristic scoring.
pub fn evaluate_brand(domain: &str) -> BrandScore {
    let parts: Vec<&str> = domain.rsplitn(2, '.').collect();
    let name = if parts.len() >= 2 { parts[1] } else { domain }.to_lowercase();
    let tld = if parts.len() >= 2 { parts[0] } else { "com" }.to_lowercase();

    let len_score = length_score(&name);
    let pron_score = pronunciability_score(&name);
    let mem_score = memorability_score(&name);
    let tld_score = tld_fitness_score(&name, &tld);

    let final_score = (pron_score * 0.25 + mem_score * 0.35 + len_score * 0.15 + tld_score * 0.25)
        .clamp(1.0, 10.0);

    let feedback = generate_feedback(final_score, &name);

    BrandScore {
        score: final_score,
        feedback,
        length_score: len_score,
        pronounce_score: pron_score,
        memory_score: mem_score,
        tld_score,
    }
}

fn length_score(name: &str) -> f32 {
    let len = name.len();
    if len <= 4 { 9.5 }
    else if len <= 6 { 8.5 }
    else if len <= 8 { 7.0 }
    else if len <= 10 { 5.5 }
    else if len <= 12 { 4.0 }
    else { 2.5 }
}

fn pronunciability_score(name: &str) -> f32 {
    let vowels = name.chars().filter(|c| "aeiou".contains(*c)).count();
    let consonants = name.chars().filter(|c| "bcdfghjklmnpqrstvwxyz".contains(*c)).count();

    let ratio = if consonants > 0 {
        vowels as f32 / consonants as f32
    } else {
        1.0
    };

    let mut score = if (0.35..=0.55).contains(&ratio) { 8.5 }
    else if (0.2..=0.7).contains(&ratio) { 6.5 }
    else { 4.0 };

    let has_cluster = Regex::new(r"[bcdfghjklmnpqrstvwxyz]{3,}")
        .map(|re| re.is_match(name))
        .unwrap_or(false);
    if has_cluster {
        score -= 2.0;
    }

    let has_rare = Regex::new(r"[qxzwj][qxzwyj]")
        .map(|re| re.is_match(name))
        .unwrap_or(false);
    if has_rare {
        score -= 2.0;
    }

    if name.contains("ee") || name.contains("oo") || name.contains("aa") {
        score += 0.5;
    }

    score.clamp(1.0, 10.0)
}

fn memorability_score(name: &str) -> f32 {
    let mut score = 7.0;

    for kw in COGNITIVE_KEYWORDS {
        if name.contains(kw) {
            score += 1.5;
            break;
        }
    }

    if name.contains('-') { score -= 2.0; }
    if name.chars().any(|c| c.is_ascii_digit()) { score -= 1.5; }

    score.clamp(1.0, 10.0)
}

fn tld_fitness_score(name: &str, tld: &str) -> f32 {
    match tld {
        "ai" => {
            let ai_words = ["mind", "brain", "bot", "intel", "neural", "deep", "learn", "chat", "net", "open", "find", "labs"];
            if ai_words.iter().any(|w| name.contains(w)) { 10.0 } else { 9.0 }
        }
        "com" => 9.8,
        "io" => {
            let tech_words = ["tech", "dev", "labs", "code", "cyber", "flow", "hub", "sys"];
            if tech_words.iter().any(|w| name.contains(w)) { 10.0 } else { 9.0 }
        }
        "co" => 8.5,
        "net" => 7.8,
        "org" => 7.5,
        _ => 6.5,
    }
}

fn generate_feedback(score: f32, _name: &str) -> String {
    if score >= 9.0 {
        "S-Tier brand name. Ultra-premium, short, and highly memorable."
    } else if score >= 7.5 {
        "Strong brand potential. Good balance of length and memorability."
    } else if score >= 5.5 {
        "Commercially acceptable. Consider shorter alternatives for premium appeal."
    } else if score >= 3.5 {
        "Below average memorability. Try the name generator for better options."
    } else {
        "Complex and hard to remember. Highly recommend using the name generator."
    }.to_string()
}
