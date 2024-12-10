package rise.tiao1.buut.presentation.register

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rise.tiao1.buut.utils.StreetType
import rise.tiao1.buut.domain.user.Address
import rise.tiao1.buut.domain.user.Role
import rise.tiao1.buut.domain.user.User
import rise.tiao1.buut.domain.user.useCases.RegisterUserUseCase
import rise.tiao1.buut.domain.user.validation.ValidateBox
import rise.tiao1.buut.domain.user.validation.ValidateDateOfBirth
import rise.tiao1.buut.domain.user.validation.ValidateEmail
import rise.tiao1.buut.domain.user.validation.ValidateFirstName
import rise.tiao1.buut.domain.user.validation.ValidateHouseNumber
import rise.tiao1.buut.domain.user.validation.ValidateLastName
import rise.tiao1.buut.domain.user.validation.ValidatePassword
import rise.tiao1.buut.domain.user.validation.ValidatePhone
import rise.tiao1.buut.domain.user.validation.ValidatePrivacy
import rise.tiao1.buut.domain.user.validation.ValidateRepeatedPassword
import rise.tiao1.buut.domain.user.validation.ValidateStreet
import rise.tiao1.buut.domain.user.validation.ValidateTerms
import rise.tiao1.buut.utils.InputKeys
import rise.tiao1.buut.utils.toApiDateString
import rise.tiao1.buut.utils.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val validateFirstName: ValidateFirstName,
    private val validateLastName: ValidateLastName,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
    private val validateRepeatedPassword: ValidateRepeatedPassword,
    private val validateStreet: ValidateStreet,
    private val validateHouseNumber: ValidateHouseNumber,
    private val validateBox: ValidateBox,
    private val validateDateOfBirth: ValidateDateOfBirth,
    private val validatePhone: ValidatePhone,
    private val validateTerms: ValidateTerms,
    private val validatePrivacy: ValidatePrivacy,
    private val registerUserUseCase: RegisterUserUseCase
): ViewModel() {

    private val _state = mutableStateOf(RegistrationScreenState())
    val state: State<RegistrationScreenState>
        get() = _state

    fun updateState(update: RegistrationScreenState.() -> RegistrationScreenState) {
        _state.value = state.value.update()
    }

    fun update(input: String, field: String) {
        updateState {
            when(field) {
                InputKeys.FIRST_NAME -> copy(firstName = input)
                InputKeys.LAST_NAME -> copy(lastName = input)
                InputKeys.EMAIL -> copy(email = input)
                InputKeys.PHONE -> copy(phone = input)
                InputKeys.STREET -> copy(street = input)
                InputKeys.HOUSE_NUMBER -> copy(houseNumber = input)
                InputKeys.BOX -> copy(box = input)
                InputKeys.DATE_OF_BIRTH -> copy(dateOfBirth = input)
                InputKeys.PASSWORD -> copy(password = input)
                InputKeys.REPEATED_PASSWORD -> copy(repeatedPassword = input)
                else -> RegistrationScreenState()
            }
        }
    }

    fun update(input: Boolean, field: String) {
        updateState {
            when (field) {
                InputKeys.TERMS -> copy(acceptedTermsOfUsage = input)
                InputKeys.PRIVACY -> copy(acceptedPrivacyConditions = input)
                else -> RegistrationScreenState()
            }
        }
    }

    fun validate(field: String) {
        updateState {
            when(field) {
                InputKeys.FIRST_NAME -> copy(firstNameError = validateFirstName.execute(_state.value.firstName))
                InputKeys.LAST_NAME -> copy(lastNameError = validateLastName.execute(_state.value.lastName))
                InputKeys.EMAIL -> copy(emailError = validateEmail.execute(_state.value.email))
                InputKeys.PHONE -> copy(phoneError = validatePhone.execute(_state.value.phone))
                InputKeys.STREET -> copy(streetError = validateStreet.execute(_state.value.street))
                InputKeys.HOUSE_NUMBER -> copy(houseNumberError = validateHouseNumber.execute(_state.value.houseNumber))
                InputKeys.BOX -> copy(boxError = validateBox.execute(_state.value.box))
                InputKeys.DATE_OF_BIRTH -> copy(dateOfBirthError = validateDateOfBirth.execute(_state.value.dateOfBirth))
                InputKeys.PASSWORD -> copy(passwordError = validatePassword.execute(_state.value.password))
                InputKeys.REPEATED_PASSWORD ->copy(repeatedPasswordError = validateRepeatedPassword.execute(_state.value.password, _state.value.repeatedPassword))
                InputKeys.TERMS -> copy(termsError = validateTerms.execute(_state.value.acceptedTermsOfUsage))
                InputKeys.PRIVACY -> copy(privacyError = validatePrivacy.execute(_state.value.acceptedPrivacyConditions))
                else -> RegistrationScreenState()
            }
        }
    }

    fun onRegisterClick() {

        listOfInputKeys.forEach { validate(it) }

        _state.value = state.value.copy(formHasErrors = listOfErrors.any { it != null })

        if(!_state.value.formHasErrors) {
            _state.value= state.value.copy(isLoading = true)
            val address =
                Address(
                    street = StreetType.fromString(_state.value.street),
                    houseNumber = _state.value.houseNumber,
                    box = _state.value.box
                )
            val newUser =
                User(
                    firstName = _state.value.firstName,
                    lastName = _state.value.lastName,
                    email = _state.value.email,
                    password = _state.value.password,
                    phone = _state.value.phone,
                    dateOfBirth = _state.value.dateOfBirth.toLocalDateTime(),
                    address = address,
                    roles = listOf(Role("")),
                )

            viewModelScope.launch {
                registerUserUseCase(
                        newUser,
                        onSuccess = {
                            _state.value = state.value.copy(isLoading = false, registrationSuccess = true)
                        },
                    onError = { error ->
                        _state.value = state.value.copy(
                            isLoading = false,
                            apiError = error
                        )
                        })
            }
        }
    }

    fun onRegistrationSuccessDismissed(navigateToHome: () -> Unit) {
        _state.value = state.value.copy(registrationSuccess = false)
        navigateToHome()
    }

    fun onNetworkStatusChange(isAvailable: Boolean) {
        _state.value = state.value.copy(isNetworkAvailable = isAvailable)
    }

    private val listOfInputKeys = listOf(
        InputKeys.FIRST_NAME,
        InputKeys.LAST_NAME,
        InputKeys.EMAIL,
        InputKeys.PHONE,
        InputKeys.STREET,
        InputKeys.HOUSE_NUMBER,
        InputKeys.BOX,
        InputKeys.DATE_OF_BIRTH,
        InputKeys.PASSWORD,
        InputKeys.REPEATED_PASSWORD,
        InputKeys.TERMS,
        InputKeys.PRIVACY
    )

    private val listOfErrors = listOf(
        _state.value.firstNameError,
        _state.value.lastNameError,
        _state.value.emailError,
        _state.value.phoneError,
        _state.value.streetError,
        _state.value.houseNumberError,
        _state.value.boxError,
        _state.value.dateOfBirthError,
        _state.value.passwordError,
        _state.value.repeatedPasswordError,
        _state.value.termsError,
        _state.value.privacyError
    )
}

