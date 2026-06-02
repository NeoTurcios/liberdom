package openfind.ai.domain.model

data class WatchlistItem(
    val domain: String,
    val label: String,
    val addedAt: Long,
    val lastCheckAt: Long,
    val lastStatus: String,
    val notifyOnAvailable: Boolean,
    val checkIntervalHours: Int
)
