package openfind.ai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey val domain: String,
    val label: String = "",
    val intervalHours: Int = 24,
    val notifyEnabled: Boolean = true,
    val lastStatus: String? = null,
    val lastChecked: Long? = null
)
