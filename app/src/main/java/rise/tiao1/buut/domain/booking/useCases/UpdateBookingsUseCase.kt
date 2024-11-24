package rise.tiao1.buut.domain.booking.useCases

import rise.tiao1.buut.data.remote.booking.BookingUpdateDTO
import rise.tiao1.buut.data.repositories.BookingRepository
import rise.tiao1.buut.domain.booking.TimeSlot
import rise.tiao1.buut.domain.user.useCases.GetUserUseCase
import rise.tiao1.buut.utils.toApiDateString
import javax.inject.Inject

class UpdateBookingsUseCase @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val getUserUseCase: GetUserUseCase
) {
    suspend operator fun invoke(bookingId:String, timeSlot: TimeSlot) {
        val userId = getUserUseCase().id
        val updatedBooking = BookingUpdateDTO(
            id = bookingId,
            date = timeSlot.date.toApiDateString(),
        )
        bookingRepository.updateBooking(bookingId, userId!!, updatedBooking)
    }
}