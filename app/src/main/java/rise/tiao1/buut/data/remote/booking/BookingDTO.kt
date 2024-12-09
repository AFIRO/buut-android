package rise.tiao1.buut.data.remote.booking

import com.google.gson.annotations.SerializedName
import com.google.type.DateTime
import rise.tiao1.buut.data.local.booking.LocalBooking
import rise.tiao1.buut.utils.toDateString
import java.time.LocalDateTime

data class BookingDTO(
    @SerializedName("bookingId")
    val id: String? = null,
    @SerializedName("bookingDate")
    val date: String,
    @SerializedName("timeSlot")
    val time: String? = null,
    val boat: BoatDTO? = null,
    val battery: BatteryDTO? = null,
    val userId: String? = null
)

fun BookingDTO.toLocalBooking(userId: String): LocalBooking{
    return LocalBooking(
        id = this.id ?: "",
        date = this.date,
        boat = this.boat?.name,
        battery = this.battery?.name,
        batteryUserFirstName = this.battery?.currentUser?.firstName,
        batteryUserLastName = this.battery?.currentUser?.lastName,
        batteryUserEmail = this.battery?.currentUser?.email,
        batteryUserPhoneNumber = this.battery?.currentUser?.phoneNumber,
        userId = userId
    )
}
