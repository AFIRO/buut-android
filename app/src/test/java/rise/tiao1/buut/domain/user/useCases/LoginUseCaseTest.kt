package rise.tiao1.buut.domain.user.useCases

import android.content.Context
import android.content.SharedPreferences
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.CredentialsManager
import com.auth0.android.callback.Callback
import com.auth0.android.result.Credentials
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import rise.tiao1.buut.domain.user.Address
import rise.tiao1.buut.domain.user.User
import rise.tiao1.buut.utils.SharedPreferencesKeys
import rise.tiao1.buut.utils.StreetType
import java.time.LocalDateTime


@ExperimentalCoroutinesApi
class LoginUseCaseTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private val authentication: AuthenticationAPIClient = mockk()
    private val credentialsManager: CredentialsManager = mockk()
    private val sharedPreferences: SharedPreferences = mockk()
    val sharedPreferencesEditor: SharedPreferences.Editor = mockk()
    private val context: Context = mockk()


    @Test
    fun loginUseCaseFails_returnsError(): Unit = scope.runTest {
        val user = getUser()
        every { sharedPreferences.edit() } returns sharedPreferencesEditor
        every { sharedPreferencesEditor.putString(any(), any()) } returns sharedPreferencesEditor
        every { sharedPreferencesEditor.apply() } just Runs
        every { credentialsManager.saveCredentials(any()) } just Runs
        every { context.getString(any()) } returns "test_audience"
        every { authentication.login(any(), any()) } returns mockk {
            every { setAudience(any()) } returns this
            every { start(any()) } answers {
                val callback = firstArg<Callback<Credentials, AuthenticationException>>()
                val exception = mockk<AuthenticationException>(relaxed = true)
                callback.onFailure(exception)
            }
        }

        val loginUseCase =
            LoginUseCase(authentication, credentialsManager, sharedPreferences, context)

        var result = false
        loginUseCase(user.email, user.password.toString(),
            onSuccess = { result = false },
            onError = { result = true }
        )

        verify { authentication.login(user.email, user.password.toString()) }
        verify(exactly = 0) { sharedPreferences.edit() }
        verify(exactly = 0) {
            sharedPreferencesEditor.putString(
                SharedPreferencesKeys.ACCESSTOKEN,
                any()
            )
        }
        verify(exactly = 0) {
            sharedPreferencesEditor.putString(
                SharedPreferencesKeys.IDTOKEN,
                any()
            )
        }
        verify(exactly = 0) { sharedPreferencesEditor.apply() }
        verify(exactly = 0) { credentialsManager.saveCredentials(any()) }
        assertTrue(result)
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