package rise.tiao1.buut.domain.user.useCases

import android.content.SharedPreferences
import com.auth0.android.jwt.JWT
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import rise.tiao1.buut.data.repositories.UserRepository
import rise.tiao1.buut.domain.user.Address
import rise.tiao1.buut.domain.user.User
import rise.tiao1.buut.utils.StreetType
import java.time.LocalDateTime


@ExperimentalCoroutinesApi
class GetUserUseCaseTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private val userRepository: UserRepository = mockk()
    private val sharedPreferences: SharedPreferences = mockk()
    private val JWT: JWT = mockk()
    private val usecase = GetUserUseCase(userRepository, sharedPreferences)

    @Test
    fun getUserUseCase_GeenToken_returnsUserFromRepository(): Unit = scope.runTest {
        coEvery { sharedPreferences.getString(any(), any()) } returns null
        coEvery { userRepository.getUser(any()) } returns getUser()

        val result = usecase.invoke()

        coVerify { userRepository.getUser(any()) }
        coVerify { sharedPreferences.getString(any(), any()) }
        assertEquals(result, getUser())
    }

    fun getUser(): User {
        return User(
            id = "fg",
            firstName = "TestFirstName",
            lastName = "TestLastName",
            email = "TestEmail",
            password = "TestPassword",
            phone = "TestPhone",
            dateOfBirth = LocalDateTime.of(1996, 8, 19, 0, 0),
            address = Address(StreetType.AFRIKALAAN, "TestHouseNumber", "TestBox"),
            roles = listOf()
        )
    }
}