package rise.tiao1.buut.presentation.register

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
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
import rise.tiao1.buut.domain.user.useCases.RegisterUserUseCase
import rise.tiao1.buut.domain.user.validation.LOWEST_POSSIBLE_HOUSE_NUMBER
import rise.tiao1.buut.domain.user.validation.MINIMUM_AGE
import rise.tiao1.buut.domain.user.validation.PASSWORD_LENGTH
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
import rise.tiao1.buut.utils.StreetType
import rise.tiao1.buut.utils.UiText

@ExperimentalCoroutinesApi
class RegistrationViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)
    private val validateFirstName: ValidateFirstName = mockk()
    private val validateLastName: ValidateLastName = mockk()
    private val validateEmail: ValidateEmail = mockk()
    private val validatePassword: ValidatePassword = mockk()
    private val validateRepeatedPassword: ValidateRepeatedPassword = mockk()
    private val validateStreet: ValidateStreet = mockk()
    private val validateHouseNumber: ValidateHouseNumber = mockk()
    private val validateBox: ValidateBox = mockk()
    private val validateDateOfBirth: ValidateDateOfBirth = mockk()
    private val validatePhone: ValidatePhone = mockk()
    private val validateTerms: ValidateTerms = mockk()
    private val validatePrivacy: ValidatePrivacy = mockk()
    private val registerUserUseCase: RegisterUserUseCase = mockk()

    @Test
    fun initialState_isProduced() = scope.runTest {
        val viewModel = getViewModel()
        val initialState = viewModel.state.value
        assert(initialState == RegistrationScreenState())
    }

    @Test
    fun updateState_updateSingleValueWithCorrectValue_updatesState() = scope.runTest {
        val viewModel = getViewModel()
        val updatedState = RegistrationScreenState(firstName = "Jan")
        viewModel.updateState {
            copy(firstName = updatedState.firstName)
        }
        assert(viewModel.state.value == updatedState)
    }

    @Test
    fun updateState_updateMultipleValuesWithCorrectValues_updatesState() = scope.runTest {
        val viewModel = getViewModel()
        val updatedState = RegistrationScreenState(firstName = "Jan", lastName = "Jansen")
        viewModel.updateState {
            copy(firstName = updatedState.firstName, lastName = updatedState.lastName)
        }
        assert(viewModel.state.value == updatedState)
    }

    @Test
    fun updateState_updateAllValuesWithCorrectValues_updatesState() = scope.runTest {
        val viewModel = getViewModel()
        val updatedState = RegistrationScreenState(
            firstName = "Jan",
            lastName = "Jansen",
            email = "william.henry.harrison@example-pet-store.com",
            phone = "0612345678",
            street = StreetType.AFRIKALAAN.streetName,
            houseNumber = "123",
            box = "12",
            dateOfBirth = "01-01-1990",
            password = "Password1!",
            repeatedPassword = "Password1!",
            acceptedTermsOfUsage = true,
            acceptedPrivacyConditions = true
        )
        viewModel.updateState {
            copy(
                firstName = updatedState.firstName,
                lastName = updatedState.lastName,
                email = updatedState.email,
                phone = updatedState.phone,
                street = updatedState.street,
                houseNumber = updatedState.houseNumber,
                box = updatedState.box,
                dateOfBirth = updatedState.dateOfBirth,
                password = updatedState.password,
                repeatedPassword = updatedState.repeatedPassword,
                acceptedTermsOfUsage = updatedState.acceptedTermsOfUsage,
                acceptedPrivacyConditions = updatedState.acceptedPrivacyConditions
            )
        }
        assert(viewModel.state.value == updatedState)
    }

    @Test
    fun validate_firstNameError_updatesStateWithCorrectError() = scope.runTest {
        val viewModel = getViewModel()
        val firstNameError = UiText.StringResource(resId = R.string.first_name_is_blank_error)
        every { validateFirstName.execute("") } returns firstNameError
        viewModel.updateState {
            copy(firstName = "")
        }
        viewModel.validate("firstName")
        assert(viewModel.state.value.firstNameError?.getStringId() == firstNameError.getStringId())
        verify { validateFirstName.execute("") }
    }

    @Test
    fun validate_lastNameError_updatesStateWithCorrectError() = scope.runTest {
        val viewModel = getViewModel()
        val lastNameError = UiText.StringResource(resId = R.string.last_name_is_blank_error)
        every { validateLastName.execute("") } returns lastNameError
        viewModel.updateState {
            copy(lastName = "")
        }
        viewModel.validate("lastName")
        assert(viewModel.state.value.lastNameError?.getStringId() == lastNameError.getStringId())
        verify { validateLastName.execute("") }
    }

    @Test
    fun validate_emailError_updatesStateWithCorrectError() = scope.runTest {
        val viewModel = getViewModel()
        val emailError = UiText.StringResource(resId = R.string.email_is_blank_error)
        every { validateEmail.execute("") } returns emailError
        viewModel.updateState {
            copy(email = "")

        }
        viewModel.validate("email")
        assert(viewModel.state.value.emailError?.getStringId() == emailError.getStringId())
        verify { validateEmail.execute("") }
    }

    @Test
    fun validate_phoneError_updatesStateWithCorrectError() = scope.runTest {
        val viewModel = getViewModel()
        val phoneError = UiText.StringResource(resId = R.string.invalid_phone_error)
        every { validatePhone.execute("1234567") } returns phoneError
        viewModel.updateState {
            copy(phone = "1234567")
        }
        viewModel.validate("phone")
        assert(viewModel.state.value.phoneError?.getStringId() == phoneError.getStringId())
        verify { validatePhone.execute("1234567") }
    }

    @Test
    fun validate_streetError_updatesStateWithCorrectError() = scope.runTest {
        val viewModel = getViewModel()
        val streetError = UiText.StringResource(resId = R.string.street_is_blank_error)
        every { validateStreet.execute("") } returns streetError
        viewModel.updateState {
            copy(street = "")
        }
        viewModel.validate("street")
        assert(viewModel.state.value.streetError?.getStringId() == streetError.getStringId())
        verify { validateStreet.execute("") }
    }

    @Test
    fun validate_houseNumberError_updatesStateWithCorrectError() = scope.runTest {
        val viewModel = getViewModel()
        val houseNumberError = UiText.StringResource(
            resId = R.string.invalid_house_number_error,
            LOWEST_POSSIBLE_HOUSE_NUMBER
        )
        every { validateHouseNumber.execute("0") } returns houseNumberError
        viewModel.updateState {
            copy(houseNumber = "0")
        }
        viewModel.validate("houseNumber")
        assert(viewModel.state.value.houseNumberError?.getStringId() == houseNumberError.getStringId())
        verify { validateHouseNumber.execute("0") }
    }

    @Test
    fun validate_boxError_updatesStateWithCorrectError() = scope.runTest {
        val viewModel = getViewModel()
        val boxError = UiText.StringResource(resId = R.string.invalid_box)
        every { validateBox.execute("A") } returns boxError
        viewModel.updateState {
            copy(box = "A")
        }
        viewModel.validate("box")
        assert(viewModel.state.value.boxError?.getStringId() == boxError.getStringId())
        verify { validateBox.execute("A") }
    }

    @Test
    fun validate_dateOfBirthError_updatesStateWithCorrectError() = scope.runTest {
        val viewModel = getViewModel()
        val dateOfBirthError =
            UiText.StringResource(resId = R.string.minimum_age_error, MINIMUM_AGE)
        every { validateDateOfBirth.execute("01-01-2000") } returns dateOfBirthError
        viewModel.updateState {
            copy(dateOfBirth = "01-01-2000")
        }
        viewModel.validate("dateOfBirth")
        assert(viewModel.state.value.dateOfBirthError?.getStringId() == dateOfBirthError.getStringId())
        verify { validateDateOfBirth.execute("01-01-2000") }
    }

    @Test
    fun validate_passwordError_updatesStateWithCorrectError() = scope.runTest {
        val viewModel = getViewModel()
        val passwordError =
            UiText.StringResource(resId = R.string.invalid_password_length_error, PASSWORD_LENGTH)
        every { validatePassword.execute("1234567") } returns passwordError
        viewModel.updateState {
            copy(password = "1234567")
        }
        viewModel.validate("password")
        assert(viewModel.state.value.passwordError?.getStringId() == passwordError.getStringId())
        verify { validatePassword.execute("1234567") }
    }

    @Test
    fun validate_passwordRepeatedError_updatesStateWithCorrectError() = scope.runTest {
        val viewModel = getViewModel()
        val passwordRepeatedError = UiText.StringResource(resId = R.string.repeated_password_error)
        every {
            validateRepeatedPassword.execute(
                "1234567",
                "1234568"
            )
        } returns passwordRepeatedError
        viewModel.updateState {
            copy(password = "1234567", repeatedPassword = "1234568")
        }
        viewModel.validate("repeatedPassword")
        assert(viewModel.state.value.repeatedPasswordError?.getStringId() == passwordRepeatedError.getStringId())
        verify { validateRepeatedPassword.execute("1234567", "1234568") }
    }

    @Test
    fun validate_termsError_updatesStateWithCorrectError() = scope.runTest {
        val viewModel = getViewModel()
        val termsError = UiText.StringResource(resId = R.string.terms_not_accepted_error)
        every { validateTerms.execute(false) } returns termsError
        viewModel.updateState {
            copy(acceptedTermsOfUsage = false)
        }
        viewModel.validate("terms")
        assert(viewModel.state.value.termsError?.getStringId() == termsError.getStringId())
        verify { validateTerms.execute(false) }
    }

    @Test
    fun validate_privacyError_updatesStateWithCorrectError() = scope.runTest {
        val viewModel = getViewModel()
        val privacyError = UiText.StringResource(resId = R.string.privacy_not_accepted_error)
        every { validatePrivacy.execute(false) } returns privacyError
        viewModel.updateState {
            copy(acceptedPrivacyConditions = false)
        }
        viewModel.validate("privacy")
        assert(viewModel.state.value.privacyError?.getStringId() == privacyError.getStringId())
        verify { validatePrivacy.execute(false) }
    }

    @Test
    fun validate_allErrors_updatesStateWithCorrectErrors() = scope.runTest {
        val viewModel = getViewModel()
        val firstNameError = UiText.StringResource(resId = R.string.first_name_is_blank_error)
        val lastNameError = UiText.StringResource(resId = R.string.last_name_is_blank_error)
        val emailError = UiText.StringResource(resId = R.string.email_is_blank_error)
        val phoneError = UiText.StringResource(resId = R.string.invalid_phone_error)
        val streetError = UiText.StringResource(resId = R.string.street_is_blank_error)
        val houseNumberError = UiText.StringResource(
            resId = R.string.invalid_house_number_error,
            LOWEST_POSSIBLE_HOUSE_NUMBER
        )
        val boxError = UiText.StringResource(resId = R.string.invalid_box)
        val dateOfBirthError =
            UiText.StringResource(resId = R.string.minimum_age_error, MINIMUM_AGE)
        val passwordError =
            UiText.StringResource(resId = R.string.invalid_password_length_error, PASSWORD_LENGTH)
        val passwordRepeatedError = UiText.StringResource(resId = R.string.repeated_password_error)
        val termsError = UiText.StringResource(resId = R.string.terms_not_accepted_error)
        val privacyError = UiText.StringResource(resId = R.string.privacy_not_accepted_error)
        every { validateFirstName.execute("") } returns firstNameError
        every { validateLastName.execute("") } returns lastNameError
        every { validateEmail.execute("") } returns emailError
        every { validatePhone.execute("1234567") } returns phoneError
        every { validateStreet.execute("") } returns streetError
        every { validateHouseNumber.execute("0") } returns houseNumberError
        every { validateBox.execute("A") } returns boxError
        every { validateDateOfBirth.execute("01-01-2000") } returns dateOfBirthError
        every { validatePassword.execute("1234567") } returns passwordError
        every {
            validateRepeatedPassword.execute(
                "1234567",
                "1234568"
            )
        } returns passwordRepeatedError
        every { validateTerms.execute(false) } returns termsError
        every { validatePrivacy.execute(false) } returns privacyError
        viewModel.updateState {
            copy(
                firstName = "",
                lastName = "",
                email = "",
                phone = "1234567",
                street = "",
                houseNumber = "0",
                box = "A",
                dateOfBirth = "01-01-2000",
                password = "1234567",
                repeatedPassword = "1234568",
                acceptedTermsOfUsage = false,
                acceptedPrivacyConditions = false
            )
        }
        viewModel.validate("firstName")
        viewModel.validate("lastName")
        viewModel.validate("email")
        viewModel.validate("phone")
        viewModel.validate("street")
        viewModel.validate("houseNumber")
        viewModel.validate("box")
        viewModel.validate("dateOfBirth")
        viewModel.validate("password")
        viewModel.validate("repeatedPassword")
        viewModel.validate("terms")
        viewModel.validate("privacy")
        assert(viewModel.state.value.firstNameError?.getStringId() == firstNameError.getStringId())
        assert(viewModel.state.value.lastNameError?.getStringId() == lastNameError.getStringId())
        assert(viewModel.state.value.emailError?.getStringId() == emailError.getStringId())
        assert(viewModel.state.value.phoneError?.getStringId() == phoneError.getStringId())
        assert(viewModel.state.value.streetError?.getStringId() == streetError.getStringId())
        assert(viewModel.state.value.houseNumberError?.getStringId() == houseNumberError.getStringId())
        assert(viewModel.state.value.boxError?.getStringId() == boxError.getStringId())
        assert(viewModel.state.value.dateOfBirthError?.getStringId() == dateOfBirthError.getStringId())
        assert(viewModel.state.value.passwordError?.getStringId() == passwordError.getStringId())
        assert(viewModel.state.value.repeatedPasswordError?.getStringId() == passwordRepeatedError.getStringId())
        assert(viewModel.state.value.termsError?.getStringId() == termsError.getStringId())
        assert(viewModel.state.value.privacyError?.getStringId() == privacyError.getStringId())
        verify { validateFirstName.execute("") }
        verify { validateLastName.execute("") }
        verify { validateEmail.execute("") }
        verify { validatePhone.execute("1234567") }
        verify { validateStreet.execute("") }
        verify { validateHouseNumber.execute("0") }
        verify { validateBox.execute("A") }
        verify { validateDateOfBirth.execute("01-01-2000") }
        verify { validatePassword.execute("1234567") }
        verify { validateRepeatedPassword.execute("1234567", "1234568") }
        verify { validateTerms.execute(false) }
        verify { validatePrivacy.execute(false) }
    }

    @Test
    fun onRegisterClick_happyFlow() = scope.runTest {
        val viewModel = spyk(getViewModel())
        val updatedState = RegistrationScreenState(
            firstName = "Jan",
            lastName = "Jansen",
            email = "william.henry.harrison@example-pet-store.com",
            phone = "0612345678",
            street = StreetType.AFRIKALAAN.streetName,
            houseNumber = "123",
            box = "12",
            dateOfBirth = "01/01/1990",
            password = "Password1!",
            repeatedPassword = "Password1!",
            acceptedTermsOfUsage = true,
            acceptedPrivacyConditions = true
        )
        viewModel.updateState {
            copy(
                firstName = updatedState.firstName,
                lastName = updatedState.lastName,
                email = updatedState.email,
                phone = updatedState.phone,
                street = updatedState.street,
                houseNumber = updatedState.houseNumber,
                box = updatedState.box,
                dateOfBirth = updatedState.dateOfBirth,
                password = updatedState.password,
                repeatedPassword = updatedState.repeatedPassword,
                acceptedTermsOfUsage = updatedState.acceptedTermsOfUsage,
                acceptedPrivacyConditions = updatedState.acceptedPrivacyConditions
            )
        }
        every { validateFirstName.execute(any()) } returns null
        every { validateLastName.execute(any()) } returns null
        every { validateEmail.execute(any()) } returns null
        every { validatePhone.execute(any()) } returns null
        every { validateStreet.execute(any()) } returns null
        every { validateHouseNumber.execute(any()) } returns null
        every { validateBox.execute(any()) } returns null
        every { validateDateOfBirth.execute(any()) } returns null
        every { validatePassword.execute(any()) } returns null
        every { validateRepeatedPassword.execute(any(), any()) } returns null
        every { validateTerms.execute(any()) } returns null
        every { validatePrivacy.execute(any()) } returns null
        coEvery { registerUserUseCase.invoke(any(), any(), any()) } just Runs
        val testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        viewModel.onRegisterClick()
        advanceUntilIdle()
        verify { validateFirstName.execute(any()) }
        verify { validateLastName.execute(any()) }
        verify { validateEmail.execute(any()) }
        verify { validatePhone.execute(any()) }
        verify { validateStreet.execute(any()) }
        verify { validateHouseNumber.execute(any()) }
        verify { validateBox.execute(any()) }
        verify { validateDateOfBirth.execute(any()) }
        verify { validatePassword.execute(any()) }
        verify { validateRepeatedPassword.execute(any(), any()) }
        verify { validateTerms.execute(any()) }
        verify { validatePrivacy.execute(any()) }
        verify { viewModel.onRegisterClick() }
        Dispatchers.resetMain()
    }

    @Test
    fun onRegisterClick_errorHappens_dataNotSend() = scope.runTest {
        val viewModel = spyk(getViewModel())
        val updatedState = RegistrationScreenState(
            firstName = "Jan",
            lastName = "Jansen",
            email = "william.henry.harrison@example-pet-store.com",
            phone = "0612345678",
            street = StreetType.AFRIKALAAN.streetName,
            houseNumber = "123",
            box = "12",
            dateOfBirth = "01/01/1990",
            password = "Password1!",
            repeatedPassword = "Password1!",
            acceptedTermsOfUsage = true,
            acceptedPrivacyConditions = true
        )
        viewModel.updateState {
            copy(
                firstName = updatedState.firstName,
                lastName = updatedState.lastName,
                email = updatedState.email,
                phone = updatedState.phone,
                street = updatedState.street,
                houseNumber = updatedState.houseNumber,
                box = updatedState.box,
                dateOfBirth = updatedState.dateOfBirth,
                password = updatedState.password,
                repeatedPassword = updatedState.repeatedPassword,
                acceptedTermsOfUsage = updatedState.acceptedTermsOfUsage,
                acceptedPrivacyConditions = updatedState.acceptedPrivacyConditions
            )
        }
        val firstNameError = UiText.StringResource(resId = R.string.first_name_is_blank_error)
        every { validateFirstName.execute(any()) } returns firstNameError
        every { validateLastName.execute(any()) } returns null
        every { validateEmail.execute(any()) } returns null
        every { validatePhone.execute(any()) } returns null
        every { validateStreet.execute(any()) } returns null
        every { validateHouseNumber.execute(any()) } returns null
        every { validateBox.execute(any()) } returns null
        every { validateDateOfBirth.execute(any()) } returns null
        every { validatePassword.execute(any()) } returns null
        every { validateRepeatedPassword.execute(any(), any()) } returns null
        every { validateTerms.execute(any()) } returns null
        every { validatePrivacy.execute(any()) } returns null
        coEvery { registerUserUseCase.invoke(any(), any(), any()) } just Runs
        val testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        viewModel.onRegisterClick()
        verify { validateFirstName.execute(any()) }
        verify { validateLastName.execute(any()) }
        verify { validateEmail.execute(any()) }
        verify { validatePhone.execute(any()) }
        verify { validateStreet.execute(any()) }
        verify { validateHouseNumber.execute(any()) }
        verify { validateBox.execute(any()) }
        verify { validateDateOfBirth.execute(any()) }
        verify { validatePassword.execute(any()) }
        verify { validateRepeatedPassword.execute(any(), any()) }
        verify { validateTerms.execute(any()) }
        verify { validatePrivacy.execute(any()) }
        verify { viewModel.onRegisterClick() }
        Dispatchers.resetMain()
    }

    private fun getViewModel(): RegistrationViewModel {
        return RegistrationViewModel(
            validateFirstName = validateFirstName,
            validateLastName = validateLastName,
            validateEmail = validateEmail,
            validatePassword = validatePassword,
            validateRepeatedPassword = validateRepeatedPassword,
            validateStreet = validateStreet,
            validateHouseNumber = validateHouseNumber,
            validateBox = validateBox,
            validateDateOfBirth = validateDateOfBirth,
            validatePhone = validatePhone,
            validateTerms = validateTerms,
            validatePrivacy = validatePrivacy,
            registerUserUseCase = registerUserUseCase
        )
    }
}