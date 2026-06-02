package openfind.ai.data.local.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import openfind.ai.OpenFindApp
import openfind.ai.data.local.entity.WatchlistEntity
import openfind.ai.data.native.OpenfindNative
import openfind.ai.data.repository.WatchlistRepository
import openfind.ai.domain.model.DomainResult
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class WatchlistWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val watchlistRepository: WatchlistRepository by inject()

    override suspend fun doWork(): Result {
        val needsCheck = watchlistRepository.getDomainsNeedingCheck()

        if (needsCheck.isEmpty()) {
            Log.d(TAG, "No domains need checking")
            return Result.success()
        }

        Log.i(TAG, "Checking ${needsCheck.size} watchlist domains")

        for (entity in needsCheck) {
            try {
                val nativeResult = OpenfindNative.checkDomainSafe(entity.domain, false)
                    ?: DomainResult(
                        domain = entity.domain,
                        status = DomainResult.STATUS_UNKNOWN,
                        detail = "Native engine unavailable",
                        ip = null,
                        registrar = null,
                        creationDate = null,
                        method = "WatchlistWorker",
                        sslActive = false,
                        sslIssuer = null,
                        cloudflare = DomainResult.CLOUDFLARE_NONE,
                        nsServers = emptyList()
                )

                watchlistRepository.updateStatus(entity.domain, nativeResult.status, System.currentTimeMillis())

                if (entity.notifyEnabled &&
                    nativeResult.status == DomainResult.STATUS_AVAILABLE &&
                    entity.lastStatus != DomainResult.STATUS_AVAILABLE
                ) {
                    showNotification(entity)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to check ${entity.domain}", e)
                watchlistRepository.updateStatus(entity.domain, DomainResult.STATUS_UNKNOWN, System.currentTimeMillis())
            }

            delay(500L)
        }

        return Result.success()
    }

    private fun showNotification(entity: WatchlistEntity) {
        val notificationManager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = android.app.NotificationChannel(
            CHANNEL_ID,
            "Domain Watchlist",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Alerts when watched domains become available"
            enableVibration(true)
        }
        notificationManager.createNotificationChannel(channel)

        val labelSuffix = if (entity.label.isNotBlank()) " (${entity.label})" else ""
        val title = "Domain Available!"
        val contentText = "${entity.domain}$labelSuffix is now available!"
        val bigText = "${entity.domain} is now available for registration!\n${if (entity.label.isNotBlank()) "Label: ${entity.label}" else ""}"

        val launchIntent = applicationContext.packageManager
            .getLaunchIntentForPackage(applicationContext.packageName)
            ?.apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            entity.domain.hashCode(),
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(contentText)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(bigText)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(entity.domain.hashCode(), notification)
    }

    companion object {
        const val CHANNEL_ID = "watchlist_alerts"
        const val WORK_NAME = "watchlist_periodic_check"
        private const val TAG = "WatchlistWorker"

        fun enqueuePeriodic(intervalHours: Int = 6) {
            val request = PeriodicWorkRequestBuilder<WatchlistWorker>(
                intervalHours.toLong(), TimeUnit.HOURS,
                15, TimeUnit.MINUTES
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    30, TimeUnit.SECONDS
                )
                .build()

            val appContext = OpenFindApp.getInstance()
            WorkManager.getInstance(appContext)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
        }

        fun cancelPeriodic() {
            val appContext = OpenFindApp.getInstance()
            WorkManager.getInstance(appContext)
                .cancelUniqueWork(WORK_NAME)
        }
    }
}
