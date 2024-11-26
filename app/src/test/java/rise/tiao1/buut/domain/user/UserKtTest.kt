package rise.tiao1.buut.domain.user

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import rise.tiao1.buut.data.local.user.LocalUser
import rise.tiao1.buut.data.remote.user.dto.AddressDTO
import rise.tiao1.buut.data.remote.user.dto.UserDTO
import rise.tiao1.buut.utils.StreetType
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class UserKtTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun whenToLocalUserIsCalled_returnsCorrectLocalUser() = scope.runTest{
        val user = getUser()
        val expected = getLocalUser()
        val result = user.toLocalUser()
        assert(result.equals(expected))
    }

    @Test
    fun whenToDtoIsCalled_returnsCorrectDto() = scope.runTest{
        val user = getUser()
        val expected = getUserDto()
        val result = user.toUserDTO()
        assert(result.equals(expected))
    }

    fun getAddress() : Address {
        return Address(StreetType.AFRIKALAAN, "TestHuisnummer", "TestBox")
    }

    fun getAddressDto() : AddressDTO {
        return AddressDTO(StreetType.AFRIKALAAN, "TestHuisnummer", "TestBox")
    }

    fun getUser() : User {
        return User(
            id = "fg",
            firstName = "TestFirstName",
            lastName = "TestLastName",
            email = "TestEmail",
            password = "TestPassword",
            phone = "TestPhone",
            dateOfBirth = LocalDateTime.of(1996, 8, 19, 0, 0,1),
            address = getAddress(),
            roles = listOf()
        )
    }

    fun getUserDto() : UserDTO {
        return UserDTO(
            firstName = "TestFirstName",
            lastName = "TestLastName",
            email = "TestEmail",
            password = "TestPassword",
            phone = "TestPhone",
            dateOfBirth = LocalDateTime.of(1996, 8, 19, 0, 0,1).toString(),
            address = getAddressDto()
        )
    }

    fun getLocalUser(): LocalUser {
        return LocalUser(
            id = "fg",
            firstName = "TestFirstName",
            lastName = "TestLastName",
            email = "TestEmail",
            phone = "TestPhone",
            dateOfBirth = LocalDateTime.of(1996, 8, 19, 0, 0,1).toString(),
            address = getAddress(),
            roles = "[]"
        )
    }
}