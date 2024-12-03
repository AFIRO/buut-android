package rise.tiao1.buut.domain.notification.useCases

import rise.tiao1.buut.data.repositories.NotificationRepository
import rise.tiao1.buut.domain.notification.Notification
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {

    suspend operator fun invoke(userId: String): List<Notification> {
        try {
            val notifications = notificationRepository.getAllNotificationsFromUser(userId)
            return if (notifications.isNotEmpty())
                notifications
            else
                emptyList()
        } catch (e: Exception) {
            throw Exception("Error fetching notifications: ${e.message}")
        }
    }
}