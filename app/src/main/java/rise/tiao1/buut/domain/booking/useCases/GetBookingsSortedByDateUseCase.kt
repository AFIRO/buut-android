package rise.tiao1.buut.domain.booking.useCases

import rise.tiao1.buut.data.repositories.BookingRepository
import rise.tiao1.buut.domain.booking.Booking
import javax.inject.Inject

class GetBookingsSortedByDateUseCase @Inject constructor (
    private val bookingRepository: BookingRepository,
) {
    suspend operator fun invoke(
        userId: String
    ):List<Booking> {
       val bookings = bookingRepository.getAllBookingsFromUser(userId)
        return if (bookings.isNotEmpty())
            bookings.sortedBy { it.date }
        else
            emptyList()
    }
}