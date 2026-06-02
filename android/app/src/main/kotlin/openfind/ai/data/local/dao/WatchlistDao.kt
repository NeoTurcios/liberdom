package openfind.ai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import openfind.ai.data.local.entity.WatchlistEntity

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlist ORDER BY lastChecked DESC")
    fun getAll(): Flow<List<WatchlistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: WatchlistEntity)

    @Delete
    suspend fun delete(entity: WatchlistEntity)

    @Query("DELETE FROM watchlist WHERE domain = :domain")
    suspend fun deleteByDomain(domain: String)

    @Query("UPDATE watchlist SET notifyEnabled = :enabled WHERE domain = :domain")
    suspend fun updateNotify(domain: String, enabled: Boolean)

    @Query("UPDATE watchlist SET intervalHours = :hours WHERE domain = :domain")
    suspend fun updateInterval(domain: String, hours: Int)

    @Query("UPDATE watchlist SET lastStatus = :status, lastChecked = :timestamp WHERE domain = :domain")
    suspend fun updateStatus(domain: String, status: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM watchlist")
    suspend fun clearAll()
}
