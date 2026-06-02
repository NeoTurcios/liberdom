package openfind.ai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_domains")
data class SavedEntity(
    @PrimaryKey val domain: String,
    val status: String,
    val detail: String,
    val method: String,
    val date: Long = System.currentTimeMillis(),
    val ip: String? = null,
    val registrar: String? = null,
    val creationDate: String? = null,
    val sslActive: Boolean = false,
    val sslIssuer: String? = null,
    val cloudflare: String = "none",
    val nsServers: String = ""
)
