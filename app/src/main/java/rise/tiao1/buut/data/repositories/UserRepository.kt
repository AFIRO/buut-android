package rise.tiao1.buut.data.repositories

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import rise.tiao1.buut.data.di.IoDispatcher
import rise.tiao1.buut.data.local.user.UserDao
import rise.tiao1.buut.data.remote.user.RemoteUser
import rise.tiao1.buut.data.remote.user.UserApiService
import rise.tiao1.buut.data.remote.user.dto.AddressDTO
import rise.tiao1.buut.data.remote.user.dto.PutUserDTO
import rise.tiao1.buut.data.remote.user.dto.RoleDTO
import rise.tiao1.buut.data.remote.user.dto.UserDTO
import rise.tiao1.buut.domain.user.User
import rise.tiao1.buut.domain.user.toLocalUser
import rise.tiao1.buut.utils.NetworkConnectivityChecker
import rise.tiao1.buut.utils.StreetType
import rise.tiao1.buut.utils.toApiErrorMessage
import rise.tiao1.buut.utils.toLocalUser
import rise.tiao1.buut.utils.toUser
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val dao: UserDao,
    private val apiService: UserApiService,
    private val networkConnectivityChecker: NetworkConnectivityChecker,
    @IoDispatcher private val dispatcher:
    CoroutineDispatcher
) {
    val noInternetConnection =
        "You appear to be offline. Displaying local data until reconnection. \n You will not be able to create a new booking, edit a booking or edit your personal data."

    suspend fun getUser(id: String): User =
        withContext(dispatcher) {
            try {
                var localUser = dao.getUserById(id)
                if (localUser == null && networkConnectivityChecker.isNetworkAvailable()) {
                    val remoteUser = apiService.getUserById(id)
                    localUser = remoteUser.toLocalUser()
                    dao.insertUser(localUser)
                }
                if (localUser == null && !networkConnectivityChecker.isNetworkAvailable()) {
                    localUser = getFallbackUser()
                }
                return@withContext localUser!!.toUser()
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        throw Exception(e.toApiErrorMessage())
                    }

                    else -> throw Exception(e.message)
                }
            }
        }

    suspend fun deleteUser(user: User) {
        withContext(dispatcher) {
            try {
                dao.deleteUser(user.toLocalUser())
            } catch (e: Exception) {
                throw Exception(e.message)
            }
        }
    }

    suspend fun registerUser(userDto: UserDTO) {
        try {
            if (!networkConnectivityChecker.isNetworkAvailable()) {
                throw Exception(noInternetConnection)
            }
            apiService.registerUser(userDto)
        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    throw Exception(e.toApiErrorMessage())
                }

                else -> throw Exception(e.message)
            }
        }
    }

    suspend fun updateUser(putUserDTO: PutUserDTO) {
        withContext(dispatcher) {
            try {
                if (!networkConnectivityChecker.isNetworkAvailable()) {
                    throw Exception(noInternetConnection)
                }
                apiService.updateUser(putUserDTO)
                val remoteUser = apiService.getUserById(putUserDTO.id)
                dao.insertUser(remoteUser.toLocalUser())
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        throw Exception(e.toApiErrorMessage())
                    }

                    else -> throw Exception(e.message)
                }
            }
        }
    }

    suspend fun updateRemoteUser(putUserDTO: PutUserDTO) {
        withContext(dispatcher) {
            try {
                apiService.updateUser(putUserDTO)
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        throw Exception(e.toApiErrorMessage())
                    }

                    else -> throw Exception(e.message)
                }
            }
        }
    }

    private fun getFallbackUser() = RemoteUser(
        null.toString(),
        null.toString(),
        null.toString(),
        null.toString(),
        null.toString(),
        birthDate = LocalDateTime.now().minusYears(20).toString(),
        address = AddressDTO(StreetType.AFRIKALAAN, 1.toString(), null.toString()),
        roles = listOf(RoleDTO(name = "User"))
    ).toLocalUser()
}




