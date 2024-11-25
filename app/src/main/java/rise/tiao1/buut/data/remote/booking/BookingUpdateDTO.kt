package rise.tiao1.buut.data.remote.booking

import com.google.gson.annotations.SerializedName
data class BookingUpdateDTO(
    @SerializedName("bookingId")
    val id: String? = null,
    @SerializedName("bookingDate")
    val date: String
)