package rise.tiao1.buut.domain.booking.useCases

import rise.tiao1.buut.data.repositories.BookingRepository
import rise.tiao1.buut.domain.booking.Booking
import javax.inject.Inject

class GetBookingsSortedByDateUseCase @Inject constructor(
    private val bookingRepository: BookingRepository,
) {
    suspend operator fun invoke(
        userId: String,
    ): List<Booking> {
        try {
            val bookings = bookingRepository.getAllBookingsFromUser(userId)
            return if (bookings.isNotEmpty())
                bookings.sortedByDescending { it.date }
            else
                emptyList()
        } catch (e: Exception) {
            throw Exception("Error fetching bookings: ${e.message}")
        }
    }
}