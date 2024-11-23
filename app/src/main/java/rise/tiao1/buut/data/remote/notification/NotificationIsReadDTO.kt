package rise.tiao1.buut.data.remote.notification

import com.google.gson.annotations.SerializedName

data class NotificationIsReadDTO (
    @SerializedName("notificationId")
    val notificationId: String? = null,
    @SerializedName("isRead")
    val isRead: Boolean? = null,
)
