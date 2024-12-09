package rise.tiao1.buut.presentation.login

import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test
import rise.tiao1.buut.R
import rise.tiao1.buut.domain.user.useCases.LoginUseCase
import rise.tiao1.buut.domain.user.validation.ValidateEmail
import rise.tiao1.buut.domain.user.validation.ValidatePassword
import rise.tiao1.buut.utils.InputKeys
import rise.tiao1.buut.utils.UiText

@ExperimentalCoroutinesApi
class LoginViewModelTest {
    private val validateEmail: ValidateEmail = mockk()
    private val validatePassword: ValidatePassword = mockk()
    private val login: LoginUseCase = mockk()
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun initialState_isProduced() = scope.runTest {
        val viewModel = getViewModel()
        val initialState = viewModel.state.value
        assert(initialState == LoginScreenState())
    }

    @Test
    fun updateState_updateSingleValueWithCorrectValue_updatesState() = scope.runTest {
        val viewModel = getViewModel()
        val updatedState = LoginScreenState(email = "buut@buut.buut")
        viewModel.updateState {
            copy(email = updatedState.email)
        }
        assert(viewModel.state.value == updatedState)
    }

    @Test
    fun updateState_updateMultipleValuesWithCorrectValues_updatesState() = scope.runTest {
        val viewModel = getViewModel()
        val updatedState = LoginScreenState(email = "buut@buut.buut", password = "Password1!")
        viewModel.updateState {
            copy(email = updatedState.email, password = updatedState.password)
        }
        assert(viewModel.state.value == updatedState)
    }

    @Test
    fun validate_mailError_updatesState() = scope.runTest {
        val viewModel = getViewModel()
        val emailError = UiText.StringResource(resId = R.string.email_is_blank_error)

        every { validateEmail.execute("") } returns emailError
        viewModel.updateState {
            copy(email = "")

        }
        viewModel.validate("", InputKeys.EMAIL)
        assert(viewModel.state.value.emailError?.getStringId() == emailError.getStringId())
        verify { validateEmail.execute("") }

    }

    @Test
    fun validate_passwordError_updatesState() = scope.runTest {
        val viewModel = getViewModel()
        val passwordError = UiText.StringResource(resId = R.string.password_not_valid_error)
        every { validatePassword.execute("") } returns passwordError
        viewModel.updateState {
            copy(password = "")
        }
        viewModel.validate("", InputKeys.PASSWORD)
        assert(viewModel.state.value.passwordError?.getStringId() == passwordError.getStringId())
        verify { validatePassword.execute("") }
    }

    @Test
    fun login_callsViewModelScope() = scope.runTest {
        val viewModel = spyk(getViewModel())
        val testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        viewModel.login {}
        advanceUntilIdle()
        verify { viewModel.login(any()) }
        Dispatchers.resetMain()
    }

    private fun getViewModel(): LoginViewModel {
        return LoginViewModel(login, validateEmail, validatePassword)
    }

}