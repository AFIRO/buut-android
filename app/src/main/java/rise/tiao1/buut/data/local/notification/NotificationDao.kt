package rise.tiao1.buut.data.local.notification

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(booking: LocalNotification)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNotifications(bookings: List<LocalNotification>)

    @Query("SELECT * FROM notification WHERE userId = :userId")
    suspend fun getNotificationsByUserId(userId: String): List<LocalNotification>

    @Query("SELECT * FROM notification WHERE id = :id")
    suspend fun getNotificationById(id: String): LocalNotification

    @Query("SELECT * FROM notification WHERE relatedEntityId = :relatedEntityId")
    suspend fun getNotificationsByRelatedEntityId(relatedEntityId: String): List<LocalNotification>
}