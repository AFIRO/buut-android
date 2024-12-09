package rise.tiao1.buut.data.remote.user.dto

import com.google.gson.annotations.SerializedName

data class UserBatteryDTO (
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
)