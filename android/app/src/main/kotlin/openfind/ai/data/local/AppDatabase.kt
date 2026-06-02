package openfind.ai.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import openfind.ai.data.local.dao.HistoryDao
import openfind.ai.data.local.dao.SavedDao
import openfind.ai.data.local.dao.WatchlistDao
import openfind.ai.data.local.entity.HistoryEntity
import openfind.ai.data.local.entity.SavedEntity
import openfind.ai.data.local.entity.WatchlistEntity

@Database(
    entities = [SavedEntity::class, HistoryEntity::class, WatchlistEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedDao(): SavedDao
    abstract fun historyDao(): HistoryDao
    abstract fun watchlistDao(): WatchlistDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "openfind_database"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }

        fun build(context: Context): AppDatabase = getInstance(context)
    }
}
