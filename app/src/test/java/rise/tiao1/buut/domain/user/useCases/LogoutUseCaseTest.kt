package rise.tiao1.buut.domain.user.useCases

import android.content.SharedPreferences
import com.auth0.android.authentication.storage.CredentialsManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import rise.tiao1.buut.data.repositories.UserRepository
import rise.tiao1.buut.domain.user.Address
import rise.tiao1.buut.domain.user.User
import rise.tiao1.buut.utils.StreetType
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class LogoutUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    var userRepository: UserRepository = mockk()
    var credentialsManager: CredentialsManager = mockk()
    var sharedPreferences: SharedPreferences = mockk()
    var sharedPreferencesEditor: SharedPreferences.Editor = mockk()


    @Test
    fun logoutUser_returnsSuccess(): Unit = scope.runTest {
        val user = getUser()
        every { sharedPreferences.edit() } returns sharedPreferencesEditor
        every { sharedPreferencesEditor.clear() } returns sharedPreferencesEditor
        every { sharedPreferencesEditor.apply() } just runs
        every { credentialsManager.hasValidCredentials() } returns true
        coEvery { userRepository.deleteUser(any()) } just runs
        every { credentialsManager.clearCredentials() } just runs
        val logoutUseCase = LogoutUseCase(userRepository, credentialsManager, sharedPreferences)
        logoutUseCase.invoke(user)
        coVerify { userRepository.deleteUser(user) }
        verify { sharedPreferences.edit() }
        verify { sharedPreferencesEditor.clear() }
        verify { sharedPreferencesEditor.apply() }
        verify { credentialsManager.clearCredentials() }
    }

    @Test
    fun logoutUserNull_doesNothing(): Unit = scope.runTest {
        val logoutUseCase = LogoutUseCase(userRepository, credentialsManager, sharedPreferences)
        logoutUseCase.invoke(null)
        coVerify(exactly = 0) { userRepository.deleteUser(any()) }
        verify(exactly = 0) { sharedPreferencesEditor.clear() }
        verify(exactly = 0) { sharedPreferences.edit() }
        verify(exactly = 0) { credentialsManager.clearCredentials() }
    }

    fun getUser(): User {
        return User(
            id = "TestId",
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