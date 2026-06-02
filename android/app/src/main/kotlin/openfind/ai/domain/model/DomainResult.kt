package openfind.ai.domain.model

data class DomainResult(
    val domain: String,
    val status: String,
    val detail: String,
    val method: String = "WHOIS",
    val ip: String? = null,
    val registrar: String? = null,
    val creationDate: String? = null,
    val sslActive: Boolean = false,
    val sslIssuer: String? = null,
    val cloudflare: String = CLOUDFLARE_NONE,
    val nsServers: List<String> = emptyList(),
    val brandScore: Float? = null,
    val brandFeedback: String? = null
) {
    companion object {
        const val STATUS_AVAILABLE = "available"
        const val STATUS_TAKEN = "taken"
        const val STATUS_UNKNOWN = "unknown"

        const val CLOUDFLARE_NONE = "none"
        const val CLOUDFLARE_ORANGE = "orange"
        const val CLOUDFLARE_GRAY = "gray"
    }
}

data class BulkStats(
    val total: Int = 0,
    val checked: Int = 0,
    val free: Int = 0,
    val taken: Int = 0
)

data class GeneratorItem(
    val domain: String,
    val status: String? = null,
    val brandScore: Float? = null
)
