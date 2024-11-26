package rise.tiao1.buut.presentation.editProfile

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import rise.tiao1.buut.R
import rise.tiao1.buut.domain.user.Address
import rise.tiao1.buut.domain.user.User
import rise.tiao1.buut.domain.user.useCases.GetUserUseCase
import rise.tiao1.buut.domain.user.useCases.UpdateUserUseCase
import rise.tiao1.buut.domain.user.validation.ValidateDateOfBirth
import rise.tiao1.buut.domain.user.validation.ValidateEmail
import rise.tiao1.buut.domain.user.validation.ValidateFirstName
import rise.tiao1.buut.domain.user.validation.ValidateHouseNumber
import rise.tiao1.buut.domain.user.validation.ValidateLastName
import rise.tiao1.buut.domain.user.validation.ValidatePhone
import rise.tiao1.buut.domain.user.validation.ValidateStreet
import rise.tiao1.buut.utils.InputKeys
import rise.tiao1.buut.utils.StreetType
import rise.tiao1.buut.utils.UiText
import java.time.LocalDateTime


@ExperimentalCoroutinesApi
class EditProfileViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)
    private val mockGetUserUseCase: GetUserUseCase = mockk()
    private val updateUser: UpdateUserUseCase = mockk()
    private val validateFirstName: ValidateFirstName = mockk()
    private val validateLastName: ValidateLastName = mockk()
    private val validateEmail: ValidateEmail = mockk()
    private val validateStreet: ValidateStreet = mockk()
    private val validateHouseNumber: ValidateHouseNumber = mockk()
    private val validateDateOfBirth: ValidateDateOfBirth = mockk()
    private val validatePhone: ValidatePhone = mockk()
    private val testError = "TestError"
    private val viewModel = EditProfileViewModel(
        getUserUseCase = mockGetUserUseCase,
        updateUserUseCase = updateUser,
        validateFirstName = validateFirstName,
        validateLastName = validateLastName,
        validateEmail = validateEmail,
        validateStreet = validateStreet,
        validateHouseNumber = validateHouseNumber,
        validateDateOfBirth = validateDateOfBirth,
        validatePhone = validatePhone,
        dispatcher = dispatcher
    )

    @Test
    fun initialState_isProduced() = scope.runTest {

        val user = User(
            id = "TestId",
            firstName = "TestFirstName",
            lastName = "TestLastName",
            email = "Test@Test.be",
            password = "TestPassword",
            phone = "TestPhoneNumber",
            dateOfBirth = LocalDateTime.of(1996, 8, 19, 0, 0),
            address = Address(StreetType.AFRIKALAAN, "TestHouseNumber", "TestBox"),
            roles = listOf()
        )
        // setup mock
        coEvery { mockGetUserUseCase.invoke() } returns user

        val viewModel = EditProfileViewModel(
            getUserUseCase = mockGetUserUseCase,
            updateUserUseCase = updateUser,
            validateFirstName = validateFirstName,
            validateLastName = validateLastName,
            validateEmail = validateEmail,
            validateStreet = validateStreet,
            validateHouseNumber = validateHouseNumber,
            validateDateOfBirth = validateDateOfBirth,
            validatePhone = validatePhone,
            dispatcher = dispatcher
        )
        // Ensure the async operation is completed
        dispatcher.scheduler.advanceUntilIdle()

        // create the initial state
        val initialState = viewModel.state.value

        // Verify that GetUserUseCase was called
        coVerify { mockGetUserUseCase.invoke() }

        // Check the initial flags
        assertFalse(initialState.isLoading)
        assertFalse(initialState.formHasErrors)
        assertFalse(initialState.updateSuccess)

        // check setting of data fields
        assertEquals("TestFirstName", viewModel.state.value.firstName)
        assertEquals("TestLastName", viewModel.state.value.lastName)
        assertEquals("Afrikalaan", viewModel.state.value.street)
        assertEquals("TestBox", viewModel.state.value.box)
        assertEquals("TestHouseNumber", viewModel.state.value.houseNumber)
        assertEquals("TestPhoneNumber", viewModel.state.value.phone)
        assertEquals("Test@Test.be", viewModel.state.value.email)
        assertEquals("1996-08-19", viewModel.state.value.dateOfBirth)

        // Check error fields are empty (as expected in initial state)
        assertNull(initialState.firstNameError)
        assertNull(initialState.lastNameError)
        assertNull(initialState.streetError)
        assertNull(initialState.houseNumberError)
        assertNull(initialState.boxError)
        assertNull(initialState.phoneError)
        assertNull(initialState.emailError)
        assertNull(initialState.dateOfBirthError)

        // Check that the user is correctly set in the state
        assertEquals(user, initialState.user)

        // Ensure there's no API error at the start
        assertEquals("", viewModel.state.value.apiError)
    }


    @Test
    fun getCurrentUser_getUserFromUseCase_invokesGetUser() = scope.runTest {
        // setup mock
        coEvery { mockGetUserUseCase.invoke() } returns getMockUser()

        // call the viewmodel function
        viewModel.getCurrentUser()
        dispatcher.scheduler.advanceUntilIdle()

        // check invocation
        coVerify { mockGetUserUseCase.invoke() }
    }

//    @Test
//    fun fillInUserDetails_updatesUserDetails() = scope.runTest{
//        // setup mock
//        coEvery { mockGetUserUseCase.invoke() } returns getMockUser()
//
//        val viewModel = EditProfileViewModel(
//            getUserUseCase = mockGetUserUseCase,
//            updateUserUseCase = updateUser,
//            validateFirstName = validateFirstName,
//            validateLastName = validateLastName,
//            validateEmail = validateEmail,
//            validateStreet = validateStreet,
//            validateHouseNumber = validateHouseNumber,
//            validateDateOfBirth = validateDateOfBirth,
//            validatePhone = validatePhone,
//            dispatcher = dispatcher
//        )
//
//        // call the viewmodel function
//        viewModel.getCurrentUser()
//        dispatcher.scheduler.advanceUntilIdle()
//
//
//
//        // check setting of data fields
//        assertEquals("TestFirstName", viewModel.state.value.firstName)
//        assertEquals("TestLastName", viewModel.state.value.lastName)
//        assertEquals("Afrikalaan", viewModel.state.value.street)
//        assertEquals("TestBox", viewModel.state.value.box)
//        assertEquals("TestHouseNumber", viewModel.state.value.houseNumber)
//        assertEquals("TestPhoneNumber", viewModel.state.value.phone)
//        assertEquals("Test@Test.be", viewModel.state.value.email)
//        assertEquals("1996-08-19", viewModel.state.value.dateOfBirth)
//    }

    @Test
    fun getCurrentUser_getUserFromUseCase_updatesState() = scope.runTest {
        // setup mock
        coEvery { mockGetUserUseCase.invoke() } returns getMockUser()

        // call the viewmodel function
        viewModel.getCurrentUser()
        dispatcher.scheduler.advanceUntilIdle()

        // check if user is correctly set
        assertEquals(getMockUser(), viewModel.state.value.user)

        // check loading flag
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun getCurrentUser_throwsException_handlesTheException() = scope.runTest {
        // setup mock
        val errorMessage = "Unable to load user data"
        coEvery { mockGetUserUseCase() } throws Exception(errorMessage)

        // call the viewmodel function
        viewModel.getCurrentUser()
        dispatcher.scheduler.advanceUntilIdle()

        // Act
        val state = viewModel.state.value

        // verify
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.apiError)
        assertNull(state.user)

        //data fields are all empty
        assertEquals("", state.firstName)
        assertEquals("", state.lastName)
        assertEquals("", state.street)
        assertEquals("", state.houseNumber)
        assertEquals("", state.box)
        assertEquals("", state.phone)
        assertEquals("", state.email)
        assertEquals("", state.dateOfBirth)

        //error fields all empty
        assertNull(state.firstNameError)
        assertNull(state.lastNameError)
        assertNull(state.streetError)
        assertNull(state.houseNumberError)
        assertNull(state.boxError)
        assertNull(state.phoneError)
        assertNull(state.emailError)
        assertNull(state.dateOfBirthError)

    }

    @Test
    fun validate_correctFirstNameValues_updatesErrorStatesCorrectly() = scope.runTest{
        // train mock
        every { validateFirstName.execute("John") } returns null
        viewModel.update("John", InputKeys.FIRST_NAME)
        viewModel.validate(InputKeys.FIRST_NAME)

        //test invokation
        verify { validateFirstName.execute("John") }

        assertNull(viewModel.state.value.firstNameError)
    }

    @Test
    fun validate_wrongFirstNameValues_updatesErrorStatesCorrectly() = scope.runTest{
        val error = UiText.StringResource(resId = R.string.first_name_is_blank_error)

        // train mock
        every { validateFirstName.execute("John") } returns error
        viewModel.update("John", InputKeys.FIRST_NAME)
        viewModel.validate(InputKeys.FIRST_NAME)

        //test invokation
        verify { validateFirstName.execute("John") }

        assertEquals(error, viewModel.state.value.firstNameError)
    }

    @Test
    fun update_firstNameInput_updatesInputCorrectly() = scope.runTest{
        // call update
        viewModel.update("John", InputKeys.FIRST_NAME)
        assertEquals("John", viewModel.state.value.firstName)
    }

    @Test
    fun validate_correctLastName_updatesErrorStatesCorrectly() = scope.runTest {
        // train mock
        every { validateLastName.execute("Doe") } returns null
        viewModel.update("Doe", InputKeys.LAST_NAME)
        viewModel.validate(InputKeys.LAST_NAME)

        // test invokation
        verify { validateLastName.execute("Doe") }

        assertNull(viewModel.state.value.lastNameError)
    }

    @Test
    fun validate_wrongLastName_updatesErrorStatesCorrectly() = scope.runTest {
        val error = UiText.StringResource(resId = R.string.last_name_is_blank_error)

        // train mock
        every { validateLastName.execute("Doe") } returns error
        viewModel.update("Doe", InputKeys.LAST_NAME)
        viewModel.validate(InputKeys.LAST_NAME)

        // test invokation
        verify { validateLastName.execute("Doe") }

        assertEquals(error, viewModel.state.value.lastNameError)
    }

    @Test
    fun update_lastNameInput_updatesInputCorrectly() = scope.runTest {
        // call update
        viewModel.update("Doe", InputKeys.LAST_NAME)
        assertEquals("Doe", viewModel.state.value.lastName)
    }

    @Test
    fun validate_correctEmail_updatesErrorStatesCorrectly() = scope.runTest {
        // train mock
        every { validateEmail.execute("john.doe@example.com") } returns null
        viewModel.update("john.doe@example.com", InputKeys.EMAIL)
        viewModel.validate(InputKeys.EMAIL)

        // test invokation
        verify { validateEmail.execute("john.doe@example.com") }

        assertNull(viewModel.state.value.emailError)
    }

    @Test
    fun validate_wrongEmail_updatesErrorStatesCorrectly() = scope.runTest {
        val error = UiText.StringResource(resId = R.string.email_not_valid_error)

        // train mock
        every { validateEmail.execute("john.doe@example.com") } returns error
        viewModel.update("john.doe@example.com", InputKeys.EMAIL)
        viewModel.validate(InputKeys.EMAIL)

        // test invokation
        verify { validateEmail.execute("john.doe@example.com") }

        assertEquals(error, viewModel.state.value.emailError)
    }

    @Test
    fun update_emailInput_updatesInputCorrectly() = scope.runTest {
        // call update
        viewModel.update("john.doe@example.com", InputKeys.EMAIL)
        assertEquals("john.doe@example.com", viewModel.state.value.email)
    }

    @Test
    fun validate_correctStreet_updatesErrorStatesCorrectly() = scope.runTest {
        // train mock
        every { validateStreet.execute("Baker Street") } returns null
        viewModel.update("Baker Street", InputKeys.STREET)
        viewModel.validate(InputKeys.STREET)

        // test invokation
        verify { validateStreet.execute("Baker Street") }

        assertNull(viewModel.state.value.streetError)
    }

    @Test
    fun validate_wrongStreet_updatesErrorStatesCorrectly() = scope.runTest {
        val error = UiText.StringResource(resId = R.string.street_is_blank_error)

        // train mock
        every { validateStreet.execute("Baker Street") } returns error
        viewModel.update("Baker Street", InputKeys.STREET)
        viewModel.validate(InputKeys.STREET)

        // test invokation
        verify { validateStreet.execute("Baker Street") }

        assertEquals(error, viewModel.state.value.streetError)
    }

    @Test
    fun update_streetInput_updatesInputCorrectly() = scope.runTest {
        // call update
        viewModel.update("Baker Street", InputKeys.STREET)
        assertEquals("Baker Street", viewModel.state.value.street)
    }


    @Test
    fun validate_correctHouseNumber_updatesErrorStatesCorrectly() = scope.runTest {
        // train mock
        every { validateHouseNumber.execute("221B") } returns null
        viewModel.update("221B", InputKeys.HOUSE_NUMBER)
        viewModel.validate(InputKeys.HOUSE_NUMBER)

        // test invokation
        verify { validateHouseNumber.execute("221B") }

        assertNull(viewModel.state.value.houseNumberError)
    }

    @Test
    fun validate_wrongHouseNumber_updatesErrorStatesCorrectly() = scope.runTest {
        val error = UiText.StringResource(resId = R.string.invalid_house_number_error)

        // train mock
        every { validateHouseNumber.execute("221B") } returns error
        viewModel.update("221B", InputKeys.HOUSE_NUMBER)
        viewModel.validate(InputKeys.HOUSE_NUMBER)

        // test invokation
        verify { validateHouseNumber.execute("221B") }

        assertEquals(error, viewModel.state.value.houseNumberError)
    }

    @Test
    fun update_houseNumberInput_updatesInputCorrectly() = scope.runTest {
        // call update
        viewModel.update("221B", InputKeys.HOUSE_NUMBER)
        assertEquals("221B", viewModel.state.value.houseNumber)
    }


    @Test
    fun validate_correctDateOfBirth_updatesErrorStatesCorrectly() = scope.runTest {
        // train mock
        every { validateDateOfBirth.execute("01-01-1990") } returns null
        viewModel.update("01-01-1990", InputKeys.DATE_OF_BIRTH)
        viewModel.validate(InputKeys.DATE_OF_BIRTH)

        // test invokation
        verify { validateDateOfBirth.execute("01-01-1990") }

        assertNull(viewModel.state.value.dateOfBirthError)
    }

    @Test
    fun validate_wrongDateOfBirth_updatesErrorStatesCorrectly() = scope.runTest {
        val error = UiText.StringResource(resId = R.string.invalid_date_of_birth_error)

        // train mock
        every { validateDateOfBirth.execute("01-01-1990") } returns error
        viewModel.update("01-01-1990", InputKeys.DATE_OF_BIRTH)
        viewModel.validate(InputKeys.DATE_OF_BIRTH)

        // test invokation
        verify { validateDateOfBirth.execute("01-01-1990") }

        assertEquals(error, viewModel.state.value.dateOfBirthError)
    }

    @Test
    fun update_dateOfBirthInput_updatesInputCorrectly() = scope.runTest {
        // call update
        viewModel.update("01-01-1990", InputKeys.DATE_OF_BIRTH)
        assertEquals("01-01-1990", viewModel.state.value.dateOfBirth)
    }

    @Test
    fun validate_correctPhone_updatesErrorStatesCorrectly() = scope.runTest {
        // train mock
        every { validatePhone.execute("123-456-7890") } returns null
        viewModel.update("123-456-7890", InputKeys.PHONE)
        viewModel.validate(InputKeys.PHONE)

        // test invokation
        verify { validatePhone.execute("123-456-7890") }

        assertNull(viewModel.state.value.phoneError)
    }

    @Test
    fun validate_wrongPhone_updatesErrorStatesCorrectly() = scope.runTest {
        val error = UiText.StringResource(resId = R.string.invalid_phone_error)

        // train mock
        every { validatePhone.execute("123-456-7890") } returns error
        viewModel.update("123-456-7890", InputKeys.PHONE)
        viewModel.validate(InputKeys.PHONE)

        // test invokation
        verify { validatePhone.execute("123-456-7890") }

        assertEquals(error, viewModel.state.value.phoneError)
    }

    @Test
    fun update_phoneInput_updatesInputCorrectly() = scope.runTest {
        // call update
        viewModel.update("123-456-7890", InputKeys.PHONE)
        assertEquals("123-456-7890", viewModel.state.value.phone)
    }


    @Test
    fun onConfirmClick_invalidLastNameInputs_doesNotNavigate() = scope.runTest{
        every { validateFirstName.execute("") } returns null
        every { validateLastName.execute("") } returns UiText.StringResource(R.string.first_name_is_blank_error)
        every { validateEmail.execute("") } returns null
        every { validateStreet.execute("") } returns null
        every { validatePhone.execute("") } returns null
        every { validateHouseNumber.execute("") } returns null
        every { validateDateOfBirth.execute("") } returns null

        viewModel.onConfirmClick { fail("Should not navigate") }

        assertTrue(viewModel.state.value.formHasErrors)
    }
    @Test
    fun onConfirmClick_invalidFirstNameInput_doesNotNavigate() = scope.runTest{
        every { validateFirstName.execute("") } returns UiText.StringResource(R.string.first_name_is_blank_error)
        every { validateLastName.execute("") } returns null
        every { validateEmail.execute("") } returns null
        every { validateStreet.execute("") } returns null
        every { validatePhone.execute("") } returns null
        every { validateHouseNumber.execute("") } returns null
        every { validateDateOfBirth.execute("") } returns null

        viewModel.onConfirmClick { fail("Should not navigate") }

        assertTrue(viewModel.state.value.formHasErrors)
    }
    @Test
    fun onConfirmClick_invalidEmailInput_doesNotNavigate() = scope.runTest{
        every { validateFirstName.execute("") } returns null
        every { validateLastName.execute("") } returns null
        every { validateEmail.execute("") } returns UiText.StringResource(R.string.first_name_is_blank_error)
        every { validateStreet.execute("") } returns null
        every { validatePhone.execute("") } returns null
        every { validateHouseNumber.execute("") } returns null
        every { validateDateOfBirth.execute("") } returns null

        viewModel.onConfirmClick { fail("Should not navigate") }

        assertTrue(viewModel.state.value.formHasErrors)
    }
    @Test
    fun onConfirmClick_invalidStreetInput_doesNotNavigate() = scope.runTest{
        every { validateFirstName.execute("") } returns null
        every { validateLastName.execute("") } returns null
        every { validateEmail.execute("") } returns null
        every { validateStreet.execute("") } returns UiText.StringResource(R.string.first_name_is_blank_error)
        every { validatePhone.execute("") } returns null
        every { validateHouseNumber.execute("") } returns null
        every { validateDateOfBirth.execute("") } returns null

        viewModel.onConfirmClick { fail("Should not navigate") }

        assertTrue(viewModel.state.value.formHasErrors)
    }
    @Test
    fun onConfirmClick_invalidPhoneInput_doesNotNavigate() = scope.runTest{
        every { validateFirstName.execute("") } returns null
        every { validateLastName.execute("") } returns null
        every { validateEmail.execute("") } returns null
        every { validateStreet.execute("") } returns null
        every { validatePhone.execute("") } returns UiText.StringResource(R.string.first_name_is_blank_error)
        every { validateHouseNumber.execute("") } returns null
        every { validateDateOfBirth.execute("") } returns null

        viewModel.onConfirmClick { fail("Should not navigate") }

        assertTrue(viewModel.state.value.formHasErrors)
    }
    @Test
    fun onConfirmClick_invalidHouseNumerInput_doesNotNavigate() = scope.runTest{
        every { validateFirstName.execute("") } returns null
        every { validateLastName.execute("") } returns null
        every { validateEmail.execute("") } returns null
        every { validateStreet.execute("") } returns null
        every { validatePhone.execute("") } returns null
        every { validateHouseNumber.execute("") } returns UiText.StringResource(R.string.first_name_is_blank_error)
        every { validateDateOfBirth.execute("") } returns null

        viewModel.onConfirmClick { fail("Should not navigate") }

        assertTrue(viewModel.state.value.formHasErrors)
    }

//    @Test
//    fun onConfirmClick_validInputs_doesNavigate() = scope.runTest {
//
//        // validator mocking
//        every { validateFirstName.execute("TestFirstName") } returns null
//        every { validateLastName.execute("TestLastName") } returns null
//        every { validateEmail.execute("Test@Test.be") } returns null
//        every { validateStreet.execute("Afrikalaan") } returns null
//        every { validatePhone.execute("TestPhoneNumber") } returns null
//        every { validateHouseNumber.execute("TestHouseNumber") } returns null
//        every { validateDateOfBirth.execute("1996-08-19") } returns null
//
//
//        val viewModel = EditProfileViewModel(
//            getUserUseCase = mockGetUserUseCase,
//            updateUserUseCase = updateUser,
//            validateFirstName = validateFirstName,
//            validateLastName = validateLastName,
//            validateEmail = validateEmail,
//            validateStreet = validateStreet,
//            validateHouseNumber = validateHouseNumber,
//            validateDateOfBirth = validateDateOfBirth,
//            validatePhone = validatePhone,
//            dispatcher = dispatcher
//        )
//        // update user mocking
//        coEvery { updateUser(any(), any(), any()) } answers {
//            secondArg<(Unit) -> Unit>().invoke(Unit) // Trigger onSuccess (second argument)
//        }
//
//        // Create a mock for navigateToProfile
//        var navigateCalled = false
//        val navigateToProfileMock: () -> Unit = {
//            navigateCalled = true
//        }
//
//        //updating all data
//        viewModel.update("TestFirstName", InputKeys.FIRST_NAME)
//        viewModel.update("TestLastName", InputKeys.LAST_NAME)
//        viewModel.update("Test@Test.be", InputKeys.EMAIL)
//        viewModel.update("Afrikalaan", InputKeys.STREET)
//        viewModel.update("TestHouseNumber", InputKeys.HOUSE_NUMBER)
//        viewModel.update("1996-08-19", InputKeys.DATE_OF_BIRTH)
//        viewModel.update("TestPhoneNumber", InputKeys.PHONE)
//
//        // Call onConfirmClick and pass the mocked navigateToProfile
//
//        viewModel.onConfirmClick {}
//        dispatcher.scheduler.advanceUntilIdle()
//
//        // Verify that updateUser was called
//        coVerify { updateUser(any(), any(), any()) }
//
//        // Check if the navigateToProfile was called
//        assertTrue(navigateCalled)
//
//        // Ensure the state was updated correctly
//        assertFalse(viewModel.state.value.formHasErrors)
//        assertTrue(viewModel.state.value.updateSuccess)
//    }


    @Test
    fun onCancelClick_invokesMethod() = scope.runTest{

        // Create a mock for navigateToProfile
        var navigateCalled = false
        val navigateToProfileMock: () -> Unit = {
            navigateCalled = true
        }
        viewModel.onCancelClick(navigateToProfileMock)

        assertTrue(navigateCalled)
    }




    fun getMockUser(): User {
        return User(
            id = "TestId",
            firstName = "TestFirstName",
            lastName = "TestLastName",
            email = "Test@Test.be",
            password = "TestPassword",
            phone = "TestPhoneNumber",
            dateOfBirth = LocalDateTime.of(1996, 8, 19, 0, 0),
            address = Address(StreetType.AFRIKALAAN, "TestHouseNumber", "TestBox"),
            roles = listOf()
        )
    }

}