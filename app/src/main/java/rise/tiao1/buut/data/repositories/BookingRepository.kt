package rise.tiao1.buut.data.repositories

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import rise.tiao1.buut.data.di.IoDispatcher
import rise.tiao1.buut.data.local.booking.BookingDao
import rise.tiao1.buut.data.remote.booking.BookingApiService
import rise.tiao1.buut.data.remote.booking.BookingDTO
import rise.tiao1.buut.data.remote.booking.BookingUpdateDTO
import rise.tiao1.buut.data.remote.booking.toLocalBooking
import rise.tiao1.buut.domain.booking.Booking
import rise.tiao1.buut.domain.booking.TimeSlot
import rise.tiao1.buut.domain.booking.toBooking
import rise.tiao1.buut.domain.booking.toTimeSlot
import rise.tiao1.buut.utils.NetworkConnectivityChecker
import rise.tiao1.buut.utils.toApiErrorMessage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepository @Inject constructor(
    private val bookingDao: BookingDao,
    private val apiService: BookingApiService,
    private val networkConnectivityChecker: NetworkConnectivityChecker,
    @IoDispatcher private val dispatcher:
    CoroutineDispatcher
) {
    val noInternetConnection =
        "You appear to be offline. Displaying local data until reconnection. \n You will not be able to create a new booking, edit a booking or edit your personal data."

    suspend fun getAllBookingsFromUser(userId: String): List<Booking> =
        withContext(dispatcher) {
            try {
                if (networkConnectivityChecker.isNetworkAvailable()) {
                    refreshCache(userId)
                }
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        throw Exception(e.toApiErrorMessage())
                    }

                    else -> throw Exception(e.message)
                }
            }

            return@withContext bookingDao.getBookingsByUserId(userId).map { it.toBooking() }
        }

    private suspend fun refreshCache(userId: String) {
        val remoteBookings = apiService
            .getAllBookingsFromUser(userId)
        bookingDao.insertAllBookings(remoteBookings.map {
            it.toLocalBooking(userId)
        })
    }

    suspend fun getAvailableDays(): List<TimeSlot> =
        withContext(dispatcher) {
            try {
                if (!networkConnectivityChecker.isNetworkAvailable()) {
                    throw Exception(noInternetConnection)
                }
                val remoteAvailableDays = apiService.getAvailableDays().value
                return@withContext remoteAvailableDays.map { it.toTimeSlot() }
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        throw Exception(e.toApiErrorMessage())
                    }

                    else -> throw Exception(e.message)
                }
            }
        }

    suspend fun getFreeTimeSlotsForDateRange(date: String): List<TimeSlot> =
        withContext(dispatcher) {
            try {
                if (!networkConnectivityChecker.isNetworkAvailable()) {
                    throw Exception(noInternetConnection)
                }
                val remoteTimeSlots = apiService.getFreeTimeSlotsForDateRange(date, date)
                return@withContext remoteTimeSlots.map { it.toTimeSlot() }
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        throw Exception(e.toApiErrorMessage())
                    }

                    else -> throw Exception(e.message)
                }
            }
        }

    suspend fun createBooking(bookingDto: BookingDTO) {
        withContext(dispatcher) {
            try {
                if (!networkConnectivityChecker.isNetworkAvailable()) {
                    throw Exception(noInternetConnection)
                }
                apiService.createBooking(bookingDto)
                refreshCache(bookingDto.userId ?: "")
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        throw Exception(e.toApiErrorMessage())
                    }

                    else -> throw Exception(e.message)
                }
            }
        }
    }

    suspend fun updateBooking(
        bookingId: String,
        userId: String,
        bookingUpdateDTO: BookingUpdateDTO
    ) {
        withContext(dispatcher) {
            try {
                if (!networkConnectivityChecker.isNetworkAvailable()) {
                    throw Exception(noInternetConnection)
                }
                apiService.updateBooking(bookingId, bookingUpdateDTO)
                refreshCache(userId ?: "")
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        throw Exception(e.toApiErrorMessage())
                    }

                    else -> throw Exception(e.message)
                }
            }
        }
    }
}