package openfind.ai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import openfind.ai.data.local.entity.HistoryEntity

@Dao
interface HistoryDao {
    @Query("SELECT * FROM search_history ORDER BY date DESC")
    fun getAll(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: HistoryEntity)

    @Delete
    suspend fun delete(entity: HistoryEntity)

    @Query("DELETE FROM search_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM search_history WHERE domain = :domain")
    suspend fun deleteByDomain(domain: String)

    @Query("DELETE FROM search_history")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM search_history")
    suspend fun count(): Int

    @Query("DELETE FROM search_history WHERE date = (SELECT MIN(date) FROM search_history)")
    suspend fun deleteOldest()
}
