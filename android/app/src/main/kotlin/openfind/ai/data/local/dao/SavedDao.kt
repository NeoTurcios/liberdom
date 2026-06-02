package openfind.ai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import openfind.ai.data.local.entity.SavedEntity

@Dao
interface SavedDao {
    @Query("SELECT * FROM saved_domains ORDER BY date DESC")
    fun getAll(): Flow<List<SavedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SavedEntity)

    @Delete
    suspend fun delete(entity: SavedEntity)

    @Query("DELETE FROM saved_domains WHERE domain = :domain")
    suspend fun deleteByDomain(domain: String)

    @Query("DELETE FROM saved_domains")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM saved_domains")
    suspend fun count(): Int

    @Query("SELECT * FROM saved_domains WHERE domain = :domain LIMIT 1")
    suspend fun getByDomain(domain: String): SavedEntity?

    @Query("DELETE FROM saved_domains WHERE date = (SELECT MIN(date) FROM saved_domains)")
    suspend fun deleteOldest()
}
