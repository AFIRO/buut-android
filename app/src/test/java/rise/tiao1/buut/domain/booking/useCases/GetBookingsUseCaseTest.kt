package rise.tiao1.buut.domain.booking.useCases

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import rise.tiao1.buut.data.local.booking.LocalBooking
import rise.tiao1.buut.data.repositories.BookingRepository
import rise.tiao1.buut.domain.booking.Booking
import rise.tiao1.buut.domain.booking.toBooking
import rise.tiao1.buut.presentation.home.HomeScreenState
import rise.tiao1.buut.utils.toApiDateString
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class GetBookingsUseCaseTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)
    private val today = LocalDateTime.now()
    private val bookingRepository = mockk<BookingRepository>()
    private val USER_ID_WITH_BOOKINGS = "TestUser1"
    private val useCase = GetBookingsSortedByDateUseCase(
        bookingRepository = bookingRepository,
        homeScreenState = HomeScreenState()
    )

    @Test
    fun getBookings_IsReturningBookingsSortedByDateWhenUserHasBookings() = scope.runTest {
        coEvery { bookingRepository.getAllBookingsFromUser(any()) } returns getBookings()
        val bookings: List<Booking> = runBlocking { useCase.invoke(USER_ID_WITH_BOOKINGS) }
        val sortedBookings = bookings.sortedBy { it.date }
        assert(bookings == sortedBookings)
    }

    @Test
    fun getBookings_IsReturningEmptyListWhenUserHasNoBookings() = scope.runTest {
        coEvery { bookingRepository.getAllBookingsFromUser(any()) } returns emptyList()
        val bookings: List<Booking> = runBlocking { useCase.invoke(USER_ID_WITH_BOOKINGS) }
        assert(bookings.isEmpty())
    }

    fun getBookings(): List<Booking> {
        return getLocalBookings().map { it.toBooking() }
    }

    fun getLocalBookings(): List<LocalBooking> {
        return listOf(
            LocalBooking("1", today.toApiDateString(), "TestBoat", "TestBattery", "TestUser1"),
            LocalBooking("2", today.toApiDateString(), "TestBoat", "TestBattery", "TestUser2"),
            LocalBooking("3", today.toApiDateString(), "TestBoat", "TestBattery", "TestUser3"),
        )
    }
}