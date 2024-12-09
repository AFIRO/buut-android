package rise.tiao1.buut.domain.booking

import rise.tiao1.buut.data.local.booking.LocalBooking
import rise.tiao1.buut.data.remote.booking.BookingDTO
import rise.tiao1.buut.utils.toLocalDateTimeFromApiString
import java.time.LocalDateTime

data class Booking(
    val id: String,
    val date: LocalDateTime,
    val boat: String? = null,
    val battery: String? = null,
    val batteryUserFirstName: String? = null,
    val batteryUserLastName: String? = null,
    val batteryUserEmail: String? = null,
    val batteryUserPhoneNumber: String? = null,
)

fun BookingDTO.toBooking(): Booking{
    return Booking(
        id = this.id ?: "",
        date = this.date.toLocalDateTimeFromApiString(),
        boat = this.boat?.name,
        battery = this.battery?.name,
        batteryUserFirstName = this.battery?.currentUser?.firstName,
        batteryUserLastName = this.battery?.currentUser?.lastName,
        batteryUserEmail = this.battery?.currentUser?.email,
        batteryUserPhoneNumber = this.battery?.currentUser?.phoneNumber
    )
}

fun LocalBooking.toBooking(): Booking {
    return Booking(
        id = this.id,
        date = this.date.toLocalDateTimeFromApiString(),
        boat = this.boat,
        battery = this.battery,
        batteryUserFirstName = this.batteryUserFirstName,
        batteryUserLastName = this.batteryUserLastName,
        batteryUserEmail = this.batteryUserEmail,
        batteryUserPhoneNumber = this.batteryUserPhoneNumber
    )
}