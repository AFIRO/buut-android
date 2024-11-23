package rise.tiao1.buut.data.remote.notification

import com.google.gson.annotations.SerializedName
import rise.tiao1.buut.data.local.notification.LocalNotification

data class NotificationDTO (
    @SerializedName("notificationId")
    val notificationId: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("isRead")
    val isRead: Boolean? = null,
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("relatedEntityId")
    val relatedEntityId: String? = null
)

fun NotificationDTO.toLocalNotification(userId: String): LocalNotification {
    return LocalNotification(
        id = this.notificationId ?: "",
        userId = userId,
        title = this.title ?: "",
        message = this.message ?: "",
        isRead = this.isRead ?: false,
        type = this.type ?: "",
        createdAt = this.createdAt,
        relatedEntityId = this.relatedEntityId ?: ""
    )
}