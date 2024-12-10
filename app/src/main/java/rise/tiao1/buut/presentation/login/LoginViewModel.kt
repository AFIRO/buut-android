package rise.tiao1.buut.presentation.login

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rise.tiao1.buut.domain.user.useCases.LoginUseCase
import rise.tiao1.buut.domain.user.validation.ValidateEmail
import rise.tiao1.buut.domain.user.validation.ValidatePassword
import rise.tiao1.buut.utils.InputKeys
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val login: LoginUseCase,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
): ViewModel() {
    private val _state = mutableStateOf(LoginScreenState())
    val state : State<LoginScreenState> get() = _state


    fun updateState(update: LoginScreenState.() -> LoginScreenState) {
        _state.value = state.value.update()
    }

    fun update(input: String, field: String) {
        updateState {
            when (field) {
                InputKeys.EMAIL -> copy(email = input)
                InputKeys.PASSWORD -> copy(password = input)
                else -> LoginScreenState()
            }
        }
    }

    fun validate(input: String, field: String) {
        updateState {
            when (field) {
                InputKeys.EMAIL-> copy(emailError = validateEmail.execute(input))
                InputKeys.PASSWORD -> copy(passwordError = validatePassword.execute(input))
                else -> LoginScreenState()
            }
        }
    }


    fun login(navigateToHome: ()->Unit) {
        _state.value = state.value.copy(isLoading = true)
        viewModelScope.launch {
                login(
                    state.value.email,
                    state.value.password,
                    onSuccess = {
                        _state.value = state.value.copy(isLoading = false)
                        navigateToHome()
                    },
                    onError = { error ->
                        _state.value = state.value.copy(
                            isLoading = false,
                            apiError = error
                        )
                    }
                )
        }


    }

    fun onNetworkStatusChange(isAvailable: Boolean) {
        _state.value = state.value.copy(isNetworkAvailable = isAvailable)
    }
}