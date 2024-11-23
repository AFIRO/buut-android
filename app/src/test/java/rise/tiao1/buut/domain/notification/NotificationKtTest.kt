package rise.tiao1.buut.domain.notification

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import org.junit.Assert.assertEquals
import org.junit.Test
import rise.tiao1.buut.data.local.notification.LocalNotification
import rise.tiao1.buut.utils.NotificationType
import rise.tiao1.buut.utils.toApiDateString
import rise.tiao1.buut.utils.toLocalDateTimeFromApiString
import java.time.LocalDateTime


class NotificationKtTest{
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)
    private val today: LocalDateTime = LocalDateTime.now()
    private val testId = "TestId"

    @Test
    fun toNotificationFromLocal_returnsCorrectNotification() {
        val actual = getLocalNotification().toNotification(testId)
        val expected = getNotification()
        assertEquals(actual, expected)
    }

    private fun getLocalNotification() = LocalNotification(
        id = "1",
        userId = "TestUserId",
        title = "title",
        message = "message",
        isRead = true,
        type = "type",
        createdAt = today.toApiDateString(),
        relatedEntityId = ""
    )

    private fun getNotification() =
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
}