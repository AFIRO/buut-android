package rise.tiao1.buut.data.remote.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.runBlocking
import rise.tiao1.buut.domain.booking.useCases.GetBookingsSortedByDateUseCase
import rise.tiao1.buut.domain.user.useCases.GetUserUseCase

@HiltWorker
class BookingSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val getBookingsUseCase: GetBookingsSortedByDateUseCase,
    private val getUserUseCase: GetUserUseCase,
) : Worker(context, params) {
    override fun doWork(): Result {
        return try {
            runBlocking {
                val currentUser = getUserUseCase.invoke()
                getBookingsUseCase.invoke(currentUser.id!!)
                Log.i("BookingSyncWorker", "Bookings synced successfully")
                Result.success()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}