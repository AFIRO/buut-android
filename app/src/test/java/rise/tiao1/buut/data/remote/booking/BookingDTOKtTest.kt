package rise.tiao1.buut.data.remote.booking

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import rise.tiao1.buut.data.local.booking.LocalBooking
import rise.tiao1.buut.data.remote.user.dto.UserBatteryDTO
import rise.tiao1.buut.domain.booking.Booking
import rise.tiao1.buut.domain.user.toLocalUser
import java.time.LocalDateTime


@ExperimentalCoroutinesApi
class BookingDTOKtTest{
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun whenToLocalBookingIsCalled_returnsCorrectLocalBooking() = scope.runTest{
        val booking = getBookingDTO()
        val expected = getLocalBooking()
        val result = booking.toLocalBooking("TestUserId")
        assert(result.equals(expected))
    }


    fun getBookingDTO() : BookingDTO {
        return BookingDTO(
            id = "1",
            date = "TestDate",
            boat = getBoatDTO(),
            battery = getBatteryDTO())
    }

    fun getLocalBooking() : LocalBooking {
        return LocalBooking(
            id = "1",
            date = "TestDate",
            boat = "TestBoat",
            battery = "TestBattery",
            batteryUserFirstName = "TestUserName",
            batteryUserLastName = "TestLastName",
            batteryUserEmail = "TestEmail",
            batteryUserPhoneNumber = "TestPhone",
            userId = "TestUserId"
        )
    }

    fun getBoatDTO() : BoatDTO {
        return BoatDTO(
            name = "TestBoat")
    }

    fun getBatteryDTO() : BatteryDTO {
        return BatteryDTO(
            name = "TestBattery",
            currentUser = UserBatteryDTO(
                "TestUserName",
                lastName = "TestLastName",
                email = "TestEmail",
                phoneNumber = "TestPhone",
            )
        )
    }

}

