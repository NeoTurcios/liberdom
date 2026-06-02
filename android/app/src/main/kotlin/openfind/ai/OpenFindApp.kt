package openfind.ai

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.res.AssetManager
import android.os.Build
import androidx.work.Configuration
import androidx.work.WorkManager
import openfind.ai.data.ai.DomainAiEvaluator
import openfind.ai.data.local.worker.WatchlistWorkerFactory
import openfind.ai.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileOutputStream
import java.nio.MappedByteBuffer

class OpenFindApp : Application(), Configuration.Provider {

    private var tfliteInterpreter: Interpreter? = null

    override fun onCreate() {
        super.onCreate()
        try {
            initKoin()
            initNativeLibrary()
            initWorkManager()
            createNotificationChannels()
            initAiIfEnabled()
        } catch (e: Exception) {
            android.util.Log.e("OpenFindApp", "Startup crash: ${e.message}", e)
            throw e
        }
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@OpenFindApp)
            modules(appModule, module {
                single { DomainAiEvaluator(androidContext()) }
            })
        }
    }

    private fun initNativeLibrary() {
        try {
            System.loadLibrary("openfind_core")
        } catch (e: UnsatisfiedLinkError) {
            android.util.Log.w("OpenFindApp", "Native library openfind_core not available: ${e.message}")
        }
    }

    private fun initWorkManager() {
        // Configuration.Provider interface handles WorkManager init automatically.
        // No need to call WorkManager.initialize() explicitly.
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setWorkerFactory(WatchlistWorkerFactory())
            .build()

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Watchlist Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for watchlist domain availability changes"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun initAiIfEnabled() {
        val prefs = getSharedPreferences("openfind_settings", MODE_PRIVATE)
        val aiEnabled = prefs.getBoolean("ai_enabled", false)

        if (aiEnabled) {
            try {
                loadTfLiteModel()
            } catch (e: Exception) {
                android.util.Log.e("OpenFindApp", "Failed to load TFLite model: ${e.message}")
            }
        }
    }

    private fun loadTfLiteModel() {
        val modelFile = copyAssetToCache("openfind_model.tflite")
        if (modelFile != null) {
            tfliteInterpreter = Interpreter(modelFile)
            android.util.Log.i("OpenFindApp", "TFLite model loaded successfully")
        } else {
            android.util.Log.w("OpenFindApp", "TFLite model file not found in assets")
        }
    }

    private fun copyAssetToCache(assetName: String): MappedByteBuffer? {
        return try {
            val assetManager: AssetManager = assets
            val fileDescriptor = assetManager.openFd(assetName)
            val inputStream = fileDescriptor.createInputStream()
            val cacheFile = File(cacheDir, assetName)

            FileOutputStream(cacheFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }

            inputStream.close()
            fileDescriptor.close()

            val result = java.io.RandomAccessFile(cacheFile, "r")
            result.channel.map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0, result.length())
        } catch (e: Exception) {
            android.util.Log.e("OpenFindApp", "Error copying asset $assetName: ${e.message}")
            null
        }
    }

    override fun onTerminate() {
        tfliteInterpreter?.close()
        super.onTerminate()
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "watchlist_alert"

        @Volatile
        private var instanceRef: OpenFindApp? = null

        fun getInstance(): OpenFindApp =
            instanceRef ?: throw IllegalStateException("OpenFindApp not initialized")

        val isInitialized: Boolean get() = instanceRef != null
    }

    override fun attachBaseContext(base: android.content.Context?) {
        super.attachBaseContext(base)
        instanceRef = this
    }
}
