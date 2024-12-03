package rise.tiao1.buut.domain.booking.useCases

import rise.tiao1.buut.data.repositories.BookingRepository
import rise.tiao1.buut.domain.booking.Booking
import rise.tiao1.buut.presentation.home.HomeScreenState
import javax.inject.Inject

class GetBookingsSortedByDateUseCase @Inject constructor (
    private val bookingRepository: BookingRepository,
    private val homeScreenState: HomeScreenState
) {
    suspend operator fun invoke(
        userId: String
    ):List<Booking> {
       val bookings = bookingRepository.getAllBookingsFromUser(userId)
        if (bookings.isNotEmpty()){
            homeScreenState.bookings = bookings.sortedByDescending { it.date }
        }
        return if (bookings.isNotEmpty()) {
            bookings.sortedByDescending { it.date }
        }
        else
            emptyList()
    }
}