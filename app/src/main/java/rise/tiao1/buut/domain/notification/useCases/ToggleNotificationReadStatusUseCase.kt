package rise.tiao1.buut.domain.notification.useCases

import rise.tiao1.buut.data.repositories.NotificationRepository
import javax.inject.Inject

class ToggleNotificationReadStatusUseCase @Inject constructor(
        private val notificationRepository: NotificationRepository) {
            suspend operator fun invoke(notificationId: String, currentReadStatus: Boolean) {
                notificationRepository.toggleNotificationReadStatus(notificationId, currentReadStatus)
            }
}