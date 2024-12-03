package rise.tiao1.buut.domain.notification.useCases

import rise.tiao1.buut.data.repositories.NotificationRepository
import rise.tiao1.buut.domain.notification.Notification
import rise.tiao1.buut.presentation.home.HomeScreenState
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val homeScreenState: HomeScreenState
){

    suspend operator fun invoke(userId: String): List<Notification> {
        val notifications = notificationRepository.getAllNotificationsFromUser(userId)
        if (notifications.isNotEmpty()){
            homeScreenState.notifications = notifications
        }
        return if (notifications.isNotEmpty())

            notifications
        else
            emptyList()
    }
}