package rise.tiao1.buut.domain.booking.useCases

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import rise.tiao1.buut.data.remote.booking.BookingDTO
import rise.tiao1.buut.data.repositories.BookingRepository
import rise.tiao1.buut.domain.booking.TimeSlot
import rise.tiao1.buut.domain.user.Address
import rise.tiao1.buut.domain.user.User
import rise.tiao1.buut.domain.user.useCases.GetUserUseCase
import rise.tiao1.buut.utils.StreetType
import rise.tiao1.buut.utils.toApiDateString
import rise.tiao1.buut.utils.toLocalDateTimeFromApiString
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class CreateBookingsUseCaseTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)
    private val bookingRepository: BookingRepository = mockk()
    private val getUserUseCase: GetUserUseCase = mockk()
    private val today = LocalDateTime.now()
    private val createBookingsUseCase = CreateBookingsUseCase(bookingRepository, getUserUseCase)

    @Test
    fun createBookingsUseCase_returnsSuccess() = scope.runTest {
        val expected = getExpectedBookingDTO()
        coEvery { getUserUseCase() } returns getUser()
        coEvery { bookingRepository.createBooking(any()) } returns Unit

        createBookingsUseCase(getTimeSlot())

        coEvery { bookingRepository.createBooking(expected) }
    }

    private fun getTimeSlot(): TimeSlot {
        return TimeSlot(
            date = today.toApiDateString().toLocalDateTimeFromApiString(),
            slot= "Morning",
            available = true
        )
    }

    fun getUser() : User {
        return User(
            id = "fg",
            firstName = "TestFirstName",
            lastName = "TestLastName",
            email = "TestEmail",
            password = "TestPassword",
            phone = "TestPhone",
            dateOfBirth = LocalDateTime.of(1996, 8, 19, 0, 0),
            address = Address(StreetType.AFRIKALAAN, "TestHouseNumber", "TestBox"),
            roles = listOf()
        )
    }

    fun getExpectedBookingDTO(): BookingDTO {
        return BookingDTO(
            date = today.toApiDateString(),
            userId = "fg"
        )
    }

}