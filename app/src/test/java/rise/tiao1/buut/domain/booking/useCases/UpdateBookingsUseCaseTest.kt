package rise.tiao1.buut.domain.booking.useCases

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import rise.tiao1.buut.data.remote.booking.BookingDTO
import rise.tiao1.buut.data.remote.booking.BookingUpdateDTO
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
class UpdateBookingsUseCaseTest{
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)
    private val bookingRepository: BookingRepository = mockk()
    private val getUserUseCase: GetUserUseCase = mockk()
    private val today = LocalDateTime.now()
    private val updateBookingsUseCase = UpdateBookingsUseCase(bookingRepository, getUserUseCase)
    private val testBookingId = "testBookingId"

    @Test
    fun updateBookingsUseCase_returnsSuccess() = scope.runTest {
        coEvery { getUserUseCase() } returns getUser()
        coEvery { bookingRepository.updateBooking(testBookingId, getUser().id.toString(), getExpectedBookingDTO()) } returns Unit

        updateBookingsUseCase(testBookingId, getTimeSlot())

        coVerify { bookingRepository.updateBooking(testBookingId, getUser().id.toString(), getExpectedBookingDTO()) }
    }

    private fun getTimeSlot(): TimeSlot {
        return TimeSlot(
            date = today.plusDays(1).toApiDateString().toLocalDateTimeFromApiString(),
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

    fun getExpectedBookingDTO(): BookingUpdateDTO {
        return BookingUpdateDTO(
            id = testBookingId,
            date = today.plusDays(1).toApiDateString(),
        )
    }


}