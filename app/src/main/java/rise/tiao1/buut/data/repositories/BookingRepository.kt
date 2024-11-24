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
import rise.tiao1.buut.utils.toApiErrorMessage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepository @Inject constructor(
    private val bookingDao: BookingDao,
    private val apiService: BookingApiService,
    @IoDispatcher private val dispatcher:
    CoroutineDispatcher
) {
    suspend fun getAllBookingsFromUser(userId: String): List<Booking> =
        withContext(dispatcher) {
            try {
                refreshCache(userId)
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
                val remoteAvailableDays = apiService.getAvailableDays()
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