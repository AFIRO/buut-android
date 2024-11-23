package rise.tiao1.buut.data.remote.notification

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import rise.tiao1.buut.utils.NotificationType

interface NotificationApiService {

    @GET("api/Notification/user/{userId}")
    suspend fun getAllNotificationsFromUser(@Path("userId") userId: String, @Query("language") language: String = "en"): List<NotificationDTO>

    @PUT("api/Notification")
    suspend fun markNotificationAsRead(@Body notificationIsReadDTO: NotificationIsReadDTO)
}

