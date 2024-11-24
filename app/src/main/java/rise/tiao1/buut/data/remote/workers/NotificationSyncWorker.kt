package rise.tiao1.buut.data.remote.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.runBlocking
import rise.tiao1.buut.domain.notification.useCases.GetNotificationsUseCase
import rise.tiao1.buut.domain.user.useCases.GetUserUseCase

@HiltWorker
class NotificationSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val getUserUseCase: GetUserUseCase,
) : Worker(context, params) {
    override fun doWork(): Result {
        return try {
            runBlocking {
                val currentUser = getUserUseCase.invoke()
                getNotificationsUseCase.invoke(currentUser.id!!)
                Result.success()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}