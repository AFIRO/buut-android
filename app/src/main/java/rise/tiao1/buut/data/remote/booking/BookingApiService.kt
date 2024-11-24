package rise.tiao1.buut.data.remote.booking

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface BookingApiService {

    @GET("api/Booking")
    suspend fun getAllBookings(): List<BookingDTO>

    @POST("api/Booking")
    suspend fun createBooking(@Body bookingDTO: BookingDTO)

    @GET("api/Booking/User/{userId}")
    suspend fun getAllBookingsFromUser(@Path("userId") userId: String): List<BookingDTO>

    @GET("api/Booking/free")
    suspend fun getAvailableDays(): List<TimeSlotDTO>

    @GET("api/Booking/free/byDateRange")
    suspend fun getFreeTimeSlotsForDateRange(@Query("startDate") startDate: String, @Query("endDate") endDate: String): List<TimeSlotDTO>

    @PUT("api/Booking/{bookingId}")
    suspend fun updateBooking(@Path("bookingId") bookingId: String, @Body BookingUpdateDTO: BookingUpdateDTO)
}

