package rise.tiao1.buut.data.repositories


import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import rise.tiao1.buut.data.local.user.LocalUser
import rise.tiao1.buut.data.local.user.UserDao
import rise.tiao1.buut.data.remote.user.RemoteUser
import rise.tiao1.buut.data.remote.user.UserApiService
import rise.tiao1.buut.data.remote.user.dto.AddressDTO
import rise.tiao1.buut.data.remote.user.dto.PutUserDTO
import rise.tiao1.buut.data.remote.user.dto.RoleDTO
import rise.tiao1.buut.data.remote.user.dto.UserDTO
import rise.tiao1.buut.domain.user.Address
import rise.tiao1.buut.domain.user.User
import rise.tiao1.buut.domain.user.toLocalUser
import rise.tiao1.buut.utils.NetworkConnectivityChecker
import rise.tiao1.buut.utils.StreetType
import rise.tiao1.buut.utils.toLocalUser
import rise.tiao1.buut.utils.toUser
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class UserRepositoryTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)
    private val dao: UserDao = mockk()
    private val apiService: UserApiService = mockk()
    private val networkConnectivityChecker = mockk<NetworkConnectivityChecker>()
    private val userRepository = UserRepository(dao, apiService,networkConnectivityChecker, dispatcher)
    private val testError = "TestError"
    private val noInternetConnectionError =
        "You appear to be offline. Displaying local data until reconnection. \n You will not be able to create a new booking, edit a booking or edit your personal data."

    @Test
    fun getUser_userExistsInRoom_returnsUser() = scope.runTest {
        val userFromDao = getLocalUser()
        val expected = userFromDao.toUser()
        coEvery { dao.getUserById(any()) } returns userFromDao
        val result = userRepository.getUser(expected.id.toString())
        assert(result.equals(expected))
        coVerify { dao.getUserById(expected.id.toString()) }
        coVerify(exactly = 0) { apiService.getUserById(any()) }
        coVerify(exactly = 0) { dao.insertUser(any()) }
    }

    @Test
    fun getUser_userExistsInBackendButNotRoom_returnsUserAndUpdatesRoom() = scope.runTest {
        val userFromDao = null
        val userFromApi = getRemoteUser()
        val userToInsert = userFromApi.toLocalUser()
        val expected = userFromApi.toLocalUser().toUser()
        coEvery { networkConnectivityChecker.isNetworkAvailable() } returns true
        coEvery { dao.getUserById(expected.id.toString()) } returns userFromDao
        coEvery { apiService.getUserById(expected.id.toString()) } returns userFromApi
        coEvery { dao.insertUser(userToInsert) } returns Unit
        val result = userRepository.getUser(expected.id.toString())
        assertEquals(result,expected)
        coVerify { dao.getUserById(expected.id.toString()) }
        coVerify { apiService.getUserById(expected.id.toString()) }
        coVerify { dao.insertUser(userToInsert) }
    }


    @Test
    fun getUser_userDoesNotExistInRoomAndBackend_returnsEmptyUser() = scope.runTest {
        val userFromDao = null
        val userFromApi = RemoteUser(
            null.toString(),
            null.toString(),
            null.toString(),
            null.toString(),
            null.toString(),
            birthDate = LocalDateTime.of(1996, 8, 19, 0, 0, 1).toString(),
            address = getAddressDto(),
            roles = listOf(RoleDTO(name = "Admin"))
        )
        val userToInsert = userFromApi.toLocalUser()
        val expected = userFromApi.toLocalUser().toUser()
        coEvery { networkConnectivityChecker.isNetworkAvailable() } returns true
        coEvery { dao.getUserById(expected.id.toString()) } returns userFromDao
        coEvery { apiService.getUserById(expected.id.toString()) } returns userFromApi
        coEvery { dao.insertUser(userToInsert) } returns Unit
        val result = userRepository.getUser(expected.id.toString())
        assert(result.equals(expected))
        coVerify { dao.getUserById(expected.id.toString()) }
        coVerify { apiService.getUserById(expected.id.toString()) }
        coVerify { dao.insertUser(userToInsert) }
    }

    @Test
    fun getUser_remoteUserNeededButNoConnection_skipsRemoteRefreshAndReturnsEmptyUser() = scope.runTest {
        val userFromDao = null
        val expected = RemoteUser(
            null.toString(),
            null.toString(),
            null.toString(),
            null.toString(),
            null.toString(),
            birthDate = LocalDateTime.now().minusYears(20).toString(),
            address = AddressDTO(StreetType.AFRIKALAAN, 1.toString(), null.toString()),
            roles = listOf(RoleDTO(name = "User"))
        ).toLocalUser().toUser()
        coEvery { networkConnectivityChecker.isNetworkAvailable() } returns false
        coEvery { dao.getUserById(any()) } returns userFromDao
        val result = userRepository.getUser("Id")
        assertEquals(expected,result)
        coVerify { dao.getUserById(any()) }
        coVerify (exactly = 0) { apiService.getUserById(any()) }
        coVerify (exactly = 0) { dao.insertUser(any()) }
    }

    @Test
    fun getUser_roomException_handles() = scope.runTest {
        val userFromDao = getLocalUser()
        val expected = userFromDao.toUser()
        coEvery { dao.getUserById(any()) } throws Exception(testError)
        val result = runCatching {userRepository.getUser(expected.id.toString())}
        assert(result.isFailure)
        assert(result.exceptionOrNull() != null)
        assert(result.exceptionOrNull() is Exception)
        assertEquals(result.exceptionOrNull()?.message, testError)
        coVerify (exactly = 0) { apiService.getUserById(any()) }
        coVerify (exactly = 0) { dao.insertUser(any()) }
    }

    @Test
    fun getUser_remoteException_handles() = scope.runTest {
        val userFromDao = null
        coEvery { dao.getUserById(any()) } returns userFromDao
        coEvery {networkConnectivityChecker.isNetworkAvailable()} returns true
        coEvery { apiService.getUserById(any()) } throws Exception(testError)
        val result = runCatching {userRepository.getUser("id")}
        assert(result.isFailure)
        assert(result.exceptionOrNull() != null)
        assert(result.exceptionOrNull() is Exception)
        assertEquals(result.exceptionOrNull()?.message, testError)
        coVerify { dao.getUserById(any())}
        coVerify { apiService.getUserById(any()) }
        coVerify (exactly = 0) { dao.insertUser(any()) }
    }

    @Test
    fun deleteUser_deletesUserFromRoom() = scope.runTest {
        val user = getUser()
        coEvery { dao.deleteUser(user.toLocalUser()) } returns Unit
        userRepository.deleteUser(user)
        coVerify { dao.deleteUser(user.toLocalUser()) }
    }

    @Test
    fun registerUser_successful_returnsTrue() = scope.runTest {
        val userDto = getUserDto()
        coEvery { apiService.registerUser(userDto) } returns Unit
        coEvery { networkConnectivityChecker.isNetworkAvailable() } returns true
        userRepository.registerUser(userDto)
        coVerify { apiService.registerUser(userDto) }
    }

    @Test
    fun registerUser_unsuccessful_returnsFalse() = scope.runTest {
        val userDto = getUserDto()
        coEvery { apiService.registerUser(userDto) } throws Exception()
        coEvery { networkConnectivityChecker.isNetworkAvailable() } returns true
        val result = runCatching { userRepository.registerUser(userDto) }
        assert(result.isFailure)
        assert(result.exceptionOrNull() != null)
        assert(result.exceptionOrNull() is Exception)
        coVerify { apiService.registerUser(userDto) }
    }

    @Test
    fun registerUser_noConnection_handles() = scope.runTest {
        coEvery { networkConnectivityChecker.isNetworkAvailable() } returns false
        val result = runCatching { userRepository.registerUser(getUserDto()) }
        assert(result.isFailure)
        assert(result.exceptionOrNull() != null)
        assert(result.exceptionOrNull() is Exception)
        assert(result.exceptionOrNull()?.message == noInternetConnectionError)
        coVerify (exactly = 0) { apiService.registerUser(any()) }
    }

    @Test
    fun updateUser_success_updatesRemoteAndLocalUsersSuccessfully() = scope.runTest {
        val putUserDTO = getPutUserDto()
        val remoteUser = getRemoteUser()
        coEvery { apiService.updateUser(putUserDTO) } returns Unit
        coEvery { apiService.getUserById("fg") } returns remoteUser
        coEvery { dao.insertUser(remoteUser.toLocalUser()) } returns Unit
        coEvery { networkConnectivityChecker.isNetworkAvailable() } returns true
        userRepository.updateUser(putUserDTO)
        coVerify { apiService.updateUser(putUserDTO) }
        coVerify { apiService.getUserById("fg") }
        coVerify { dao.insertUser(remoteUser.toLocalUser()) }
    }

    @Test
    fun updateUser_exception_throwsExceptionWhenApiServiceUpdateFails() = scope.runTest {
        val putUserDTO = getPutUserDto()
        coEvery { apiService.updateUser(putUserDTO) } throws HttpException(
            Response.error<Any>(400, ResponseBody.create(null, "Bad Request"))
        )
        val result = runCatching { userRepository.updateUser(putUserDTO) }
        assert(result.isFailure)
        assert(result.exceptionOrNull() != null)
        assert(result.exceptionOrNull() is Exception)
        coVerify(exactly = 0) { apiService.getUserById(any()) }
        coVerify(exactly = 0) { dao.insertUser(any()) }
    }

    @Test
    fun updateUser_noConnection_handles() = scope.runTest {
        coEvery { networkConnectivityChecker.isNetworkAvailable() } returns false
        val result = runCatching { userRepository.updateUser(getPutUserDto())}
        assert(result.isFailure)
        assert(result.exceptionOrNull() != null)
        assert(result.exceptionOrNull() is Exception)
        assertEquals(result.exceptionOrNull()?.message,noInternetConnectionError)
        coVerify (exactly = 0) { apiService.updateUser(any()) }
        coVerify (exactly = 0)  { apiService.getUserById(any()) }
        coVerify (exactly = 0)  { dao.insertUser(any()) }
    }


    fun getUser(): User {
        return User(
            id = "fg",
            firstName = "TestFirstName",
            lastName = "TestLastName",
            email = "TestEmail",
            password = "TestPassword",
            phone = "TestPhone",
            dateOfBirth = LocalDateTime.of(1996, 8, 19, 0, 0, 1),
            address = getAddress(),
            roles = listOf()
        )
    }

    fun getLocalUser(): LocalUser {
        return LocalUser(
            id = "fg",
            firstName = "TestFirstName",
            lastName = "TestLastName",
            email = "TestEmail",
            phone = "TestPhone",
            dateOfBirth = LocalDateTime.of(1996, 8, 19, 0, 0, 1).toString(),
            address = getAddress(),
            roles = ""
        )
    }

    fun getRemoteUser(): RemoteUser {
        return RemoteUser(
            id = "fg",
            firstName = "TestFirstName",
            lastName = "TestLastName",
            email = "TestEmail",
            phoneNumber = "TestPhone",
            birthDate = LocalDateTime.of(1996, 8, 19, 0, 0, 1).toString(),
            address = getAddressDto(),
            roles = listOf()
        )
    }

    fun getUserDto(): UserDTO {
        return UserDTO(
            firstName = "TestFirstName",
            lastName = "TestLastName",
            email = "TestEmail",
            password = "TestPassword",
            phone = "TestPhone",
            dateOfBirth = LocalDateTime.of(1996, 8, 19, 0, 0, 1).toString(),
            address = getAddressDto()
        )

    }

    fun getPutUserDto(): PutUserDTO {
        return PutUserDTO(
            firstName = "TestFirstName",
            lastName = "TestLastName",
            email = "TestEmail",
            password = "TestPassword",
            phone = "TestPhone",
            dateOfBirth = LocalDateTime.of(1996, 8, 19, 0, 0, 1).toString(),
            address = getAddressDto(),
            id = "fg",
            roles = listOf()
        )

    }

    fun getAddressDto(): AddressDTO {
        return AddressDTO(StreetType.AFRIKALAAN, "TestHuisnummer", "TestBox")
    }

    fun getAddress(): Address {
        return Address(StreetType.AFRIKALAAN, "TestHuisnummer", "TestBox")
    }

}