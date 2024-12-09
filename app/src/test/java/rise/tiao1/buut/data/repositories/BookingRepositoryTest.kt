package rise.tiao1.buut.data.repositories

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.any
import retrofit2.Response
import rise.tiao1.buut.data.local.booking.BookingDao
import rise.tiao1.buut.data.local.booking.LocalBooking
import rise.tiao1.buut.data.remote.booking.BatteryDTO
import rise.tiao1.buut.data.remote.booking.BoatDTO
import rise.tiao1.buut.data.remote.booking.BookingApiService
import rise.tiao1.buut.data.remote.booking.BookingDTO
import rise.tiao1.buut.data.remote.booking.BookingUpdateDTO
import rise.tiao1.buut.data.remote.booking.TimeSlotDTO
import rise.tiao1.buut.data.remote.user.dto.UserBatteryDTO
import rise.tiao1.buut.domain.booking.Booking
import rise.tiao1.buut.domain.booking.TimeSlot
import rise.tiao1.buut.domain.booking.toBooking
import rise.tiao1.buut.domain.booking.toTimeSlot
import rise.tiao1.buut.utils.NetworkConnectivityChecker
import rise.tiao1.buut.utils.toApiDateString
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class BookingRepositoryTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)
    private val dao = mockk<BookingDao>()
    private val service = mockk<BookingApiService>()
    private val networkConnectivityChecker = mockk<NetworkConnectivityChecker>()
    private val repo = BookingRepository(dao, service,networkConnectivityChecker, dispatcher)
    private val today = LocalDateTime.now()
    private val testException = "We can not load the bookings at this moment in time."
    private val USER_ID_WITH_BOOKINGS = "TestUser1"

    @Test
    fun getBookings_IsReturningBookingsWhenUserHasBookings() = scope.runTest {
        coEvery { service.getAllBookingsFromUser(any()) } returns getBookingsDTOs()
        coEvery { dao.insertAllBookings(any()) } returns any()
        coEvery { dao.getBookingsByUserId(any()) } returns getLocalBookings()
        coEvery { networkConnectivityChecker.isNetworkAvailable() } returns true
        val expected = getBookings()
        val actual = repo.getAllBookingsFromUser(USER_ID_WITH_BOOKINGS)

        assert(expected == actual)
    }

    @Test
    fun getBookings_IsReturningEmptyListWhenUserHasNoBookings() = scope.runTest {
        coEvery { service.getAllBookingsFromUser(any()) } returns emptyList()
        coEvery { dao.insertAllBookings(any()) } returns any()
        coEvery { dao.getBookingsByUserId(any()) } returns emptyList()
        coEvery { networkConnectivityChecker.isNetworkAvailable() } returns true
        val expected = emptyList<Booking>()
        val actual = repo.getAllBookingsFromUser(USER_ID_WITH_BOOKINGS)

        assert(expected == actual)
    }

    @Test
    fun getBookings_refreshThrowsExceptionAndHandles() = scope.runTest {
        coEvery { service.getAllBookingsFromUser(any()) } throws Exception(testException)
        coEvery { dao.insertAllBookings(any()) } returns any()
        coEvery { dao.getBookingsByUserId(any()) } returns getLocalBookings()
        coEvery { networkConnectivityChecker.isNetworkAvailable() } returns true
        val result = runCatching { repo.getAllBookingsFromUser(USER_ID_WITH_BOOKINGS) }
        Assert.assertTrue(result.isFailure)
        Assert.assertTrue(result.exceptionOrNull() is Exception)
        Assert.assertEquals(testException, result.exceptionOrNull()?.message)
    }

    @Test
    fun getFreeDates_returnsListOfTimeSlots() = scope.runTest {
        coEvery { service.getFreeTimeSlotsForDateRange(any(), any()) } returns getTimeslotDTOs()
        coEvery { networkConnectivityChecker.isNetworkAvailable() } returns true
        val actual = repo.getFreeTimeSlotsForDateRange(today.toApiDateString())
        val expected = getTimeslots()
        assert(expected == actual)
    }

    @Test
    fun getFreeDates_refreshThrowsExceptionAndHandles() = scope.runTest {
        coEvery { service.getFreeTimeSlotsForDateRange(any(), any()) } throws Exception(
            testException
        )
        coEvery { networkConnectivityChecker.isNetworkAvailable() } returns true
        val result = runCatching { repo.getFreeTimeSlotsForDateRange(today.toApiDateString()) }
        Assert.assertTrue(result.isFailure)
        Assert.assertTrue(result.exceptionOrNull() is Exception)
        Assert.assertEquals(testException, result.exceptionOrNull()?.message)
    }

    @Test
    fun createBooking_createsBooking() = scope.runTest {
        coEvery { service.createBooking(any()) } returns Unit
        coEvery { dao.insertAllBookings(any()) } returns Unit
        coEvery { networkConnectivityChecker.isNetworkAvailable() } returns true
        coEvery { service.getAllBookingsFromUser(USER_ID_WITH_BOOKINGS) } returns listOf(
            getBookingsDTOs()[0]
        )

        repo.createBooking(getBookingDTO())

        coVerify { service.createBooking(any()) }
        coVerify { dao.insertAllBookings(any()) }
        coVerify { service.getAllBookingsFromUser(USER_ID_WITH_BOOKINGS) }
    }

    @Test
    fun createBooking_serviceThrowsExceptionAndHandles() = scope.runTest {
        coEvery { service.createBooking(any()) } throws Exception(testException)
        coEvery { dao.insertAllBookings(any()) } returns Unit
        coEvery { networkConnectivityChecker.isNetworkAvailable() } returns true
        val result = runCatching { repo.createBooking(getBookingDTO()) }
        Assert.assertTrue(result.isFailure)
        Assert.assertTrue(result.exceptionOrNull() is Exception)
        Assert.assertEquals(testException, result.exceptionOrNull()?.message)
    }

    @Test
    fun createBooking_refreshThrowsExceptionAndHandles() = scope.runTest {
        coEvery { service.createBooking(any()) } returns Unit
        coEvery { service.getAllBookingsFromUser(USER_ID_WITH_BOOKINGS) } throws Exception(
            testException
        )
        coEvery { networkConnectivityChecker.isNetworkAvailable() } returns true
        val result = runCatching { repo.createBooking(getBookingDTO()) }
        Assert.assertTrue(result.isFailure)
        Assert.assertTrue(result.exceptionOrNull() is Exception)
        Assert.assertEquals(testException, result.exceptionOrNull()?.message)
    }

    @Test
    fun updateBook_updatesBooking() = scope.runTest {
        coEvery {
            service.updateBooking(
                getBookingUpdateDto().id.toString(),
                getBookingUpdateDto()
            )
        } returns Response.success(Unit)
        coEvery { dao.insertAllBookings(any()) } returns Unit
        coEvery { service.getAllBookingsFromUser(USER_ID_WITH_BOOKINGS) } returns listOf(
            getBookingsDTOs()[0]
        )
        coEvery { networkConnectivityChecker.isNetworkAvailable() } returns true
        repo.updateBooking(
            getBookingUpdateDto().id.toString(),
            USER_ID_WITH_BOOKINGS,
            getBookingUpdateDto()
        )

        coVerify {
            service.updateBooking(
                getBookingUpdateDto().id.toString(),
                getBookingUpdateDto()
            )
        }
        coVerify { dao.insertAllBookings(any()) }
        coVerify { service.getAllBookingsFromUser(USER_ID_WITH_BOOKINGS) }
    }

    @Test
    fun updateBook_serviceThrowsExceptionAndHandles() = scope.runTest {
        coEvery {
            service.updateBooking(
                getBookingUpdateDto().id.toString(),
                getBookingUpdateDto()
            )
        } throws Exception(testException)
        coEvery { networkConnectivityChecker.isNetworkAvailable() } returns true
        val result = runCatching {
            repo.updateBooking(
                getBookingUpdateDto().id.toString(),
                USER_ID_WITH_BOOKINGS,
                getBookingUpdateDto()
            )
        }

        Assert.assertTrue(result.isFailure)
        Assert.assertTrue(result.exceptionOrNull() is Exception)
        Assert.assertEquals(testException, result.exceptionOrNull()?.message)
    }

    @Test
    fun updateBook_refreshThrowsExceptionAndHandles() = scope.runTest {
        coEvery {
            service.updateBooking(
                getBookingUpdateDto().id.toString(),
                getBookingUpdateDto()
            )
        } returns Response.success(Unit)
        coEvery { networkConnectivityChecker.isNetworkAvailable() } returns true
        coEvery { service.getAllBookingsFromUser(USER_ID_WITH_BOOKINGS) } throws Exception(
            testException
        )
        coEvery { networkConnectivityChecker.isNetworkAvailable() } returns true
        val result = runCatching {
            repo.updateBooking(
                getBookingUpdateDto().id.toString(),
                USER_ID_WITH_BOOKINGS,
                getBookingUpdateDto()
            )
        }

        Assert.assertTrue(result.isFailure)
        Assert.assertTrue(result.exceptionOrNull() is Exception)
        Assert.assertEquals(testException, result.exceptionOrNull()?.message)
    }


    fun getTimeslots(): List<TimeSlot> {
        return getTimeslotDTOs().map { it.toTimeSlot() }
    }


    fun getTimeslotDTOs(): List<TimeSlotDTO> {
        return listOf(
            TimeSlotDTO(today.toApiDateString(), "testSlot1", true),
            TimeSlotDTO(today.plusDays(1).toApiDateString(), "testSlot2", false),
            TimeSlotDTO(today.plusDays(2).toApiDateString(), "testSlot3", true),
        )

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

    fun getBookingsDTOs(): List<BookingDTO> {
        return listOf(
            BookingDTO(
                "1",
                today.toApiDateString(),
                "TestTimeSlot1",
                getBoatDTO(),
                getBatteryDTO(),
                "TestUser1"
            ),
            BookingDTO(
                "2",
                today.toApiDateString(),
                "TestTimeSlot2",
                getBoatDTO(),
                getBatteryDTO(),
                "TestUser2"
            ),
            BookingDTO(
                "3",
                today.toApiDateString(),
                "TestTimeSlot3",
                getBoatDTO(),
                getBatteryDTO(),
                "TestUser3"
            ),
        )
    }

    fun getBoatDTO(): BoatDTO {
        return BoatDTO(
            name = "TestBoat"
        )
    }

    fun getBatteryDTO(): BatteryDTO {
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

    fun getBookingDTO(): BookingDTO =
        BookingDTO(
            "1",
            today.toApiDateString(),
            "TestTimeSlot1",
            getBoatDTO(),
            getBatteryDTO(),
            "TestUser1"
        )

    fun getBookingUpdateDto(): BookingUpdateDTO =
        BookingUpdateDTO("1", today.plusDays(1).toApiDateString())
}