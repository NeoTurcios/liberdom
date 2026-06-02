package openfind.ai.data.local.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters

class WatchlistWorkerFactory : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        if (workerClassName == WatchlistWorker::class.java.name) {
            return WatchlistWorker(appContext, workerParameters)
        }

        return try {
            val clazz = Class.forName(workerClassName)
                .asSubclass(ListenableWorker::class.java)
            val constructor = clazz.getDeclaredConstructor(
                Context::class.java,
                WorkerParameters::class.java
            )
            constructor.newInstance(appContext, workerParameters)
        } catch (e: Exception) {
            null
        }
    }
}
