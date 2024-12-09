package rise.tiao1.buut.data.remote.booking

import com.google.gson.annotations.SerializedName
import rise.tiao1.buut.data.remote.user.dto.UserBatteryDTO

data class BatteryDTO(
    @SerializedName("name")
    val name: String,
    @SerializedName("currentUser")
    val currentUser: UserBatteryDTO?
)
