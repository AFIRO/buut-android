package rise.tiao1.buut.data.remote.notification

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import rise.tiao1.buut.data.local.notification.LocalNotification
import rise.tiao1.buut.utils.toApiDateString
import java.time.LocalDateTime


class NotificationDTOKtTest{
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)
    private val today: LocalDateTime = LocalDateTime.now()

    @Test
    fun notificationDTO_toLocalNotification_mapsCorrectly() = scope.runTest{
        val initial = getNotificationDTO()
        val expected = getLocalNotification()
        val actual = initial.toLocalNotification("TestUserId")
        assertEquals(expected, actual)
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

    private fun getNotificationDTO() = NotificationDTO(
        notificationId = "1",
        title = "title",
        message = "message",
        isRead = true,
        type = "type",
        createdAt = today.toApiDateString(),
        relatedEntityId = ""
    )
}