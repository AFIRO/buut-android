package rise.tiao1.buut.data.repositories

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import rise.tiao1.buut.data.local.notification.LocalNotification
import rise.tiao1.buut.data.local.notification.NotificationDao
import rise.tiao1.buut.data.remote.notification.NotificationApiService
import rise.tiao1.buut.data.remote.notification.NotificationDTO
import rise.tiao1.buut.data.remote.notification.NotificationIsReadDTO
import rise.tiao1.buut.domain.notification.Notification
import rise.tiao1.buut.utils.NotificationType
import rise.tiao1.buut.utils.toApiDateString
import rise.tiao1.buut.utils.toLocalDateTimeFromApiString
import java.time.LocalDateTime


class NotificationRepositoryTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)
    private val today: LocalDateTime = LocalDateTime.now()
    private val notificationDao = mockk<NotificationDao>()
    private val apiService = mockk<NotificationApiService>()
    private val repository = NotificationRepository(notificationDao, apiService, dispatcher)
    private val testId = "TestId"
    private val testError = "TestError"

    @Test
    fun getAllNotificationsFromUser_returnsCorrectList() = scope.runTest {
        coEvery { notificationDao.getNotificationsByUserId(testId) } returns getLocalNotifications()
        coEvery { apiService.getAllNotificationsFromUser(testId) } returns getNotificationDTOs()
        coEvery { notificationDao.insertAllNotifications(any()) } returns Unit
        var actual =  repository.getAllNotificationsFromUser(testId)
        var expected = getNotifications()
        assertEquals(actual,expected)
        coVerify { notificationDao.getNotificationsByUserId(testId) }
        coVerify { apiService.getAllNotificationsFromUser(testId) }
        coVerify { notificationDao.insertAllNotifications(any()) }
}

    @Test
    fun getAllNotificationsFromUser_ApiError_HandlesCorrectly() = scope.runTest {
        coEvery { notificationDao.getNotificationsByUserId(testId) } returns getLocalNotifications()
        coEvery { apiService.getAllNotificationsFromUser(testId) } throws Exception(testError)
        val actual = runCatching { repository.getAllNotificationsFromUser(testId) }
        assertEquals(actual.isFailure, true)
        assertEquals(actual.exceptionOrNull()?.message, testError)
        coVerify { apiService.getAllNotificationsFromUser(testId) }
        coVerify (exactly = 0) {  notificationDao.getNotificationsByUserId(testId) }
        coVerify (exactly = 0) {  notificationDao.insertAllNotifications(any()) }
    }

    @Test
    fun getAllNotificationsFromUser_RoomErrorDuringGet_HandlesCorrectly() = scope.runTest {
        coEvery { apiService.getAllNotificationsFromUser(testId) } returns getNotificationDTOs()
        coEvery { notificationDao.getNotificationsByUserId(testId) } returns getLocalNotifications()
        coEvery { notificationDao.insertAllNotifications(any()) } throws Exception(testError)
        val actual = runCatching { repository.getAllNotificationsFromUser(testId) }
        assertEquals(actual.isFailure, true)
        assertEquals(actual.exceptionOrNull()?.message, testError)
        coVerify { apiService.getAllNotificationsFromUser(testId) }
        coVerify {  notificationDao.insertAllNotifications(any()) }
        coVerify (exactly = 0) {  notificationDao.getNotificationsByUserId(testId) }
    }

    @Test
    fun toggleNotificationReadStatus_setsCorrectly() = scope.runTest {
        var expected = getLocalNotifications()[0].copy(isRead = false)

        coEvery { apiService.markNotificationAsRead(getNotificatioIsReadDto()) } returns Unit
        coEvery { notificationDao.getNotificationById(testId) } returns getLocalNotifications()[0]
        coEvery { notificationDao.insertNotification(expected) } returns Unit
        repository.toggleNotificationReadStatus(testId, false)
        coVerify { apiService.markNotificationAsRead(getNotificatioIsReadDto()) }
        coVerify { notificationDao.getNotificationById(testId) }
        coVerify { notificationDao.insertNotification(expected) }
    }

    @Test
    fun toggleNotificationReadStatus_ApiError_HandlesCorrectly() = scope.runTest {
        coEvery { apiService.markNotificationAsRead(getNotificatioIsReadDto()) } throws Exception(testError)
        val actual = runCatching { repository.toggleNotificationReadStatus(testId, false) }
        assertEquals(actual.isFailure, true)
        assertEquals(actual.exceptionOrNull()?.message, testError)
        coVerify { apiService.markNotificationAsRead(getNotificatioIsReadDto()) }
        coVerify (exactly = 0) { notificationDao.getNotificationById(testId) }
        coVerify (exactly = 0) { notificationDao.insertNotification(any()) }
    }

    @Test
    fun toggleNotificationReadStatus_RoomError_HandlesCorrectly() = scope.runTest {
        coEvery { apiService.markNotificationAsRead(getNotificatioIsReadDto()) } returns Unit
        coEvery { notificationDao.getNotificationById(testId) } returns getLocalNotifications()[0]
        coEvery { notificationDao.insertNotification(any()) } throws Exception(testError)
        val actual = runCatching { repository.toggleNotificationReadStatus(testId, false) }
        assertEquals(actual.isFailure, true)
        assertEquals(actual.exceptionOrNull()?.message, testError)
        coVerify { apiService.markNotificationAsRead(getNotificatioIsReadDto()) }
        coVerify { notificationDao.getNotificationById(testId) }
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

    private fun getLocalNotifications() = listOf(
        LocalNotification(
        id = "1",
        userId = "TestUserId",
        title = "title",
        message = "message",
        isRead = true,
        type = "General",
        createdAt = today.toApiDateString(),
        relatedEntityId = ""
    )
    )

    private fun getNotificationDTOs() = listOf(
        NotificationDTO(
        notificationId = "1",
        title = "title",
        message = "message",
        isRead = true,
        type = "General",
        createdAt = today.toApiDateString(),
        relatedEntityId = ""
    ))

    private fun getNotificatioIsReadDto() =
        NotificationIsReadDTO(
            notificationId = testId,
            isRead = true
        )

}