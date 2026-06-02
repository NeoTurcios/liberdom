package openfind.ai.data.repository

import openfind.ai.data.local.dao.WatchlistDao
import openfind.ai.data.local.entity.WatchlistEntity
import kotlinx.coroutines.flow.Flow

class WatchlistRepository(private val watchlistDao: WatchlistDao) {

    val watchlist: Flow<List<WatchlistEntity>> = watchlistDao.getAll()

    suspend fun add(domain: String, label: String, intervalHours: Int) {
        watchlistDao.insert(
            WatchlistEntity(domain = domain, label = label, intervalHours = intervalHours)
        )
    }

    suspend fun remove(domain: String) {
        watchlistDao.deleteByDomain(domain)
    }

    suspend fun toggleNotify(domain: String, enabled: Boolean) {
        watchlistDao.updateNotify(domain, enabled)
    }

    suspend fun changeInterval(domain: String, hours: Int) {
        watchlistDao.updateInterval(domain, hours)
    }

    suspend fun updateStatus(domain: String, status: String, timestamp: Long = System.currentTimeMillis()) {
        watchlistDao.updateStatus(domain, status, timestamp)
    }

    suspend fun getDomainsNeedingCheck(): List<openfind.ai.data.local.entity.WatchlistEntity> {
        val all = mutableListOf<openfind.ai.data.local.entity.WatchlistEntity>()
        watchlistDao.getAll().collect { list -> all.addAll(list); return@collect }
        val now = System.currentTimeMillis()
        return all.filter { entity ->
            val lastChecked = entity.lastChecked ?: 0L
            (now - lastChecked) >= entity.intervalHours * 3600_000L
        }
    }
}
