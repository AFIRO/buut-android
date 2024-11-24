package rise.tiao1.buut.presentation.editProfile


import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rise.tiao1.buut.domain.user.validation.ValidateDateOfBirth
import rise.tiao1.buut.domain.user.validation.ValidateEmail
import rise.tiao1.buut.domain.user.validation.ValidateFirstName
import rise.tiao1.buut.domain.user.validation.ValidateHouseNumber
import rise.tiao1.buut.domain.user.validation.ValidateLastName
import rise.tiao1.buut.domain.user.validation.ValidatePhone
import rise.tiao1.buut.domain.user.validation.ValidateStreet
import rise.tiao1.buut.utils.InputKeys
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import rise.tiao1.buut.data.di.MainDispatcher
import rise.tiao1.buut.domain.user.useCases.GetUserUseCase
import javax.inject.Inject
import androidx.compose.runtime.State
import rise.tiao1.buut.domain.user.Address
import rise.tiao1.buut.domain.user.User
import rise.tiao1.buut.domain.user.useCases.UpdateUserUseCase
import rise.tiao1.buut.utils.StreetType
import rise.tiao1.buut.utils.toLocalDateTime
import kotlinx.coroutines.delay



@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val validateFirstName: ValidateFirstName,
    private val validateLastName: ValidateLastName,
    private val validateEmail: ValidateEmail,
    private val validateStreet: ValidateStreet,
    private val validateHouseNumber: ValidateHouseNumber,
    private val validateDateOfBirth: ValidateDateOfBirth,
    private val validatePhone: ValidatePhone,
    @MainDispatcher private val dispatcher: CoroutineDispatcher
): ViewModel() {

    val TAG = "EditProfileViewModel"

    private val _state = mutableStateOf(EditProfileScreenState())
    val state: State<EditProfileScreenState>
        get() = _state

    init {
        viewModelScope.launch(dispatcher) {
            getCurrentUser()
            fillInUserDetails()
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private fun fillInUserDetails() {
       _state.value = state.value.copy(
           firstName = state.value.user?.firstName ?: "",
           lastName = state.value.user?.lastName ?: "",
           email = state.value.user?.email ?: "",
           phone = state.value.user?.phone ?: "",
           dateOfBirth = (state.value.user?.dateOfBirth?.toLocalDate().toString()),
           street = state.value.user?.address?.street?.streetName ?: "",
           houseNumber = state.value.user?.address?.houseNumber ?: "",
           box = state.value.user?.address?.box ?: "",

       )
    }


    suspend fun getCurrentUser() {
        _state.value = state.value.copy(isLoading = true)

        try {
            val user = getUserUseCase()
            _state.value = state.value.copy(isLoading = false, user = user)
        } catch (e: Exception) {
            _state.value = state.value.copy(
                isLoading = false,
                apiError = e.message
            )
        }

    }

    fun updateState(update: EditProfileScreenState.() -> EditProfileScreenState) {
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
                else -> EditProfileScreenState()
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
                InputKeys.DATE_OF_BIRTH -> copy(dateOfBirthError = validateDateOfBirth.execute(_state.value.dateOfBirth))
                      else -> copy()
            }
        }
    }




    fun onConfirmClick(navigateToProfile: ()->Unit) {

        listOfInputKeys.forEach { validate(it) }

        // Rebuild the error list dynamically
        val updatedErrors = listOf(
            _state.value.firstNameError,
            _state.value.lastNameError,
            _state.value.emailError,
            _state.value.phoneError,
            _state.value.streetError,
            _state.value.houseNumberError,
            _state.value.boxError,
            _state.value.dateOfBirthError,
        )

        // check for errors.
        _state.value = state.value.copy(formHasErrors = updatedErrors.any { it != null })

        // if all inputs are correctly validated send the update
        if(!_state.value.formHasErrors) {
            _state.value= state.value.copy(isLoading = true)
            val address =
                Address(
                    street = StreetType.fromString(_state.value.street),
                    houseNumber = _state.value.houseNumber,
                    box = _state.value.box
                )
            val newUser = User(
                    id = _state.value.user?.id.toString(),
                    firstName = _state.value.firstName,
                    lastName = _state.value.lastName,
                    email = _state.value.email,
                    phone = _state.value.phone,
                    dateOfBirth = _state.value.dateOfBirth.toLocalDateTime(),
                    address = address,
                    roles = _state.value.user?.roles ?: emptyList()
                )

            viewModelScope.launch {

                updateUserUseCase(
                    newUser,
                    onSuccess = {
                        _state.value = state.value.copy(isLoading = false, registrationSuccess = true)
                        navigateToProfile()
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

    fun onCancelClick(navigateToProfile: ()->Unit) {
        navigateToProfile()
    }

    fun onRegistrationSuccessDismissed(navigateToHome: () -> Unit) {
        _state.value = state.value.copy(registrationSuccess = false)
        navigateToHome()
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
    )
}
