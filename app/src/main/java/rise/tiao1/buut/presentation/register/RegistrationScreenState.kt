package rise.tiao1.buut.presentation.register
import rise.tiao1.buut.utils.StreetType
import rise.tiao1.buut.utils.UiText

data class RegistrationScreenState(
    val firstName: String = "",
    val lastName: String = "",
    val street: String = "",
    val houseNumber: String = "",
    val box: String = "",
    val phone: String = "",
    val email: String = "",
    val password: String = "",
    val repeatedPassword: String = "",
    val dateOfBirth: String = "",
    val acceptedTermsOfUsage: Boolean = false,
    val acceptedPrivacyConditions: Boolean = false,

    val firstNameError: UiText? = null,
    val lastNameError: UiText? = null,
    val streetError: UiText? = null,
    val houseNumberError: UiText? = null,
    val boxError: UiText? = null,
    val phoneError: UiText? = null,
    val emailError: UiText? = null,
    val passwordError: UiText? = null,
    val repeatedPasswordError: UiText? = null,
    val dateOfBirthError: UiText? = null,
    val termsError: UiText? = null,
    val privacyError: UiText? = null,

    val isLoading: Boolean = false,
    val formHasErrors: Boolean= false,
    val registrationSuccess: Boolean = false,

    val apiError: String = "",

    val isNetworkAvailable: Boolean = true
)
