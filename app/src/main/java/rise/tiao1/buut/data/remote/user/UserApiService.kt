package rise.tiao1.buut.data.remote.user

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import rise.tiao1.buut.data.remote.user.dto.PutUserDTO
import rise.tiao1.buut.data.remote.user.dto.UserDTO


interface UserApiService {
    @POST("api/User")
    suspend fun registerUser(@Body data: UserDTO)

    @GET("api/User/{id}/details")
    suspend fun getUserById(@Path("id", encoded = false) id: String): RemoteUser

    @PUT("api/User")
    suspend fun updateUser(@Body data: PutUserDTO)

}