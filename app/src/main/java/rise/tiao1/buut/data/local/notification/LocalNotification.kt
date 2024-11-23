package rise.tiao1.buut.data.local.notification

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification")
data class LocalNotification (
    @PrimaryKey
    @ColumnInfo
    val id: String,
    @ColumnInfo
    val userId: String,
    @ColumnInfo
    val title: String? = null,
    @ColumnInfo
    val message: String? = null,
    @ColumnInfo
    val isRead: Boolean? = null,
    @ColumnInfo
    val type: String? = null,
    @ColumnInfo
    val createdAt: String,
    @ColumnInfo
    val relatedEntityId: String? = null
)