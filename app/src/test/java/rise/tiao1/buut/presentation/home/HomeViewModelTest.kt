package rise.tiao1.buut.presentation.home

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import rise.tiao1.buut.domain.booking.Booking
import rise.tiao1.buut.domain.booking.useCases.GetBookingsSortedByDateUseCase
import rise.tiao1.buut.domain.notification.Notification
import rise.tiao1.buut.domain.notification.useCases.GetNotificationsUseCase
import rise.tiao1.buut.domain.notification.useCases.ToggleNotificationReadStatusUseCase
import rise.tiao1.buut.domain.user.User
import rise.tiao1.buut.domain.user.useCases.GetUserUseCase
import rise.tiao1.buut.utils.NotificationType
import java.time.LocalDateTime

class HomeViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)
    private val getUserUseCase : GetUserUseCase = mockk()
    private val getBookingsUseCase: GetBookingsSortedByDateUseCase = mockk()
    private val getNotificationsUseCase: GetNotificationsUseCase = mockk()
    private val toggleNotificationReadStatusUseCase: ToggleNotificationReadStatusUseCase = mockk()
    private val today: LocalDateTime = LocalDateTime.now()
    private val testId = "TestId"

    @Test
    fun initialState_isProduced() = scope.runTest {
        val viewModel = getViewModel()
        val initialState = viewModel.state.value
        assert(initialState == HomeScreenState())
    }

    @Test
    fun initializeData_getDataCorrectly() = scope.runTest {
        coEvery { getUserUseCase.invoke() } returns getUser()
        coEvery { getBookingsUseCase.invoke(testId) } returns getBookings()
        coEvery { getNotificationsUseCase.invoke(testId) } returns getNotifications()
        val viewModel = getViewModel()
        dispatcher.scheduler.advanceUntilIdle()
        val initialState = viewModel.state.value
        assertEquals(initialState.user, getUser())
        assertEquals(initialState.bookings, getBookings())
        assertEquals(initialState.notifications, getNotifications())
        assertEquals(initialState.unReadNotifications, getNotifications().count())
        coVerify { getUserUseCase.invoke() }
        coVerify { getBookingsUseCase.invoke(testId) }
        coVerify { getNotificationsUseCase.invoke(testId)}
    }

    private fun getViewModel(): HomeViewModel {
        return HomeViewModel(getUserUseCase, getBookingsUseCase, getNotificationsUseCase, toggleNotificationReadStatusUseCase, dispatcher)
    }

    private fun getUser(): User {
        return User(id = testId, firstName = "TestFirstName", lastName = "TestLastName", email = "TestEmail", password = "TestPassword", phone = "TestPhone", dateOfBirth = null, address = null)
    }

    private fun getBookings(): List<Booking> {
        return listOf(Booking(testId, today, null, null))
    }

    private fun getNotifications(): List<Notification> {
        return listOf(Notification(testId, "TestUserId", "TestTitle", "TestMessage", false, NotificationType.GENERAL, today, "TestRelatedEntityId"))

    }
}