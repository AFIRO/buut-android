package rise.tiao1.buut.data.repositories

import android.content.Context
import android.net.ConnectivityManager
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import rise.tiao1.buut.data.di.IoDispatcher
import rise.tiao1.buut.data.local.notification.NotificationDao
import rise.tiao1.buut.data.remote.notification.NotificationApiService
import rise.tiao1.buut.data.remote.notification.NotificationIsReadDTO
import rise.tiao1.buut.data.remote.notification.toLocalNotification
import rise.tiao1.buut.domain.notification.Notification
import rise.tiao1.buut.domain.notification.toNotification
import rise.tiao1.buut.utils.NetworkConnectivityChecker
import rise.tiao1.buut.utils.toApiErrorMessage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationDao: NotificationDao,
    private val apiService: NotificationApiService,
    private val networkConnectivityChecker: NetworkConnectivityChecker,
    @IoDispatcher private val dispatcher:
    CoroutineDispatcher
) {

    suspend fun getAllNotificationsFromUser(userId: String): List<Notification>  =
        withContext(dispatcher) {
            try {
                if (networkConnectivityChecker.isNetworkAvailable()) {
                    refreshCache(userId)
                }
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> { throw Exception(e.toApiErrorMessage())}
                    else -> throw Exception(e.message)
                }
            }
            return@withContext  notificationDao.getNotificationsByUserId(userId).map { it.toNotification(userId) }.sortedByDescending { it.createdAt }
        }

    suspend fun toggleNotificationReadStatus(notificationId: String, currentStatus: Boolean)  =
        withContext(dispatcher) {
            try {
                if (networkConnectivityChecker.isNetworkAvailable()) {
                    apiService.markNotificationAsRead(
                        NotificationIsReadDTO(
                            notificationId,
                            !currentStatus
                        )
                    )
                    var notificationToUpdate = notificationDao.getNotificationById(notificationId)
                    notificationToUpdate =
                        notificationToUpdate.copy(isRead = !notificationToUpdate.isRead!!)
                    notificationDao.insertNotification(notificationToUpdate)
                }

            } catch (e: Exception) {
                when (e) {
                    is HttpException -> { throw Exception(e.toApiErrorMessage())}
                    else -> throw Exception(e.message)
                }
            }
        }

    private suspend fun refreshCache(userId: String) {
        val remoteNotifications = apiService.getAllNotificationsFromUser(userId)
        notificationDao.insertAllNotifications(remoteNotifications.map {it.toLocalNotification(userId)})
    }
}