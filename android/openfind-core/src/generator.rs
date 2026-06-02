use std::collections::HashSet;

/// Generate domain name ideas from a keyword and selected TLDs.
pub fn generate_names(keyword: &str, tlds: &[String], max_count: u32) -> Vec<String> {
    let word = keyword.trim().to_lowercase();
    if word.len() < 2 {
        return vec![];
    }

    let prefixes = [
        "get", "try", "go", "my", "the", "we", "neo", "mega", "alpha",
        "ultra", "swift", "nova", "apex", "pulse", "core",
    ];
    let suffixes = [
        "ai", "hub", "flow", "tech", "ify", "ly", "base", "smart", "labs",
        "sys", "zone", "net", "io", "app", "pro", "zen", "box", "kit",
    ];

    let mut results = HashSet::new();

    for tld in tlds.iter().filter(|t| !t.is_empty()) {
        results.insert(format!("{}.{}", word, tld));

        for prefix in &prefixes {
            let candidate = format!("{}{}.{}", prefix, word, tld);
            results.insert(candidate);
            if results.len() as u32 >= max_count {
                return results.into_iter().collect();
            }
        }

        for suffix in &suffixes {
            let candidate = format!("{}{}.{}", word, suffix, tld);
            results.insert(candidate);
            if results.len() as u32 >= max_count {
                return results.into_iter().collect();
            }
        }

        // AI-style patterns
        results.insert(format!("{}ai.{}", word, tld));
        results.insert(format!("{}bot.{}", word, tld));
        results.insert(format!("my{}ai.{}", word, tld));
        results.insert(format!("get{}.{}", word, tld));

        if results.len() as u32 >= max_count {
            return results.into_iter().collect();
        }
    }

    let mut final_results: Vec<String> = results.into_iter().collect();
    if final_results.len() > max_count as usize {
        final_results.truncate(max_count as usize);
    }
    final_results
}
