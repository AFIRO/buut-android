package rise.tiao1.buut

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import rise.tiao1.buut.data.remote.workers.BookingSyncWorker
import rise.tiao1.buut.data.remote.workers.NotificationSyncWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class BuutApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory : HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        scheduleBackgroundSyncTasks()
    }

    private fun scheduleBackgroundSyncTasks() {
        val workManager = WorkManager.getInstance(this)

        val bookingSyncWorkRequest = PeriodicWorkRequestBuilder<BookingSyncWorker>(
            5, TimeUnit.MINUTES
        ).setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        ).build()


        workManager.enqueueUniquePeriodicWork(
            "BookingSyncWork",
            ExistingPeriodicWorkPolicy.KEEP,
            bookingSyncWorkRequest
        )

        val notificationSyncWorkRequest = PeriodicWorkRequestBuilder<NotificationSyncWorker>(
            1, TimeUnit.MINUTES
        ).setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        ).build()

        workManager.enqueueUniquePeriodicWork(
            "NotificationSyncWork",
            ExistingPeriodicWorkPolicy.KEEP,
            notificationSyncWorkRequest
        )
    }
}

