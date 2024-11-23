package rise.tiao1.buut.domain.notification.useCases

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import rise.tiao1.buut.data.repositories.NotificationRepository
import rise.tiao1.buut.domain.notification.Notification
import rise.tiao1.buut.utils.NotificationType
import rise.tiao1.buut.utils.toApiDateString
import rise.tiao1.buut.utils.toLocalDateTimeFromApiString
import java.time.LocalDateTime


class GetNotificationsUseCaseTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)
    private val today = LocalDateTime.now()
    private val notificationRepository = mockk<NotificationRepository>()
    private val testId = "TestId"
    private val useCase = GetNotificationsUseCase(
        notificationRepository = notificationRepository
    )


    @Test
    fun getNotifications_IsReturningNotificationsWhenUserHasNotifications() = scope.runTest {
        coEvery { notificationRepository.getAllNotificationsFromUser(testId) } returns getNotifications()
        val actual: List<Notification> = useCase.invoke(testId)
        assertEquals(actual, getNotifications())
        coVerify { notificationRepository.getAllNotificationsFromUser(testId) }
    }

    @Test
    fun getNotifications_returnsEmptyListWhenUserHasNoNotifications() = scope.runTest {
        coEvery { notificationRepository.getAllNotificationsFromUser(testId) } returns emptyList()
        val actual: List<Notification> = useCase.invoke(testId)
        assertEquals(actual, emptyList<Notification>())
        coVerify { notificationRepository.getAllNotificationsFromUser(testId) }
    }


    private fun getNotifications() = listOf(
        Notification(
            notificationId = "1",
            userId = testId,
            title = "title",
            message = "message",
            isRead = true,
            type = NotificationType.GENERAL,
            createdAt = today.toApiDateString().toLocalDateTimeFromApiString(),
            relatedEntityId = ""
        )
    )
}