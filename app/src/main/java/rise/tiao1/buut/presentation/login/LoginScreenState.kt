package rise.tiao1.buut.presentation.login

import rise.tiao1.buut.utils.UiText

data class LoginScreenState (
    val email: String = "",
    val password: String = "",

    val emailError: UiText? = null,
    val passwordError: UiText? = null,

    val apiError: String = "",
    val isLoading: Boolean = false,

    val isNetworkAvailable: Boolean = true
)