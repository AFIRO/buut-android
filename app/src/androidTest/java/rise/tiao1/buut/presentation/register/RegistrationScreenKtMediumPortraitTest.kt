package rise.tiao1.buut.presentation.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput

import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.espresso.device.DeviceInteraction.Companion.setScreenOrientation
import androidx.test.espresso.device.EspressoDevice.Companion.onDevice
import androidx.test.espresso.device.action.ScreenOrientation
import androidx.test.espresso.device.rules.ScreenOrientationRule
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import rise.tiao1.buut.R
import rise.tiao1.buut.domain.user.validation.LOWEST_POSSIBLE_HOUSE_NUMBER
import rise.tiao1.buut.domain.user.validation.MINIMUM_AGE
import rise.tiao1.buut.presentation.login.LoginScreen
import rise.tiao1.buut.presentation.login.LoginScreenState
import rise.tiao1.buut.utils.InputKeys
import rise.tiao1.buut.utils.NavigationKeys
import rise.tiao1.buut.utils.StreetType
import rise.tiao1.buut.utils.UiLayout
import rise.tiao1.buut.utils.UiText


class RegistrationScreenKtMediumPortraitTest {
    val startOrientation = ScreenOrientation.PORTRAIT
    val updatedOrientation = ScreenOrientation.LANDSCAPE

    val uiLayout = UiLayout.PORTRAIT_MEDIUM

    @get:Rule
    val rule: ComposeContentTestRule =
        createComposeRule()

    @get:Rule
    val screenOrientationRule: ScreenOrientationRule = ScreenOrientationRule(startOrientation)

    val context = InstrumentationRegistry.getInstrumentation().targetContext
    var navControllerState by mutableStateOf<NavController?>(null)
    var background = rule.onNodeWithTag("BuutBackground")
    val firstNameInput = rule.onNodeWithText(context.getString(R.string.firstName))
    val lastNameInput = rule.onNodeWithText(context.getString(R.string.last_name))
    val streetInput = rule.onNodeWithTag(context.getString(R.string.street))
    val houseNumberInput = rule.onNodeWithText(context.getString(R.string.house_number))
    val boxLabelInput = rule.onNodeWithText(context.getString(R.string.box))
    val dateOfBirthInput = rule.onNodeWithText(context.getString(R.string.date_of_birth))
    val emailInput = rule.onNodeWithText(context.getString(R.string.email_label))
    val phoneInput = rule.onNodeWithText(context.getString(R.string.phone))
    val passwordInput = rule.onNodeWithText(context.getString(R.string.password))
    val confirmPasswordInput = rule.onNodeWithText(context.getString(R.string.repeated_password))
    val registerButton = rule.onNodeWithText(context.getString(R.string.register_button))
    val termsInput = rule.onNodeWithTag(context.getString(R.string.terms_of_usage))
    val privacyInput = rule.onNodeWithTag(context.getString(R.string.privacy_policy))
    val errorMessage = rule.onNodeWithText("TestError")
    val registrationSuccessModal = rule.onNodeWithTag("RegistrationSuccessModal")
    val registrationSuccessModalButton = rule.onNodeWithTag("RegistrationSuccessModalButton")

    @Before
    fun resetOrientation() {
        onDevice().setScreenOrientation(startOrientation)
    }


    @Test
    fun registrationScreen_displayCorrectly() {
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(),
                onValueChanged = { _, _ -> },
                onCheckedChanged = { _, _ -> },
                onValidate = { },
                uiLayout = uiLayout
            )
        }

        background.assertIsDisplayed()
        firstNameInput.assertIsDisplayed()
        lastNameInput.assertIsDisplayed()
        streetInput.assertIsDisplayed()
        houseNumberInput.assertIsDisplayed()
        boxLabelInput.assertIsDisplayed()
        dateOfBirthInput.assertIsDisplayed()
        emailInput.assertIsDisplayed()
        phoneInput.assertIsDisplayed()
        passwordInput.assertIsDisplayed()
        confirmPasswordInput.assertIsDisplayed()
        registerButton.assertIsDisplayed()
        termsInput.assertIsDisplayed()
        privacyInput.assertIsDisplayed()
        errorMessage.assertIsNotDisplayed()
    }

    @Test
    fun registrationScreen_streetInput_dropdownAppears() {
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(),
                onValueChanged = { _, _ -> },
                onCheckedChanged = { _, _ -> },
                onValidate = { },
                uiLayout = uiLayout
            )
        }
        streetInput.performClick()
        rule.onNodeWithText(StreetType.AFRIKALAAN.streetName).assertExists()
    }


    @Test
    fun registrationScreen_firstNameInput_UpdatesState() {
        var firstName by mutableStateOf("")
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(firstName = firstName),
                onValueChanged = { input, field ->
                    if (field == InputKeys.FIRST_NAME) {
                        firstName = input
                    }
                },
                onCheckedChanged = { _, _ -> },
                onValidate = { },
                uiLayout = uiLayout
            )
        }

        firstNameInput.performTextInput("TestFirstName")
        Assert.assertEquals("TestFirstName", firstName)
    }

    @Test
    fun registrationScreen_firstNameInputOrientationSwitch_keepsState() {
        var firstName by mutableStateOf("")
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(firstName = firstName),
                onValueChanged = { input, field ->
                    if (field == InputKeys.FIRST_NAME) {
                        firstName = input
                    }
                },
                onCheckedChanged = { _, _ -> },
                onValidate = { },
                uiLayout = uiLayout
            )
        }

        firstNameInput.performTextInput("TestFirstName")
        Assert.assertEquals("TestFirstName", firstName)
        onDevice().setScreenOrientation(updatedOrientation)
        Assert.assertEquals("TestFirstName", firstName)
    }

    @Test
    fun registrationScreen_lastNameInput_UpdatesState() {
        var lastName by mutableStateOf("")
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(lastName = lastName),
                onValueChanged = { input, field ->
                    if (field == InputKeys.LAST_NAME) {
                        lastName = input
                    }
                },
                onCheckedChanged = { _, _ -> },
                onValidate = { },
                uiLayout = uiLayout
            )
        }

        lastNameInput.performTextInput("TestLastName")
        Assert.assertEquals("TestLastName", lastName)
    }

    @Test
    fun registrationScreen_lastNameInputOrientationSwitch_UpdatesState() {
        var lastName by mutableStateOf("")
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(lastName = lastName),
                onValueChanged = { input, field ->
                    if (field == InputKeys.LAST_NAME) {
                        lastName = input
                    }
                },
                onCheckedChanged = { _, _ -> },
                onValidate = { },
                uiLayout = uiLayout
            )
        }

        lastNameInput.performTextInput("TestLastName")
        Assert.assertEquals("TestLastName", lastName)
        onDevice().setScreenOrientation(updatedOrientation)
        Assert.assertEquals("TestLastName", lastName)
    }

    @Test
    fun registrationScreen_streetInput_UpdatesState() {
        var street by mutableStateOf("")
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(street = street),
                onValueChanged = { input, field ->
                    if (field == InputKeys.STREET) {
                        street = input
                    }
                },
                onCheckedChanged = { _, _ -> },
                onValidate = { },
                uiLayout = uiLayout
            )
        }
        streetInput.performClick()
        rule.onNodeWithText(StreetType.AFRIKALAAN.streetName).performClick()
        Assert.assertEquals(StreetType.AFRIKALAAN.streetName, street)
    }

    @Test
    fun registrationScreen_streetInputOrientationChange_keepsState() {
        var street by mutableStateOf("")
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(street = street),
                onValueChanged = { input, field ->
                    if (field == InputKeys.STREET) {
                        street = input
                    }
                },
                onCheckedChanged = { _, _ -> },
                onValidate = { },
                uiLayout = uiLayout
            )
        }
        streetInput.performClick()
        rule.onNodeWithText(StreetType.AFRIKALAAN.streetName).performClick()
        Assert.assertEquals(StreetType.AFRIKALAAN.streetName, street)
        onDevice().setScreenOrientation(updatedOrientation)
        Assert.assertEquals(StreetType.AFRIKALAAN.streetName, street)
    }

    @Test
    fun registrationScreen_houseNumberInput_UpdatesState() {
        var houseNumber by mutableStateOf("")
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(houseNumber = houseNumber),
                onValueChanged = { input, field ->
                    if (field == InputKeys.HOUSE_NUMBER) {
                        houseNumber = input
                    }
                },
                onCheckedChanged = { _, _ -> },
                onValidate = { },
                uiLayout = uiLayout
            )
        }
        houseNumberInput.performTextInput("123")
        Assert.assertEquals("123", houseNumber)
    }

    @Test
    fun registrationScreen_houseNumberInputOrientationChange_keepsState() {
        var houseNumber by mutableStateOf("")
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(houseNumber = houseNumber),
                onValueChanged = { input, field ->
                    if (field == InputKeys.HOUSE_NUMBER) {
                        houseNumber = input
                    }
                },
                onCheckedChanged = { _, _ -> },
                onValidate = { },
                uiLayout = uiLayout
            )
        }
        houseNumberInput.performTextInput("123")
        Assert.assertEquals("123", houseNumber)
        onDevice().setScreenOrientation(updatedOrientation)
        Assert.assertEquals("123", houseNumber)
    }


    @Test
    fun registrationScreen_boxLabelInput_UpdatesState() {
        var boxLabel by mutableStateOf("")
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(box = boxLabel),
                onValueChanged = { input, field ->
                    if (field == InputKeys.BOX) {
                        boxLabel = input
                    }
                },
                onCheckedChanged = { _, _ -> },
                onValidate = { },
                uiLayout = uiLayout
            )
        }
        boxLabelInput.performTextInput("4")
        Assert.assertEquals("4", boxLabel)
    }

    @Test
    fun registrationScreen_boxLabelInputOrientationChange_keepsState() {
        var boxLabel by mutableStateOf("")
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(box = boxLabel),
                onValueChanged = { input, field ->
                    if (field == InputKeys.BOX) {
                        boxLabel = input
                    }
                },
                onCheckedChanged = { _, _ -> },
                onValidate = { },
                uiLayout = uiLayout
            )
        }
        boxLabelInput.performTextInput("4")
        Assert.assertEquals("4", boxLabel)
        onDevice().setScreenOrientation(updatedOrientation)
        Assert.assertEquals("4", boxLabel)
    }

    @Test
    fun registrationScreen_emailInput_UpdatesState() {
        var email by mutableStateOf("")
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(email = email),
                onValueChanged = { input, field ->
                    if (field == InputKeys.EMAIL) {
                        email = input
                    }
                },
                onCheckedChanged = { _, _ -> },
                onValidate = { },
                uiLayout = uiLayout
            )
        }
        emailInput.performTextInput("buut@buut.buut")
        Assert.assertEquals("buut@buut.buut", email)
    }

    @Test
    fun registrationScreen_emailInputOrientationChange_keepsState() {
        var email by mutableStateOf("")
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(email = email),
                onValueChanged = { input, field ->
                    if (field == InputKeys.EMAIL) {
                        email = input
                    }
                },
                onCheckedChanged = { _, _ -> },
                onValidate = { },
                uiLayout = uiLayout
            )
        }
        emailInput.performTextInput("buut@buut.buut")
        Assert.assertEquals("buut@buut.buut", email)
        onDevice().setScreenOrientation(updatedOrientation)
        Assert.assertEquals("buut@buut.buut", email)
    }

    @Test
    fun registrationScreen_phoneInput_UpdatesState() {
        var phone by mutableStateOf("")
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(phone = phone),
                onValueChanged = { input, field ->
                    if (field == InputKeys.PHONE) {
                        phone = input
                    }
                },
                onCheckedChanged = { _, _ -> },
                onValidate = { },
                uiLayout = uiLayout
            )
        }
        phoneInput.performTextInput("0612345678")
        Assert.assertEquals("0612345678", phone)
    }

    @Test
    fun registrationScreen_phoneInputOrientationChange_keepsState() {
        var phone by mutableStateOf("")
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(phone = phone),
                onValueChanged = { input, field ->
                    if (field == InputKeys.PHONE) {
                        phone = input
                    }
                },
                onCheckedChanged = { _, _ -> },
                onValidate = { },
                uiLayout = uiLayout
            )
        }
        phoneInput.performTextInput("0612345678")
        Assert.assertEquals("0612345678", phone)
        onDevice().setScreenOrientation(updatedOrientation)
        Assert.assertEquals("0612345678", phone)
    }

    @Test
    fun registrationScreen_passwordInput_UpdatesState() {
        var password by mutableStateOf("")
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(password = password),
                onValueChanged = { input, field ->
                    if (field == InputKeys.PASSWORD) {
                        password = input
                    }
                },
                onCheckedChanged = { _, _ -> },
                onValidate = { },
                uiLayout = uiLayout
            )
        }
        passwordInput.performTextInput("TestPassword")
        Assert.assertEquals("TestPassword", password)
    }

    @Test
    fun registrationScreen_passwordInputOrientationChange_KeepState() {
        var password by mutableStateOf("")
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(password = password),
                onValueChanged = { input, field ->
                    if (field == InputKeys.PASSWORD) {
                        password = input
                    }
                },
                onCheckedChanged = { _, _ -> },
                onValidate = { },
                uiLayout = uiLayout
            )
        }
        passwordInput.performTextInput("TestPassword")
        Assert.assertEquals("TestPassword", password)
        onDevice().setScreenOrientation(updatedOrientation)
        Assert.assertEquals("TestPassword", password)
    }

    @Test
    fun registrationScreen_confirmPasswordInput_UpdatesState() {
        var confirmPassword by mutableStateOf("")
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(repeatedPassword = confirmPassword),
                onValueChanged = { input, field ->
                    if (field == InputKeys.REPEATED_PASSWORD) {
                        confirmPassword = input
                    }
                },
                onCheckedChanged = { _, _ -> },
                onValidate = { },
                uiLayout = uiLayout
            )

        }
        confirmPasswordInput.performTextInput("TestPassword")
        Assert.assertEquals("TestPassword", confirmPassword)
    }

    @Test
    fun registrationScreen_confirmPasswordInputOrientationChange_keepsState() {
        var confirmPassword by mutableStateOf("")
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(repeatedPassword = confirmPassword),
                onValueChanged = { input, field ->
                    if (field == InputKeys.REPEATED_PASSWORD) {
                        confirmPassword = input
                    }
                },
                onCheckedChanged = { _, _ -> },
                onValidate = { },
                uiLayout = uiLayout
            )

        }
        confirmPasswordInput.performTextInput("TestPassword")
        Assert.assertEquals("TestPassword", confirmPassword)
        onDevice().setScreenOrientation(updatedOrientation)
        Assert.assertEquals("TestPassword", confirmPassword)
    }

    @Test
    fun registrationScreen_termsInput_UpdatesState() {
        var terms by mutableStateOf(false)
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(acceptedPrivacyConditions = terms),
                onValueChanged = { _, _ -> },
                onCheckedChanged = { input, field ->
                    if (field == InputKeys.TERMS) {
                        terms = input
                    }
                },
                onValidate = { },
                uiLayout = uiLayout
            )
        }
        termsInput.performClick()
        Assert.assertTrue(terms)
    }

    @Test
    fun registrationScreen_termsInputOrientationChange_keepsState() {
        var terms by mutableStateOf(false)
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(acceptedPrivacyConditions = terms),
                onValueChanged = { _, _ -> },
                onCheckedChanged = { input, field ->
                    if (field == InputKeys.TERMS) {
                        terms = input
                    }
                },
                onValidate = { },
                uiLayout = uiLayout
            )
        }
        termsInput.performClick()
        Assert.assertTrue(terms)
        onDevice().setScreenOrientation(updatedOrientation)
        Assert.assertTrue(terms)
    }

    @Test
    fun registrationScreen_privacyInput_UpdatesState() {
        var privacy by mutableStateOf(false)
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(acceptedPrivacyConditions = privacy),
                onValueChanged = { _, _ -> },
                onCheckedChanged = { input, field ->
                    if (field == InputKeys.PRIVACY) {
                        privacy = input
                    }
                },
                onValidate = {},
                uiLayout = uiLayout
            )
        }
        privacyInput.performClick()
        Assert.assertTrue(privacy)
    }

    @Test
    fun registrationScreen_privacyInputOrientationChange_keepsState() {
        var privacy by mutableStateOf(false)
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(acceptedPrivacyConditions = privacy),
                onValueChanged = { _, _ -> },
                onCheckedChanged = { input, field ->
                    if (field == InputKeys.PRIVACY) {
                        privacy = input
                    }
                },
                onValidate = {},
                uiLayout = uiLayout
            )
        }
        privacyInput.performClick()
        Assert.assertTrue(privacy)
        onDevice().setScreenOrientation(updatedOrientation)
        Assert.assertTrue(privacy)
    }

    @Test
    fun registrationScreen_firstNameError_DisplaysError() {
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(firstNameError = UiText.StringResource(R.string.first_name_is_blank_error)),
                onValueChanged = { _, _ -> },
                onCheckedChanged = { _, _ -> },
                onValidate = {},
                uiLayout = uiLayout
            )
        }
        rule.onNodeWithText(context.getString(R.string.first_name_is_blank_error))
            .assertIsDisplayed()
    }

    @Test
    fun registrationScreen_lastNameError_DisplaysError() {
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(lastNameError = UiText.StringResource(R.string.last_name_is_blank_error)),
                onValueChanged = { _, _ -> },
                onCheckedChanged = { _, _ -> },
                onValidate = {},
                uiLayout = uiLayout
            )
        }
        rule.onNodeWithText(context.getString(R.string.last_name_is_blank_error))
            .assertIsDisplayed()
    }

    @Test
    fun registrationScreen_streetError_DisplaysError() {
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(streetError = UiText.StringResource(R.string.street_is_blank_error)),
                onValueChanged = { _, _ -> },
                onCheckedChanged = { _, _ -> },
                onValidate = {},
                uiLayout = uiLayout
            )
        }
        rule.onNodeWithText(context.getString(R.string.street_is_blank_error)).assertIsDisplayed()
    }

    @Test
    fun registrationScreen_houseNumberError_DisplaysError() {
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(
                    houseNumberError = UiText.StringResource(
                        resId = R.string.invalid_house_number_error,
                        LOWEST_POSSIBLE_HOUSE_NUMBER
                    )
                ),
                onValueChanged = { _, _ -> },
                onCheckedChanged = { _, _ -> },
                onValidate = {},
                uiLayout = uiLayout
            )
        }
        rule.onNodeWithText(
            context.getString(
                R.string.invalid_house_number_error,
                LOWEST_POSSIBLE_HOUSE_NUMBER
            )
        ).assertIsDisplayed()
    }


    @Test
    fun registrationScreen_emailError_DisplaysError() {
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(emailError = UiText.StringResource(R.string.email_is_blank_error)),
                onValueChanged = { _, _ -> },
                onCheckedChanged = { _, _ -> },
                onValidate = {},
                uiLayout = uiLayout
            )
        }
        rule.onNodeWithText(context.getString(R.string.email_is_blank_error)).assertIsDisplayed()
    }

    @Test
    fun registrationScreen_phoneError_DisplaysError() {
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(phoneError = UiText.StringResource(R.string.invalid_phone_error)),
                onValueChanged = { _, _ -> },
                onCheckedChanged = { _, _ -> },
                onValidate = {},
                uiLayout = uiLayout
            )
        }
        rule.onNodeWithText(context.getString(R.string.invalid_phone_error)).assertIsDisplayed()
    }

    @Test
    fun registrationScreen_passwordError_DisplaysError() {
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(passwordError = UiText.StringResource(R.string.password_not_valid_error)),
                onValueChanged = { _, _ -> },
                onCheckedChanged = { _, _ -> },
                onValidate = {},
                uiLayout = uiLayout
            )
        }
        rule.onNodeWithText(context.getString(R.string.password_not_valid_error))
            .assertIsDisplayed()
    }

    @Test
    fun registrationScreen_confirmPasswordError_DisplaysError() {
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(repeatedPasswordError = UiText.StringResource(R.string.repeated_password_error)),
                onValueChanged = { _, _ -> },
                onCheckedChanged = { _, _ -> },
                onValidate = {},
                uiLayout = uiLayout
            )
        }
        rule.onNodeWithText(context.getString(R.string.repeated_password_error)).assertIsDisplayed()
    }

    @Test
    fun registrationScreen_termsError_DisplaysError() {
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(termsError = UiText.StringResource(R.string.terms_not_accepted_error)),
                onValueChanged = { _, _ -> },
                onCheckedChanged = { _, _ -> },
                onValidate = {},
                uiLayout = uiLayout
            )
        }
        rule.onNodeWithText(context.getString(R.string.terms_not_accepted_error))
            .assertIsDisplayed()
    }

    @Test
    fun registrationScreen_privacyError_DisplaysError() {
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(privacyError = UiText.StringResource(R.string.privacy_not_accepted_error)),
                onValueChanged = { _, _ -> },
                onCheckedChanged = { _, _ -> },
                onValidate = {},
                uiLayout = uiLayout
            )
        }

        rule.onNodeWithText(context.getString(R.string.privacy_not_accepted_error))
            .assertIsDisplayed()
    }

    @Test
    fun registrationScreen_dateOfBirthError_DisplaysError() {
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(
                    dateOfBirthError = UiText.StringResource(
                        resId = R.string.minimum_age_error,
                        MINIMUM_AGE
                    )
                ),
                onValueChanged = { _, _ -> },
                onCheckedChanged = { _, _ -> },
                onValidate = {},
                uiLayout = uiLayout
            )
        }
        rule.onNodeWithText(context.getString(R.string.minimum_age_error, MINIMUM_AGE))
            .assertIsDisplayed()
    }

    @Test
    fun registrationScreen_apiError_DisplaysError() {
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(apiError = "TestError"),
                onValueChanged = { _, _ -> },
                onCheckedChanged = { _, _ -> },
                onValidate = {},
                uiLayout = uiLayout
            )
        }
        errorMessage.assertIsDisplayed()
    }

    @Test
    fun registrationScreen_onRegistrationSuccess_showModal() {
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(registrationSuccess = true),
                onValueChanged = { _, _ -> },
                onCheckedChanged = { _, _ -> },
                onValidate = {},
                uiLayout = uiLayout,
                onSubmitClick = {},
                onRegistrationSuccessDismissed = { }
            )
        }
        rule.waitForIdle()
        registrationSuccessModal.assertIsDisplayed()
        registrationSuccessModalButton.assertIsDisplayed()
    }

    @Test
    fun registrationScreen_onRegisterClickAndSuccesfulRegistration_openRegistrationSuccessModal() {
        var registrationSuccess by mutableStateOf(false)
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(registrationSuccess = registrationSuccess),
                onValueChanged = { _, _ -> },
                onCheckedChanged = { _, _ -> },
                onValidate = {},
                uiLayout = uiLayout,
                onSubmitClick = { registrationSuccess = true },
                onRegistrationSuccessDismissed = { }
            )
        }
        registerButton.performClick()
        rule.waitForIdle()
        registrationSuccessModal.assertIsDisplayed()
        registrationSuccessModalButton.assertIsDisplayed()
        errorMessage.assertIsNotDisplayed()
    }

    @Test
    fun registrationScreen_onRegisterButtonClickAndError_DisplaysErrorAndNotModal() {
        var apiError by mutableStateOf("")
        rule.setContent {
            RegistrationScreen(
                state = RegistrationScreenState(apiError = apiError),
                onValueChanged = { _, _ -> },
                onCheckedChanged = { _, _ -> },
                onValidate = {},
                uiLayout = uiLayout,
                onSubmitClick = { apiError = "TestError" },
                onRegistrationSuccessDismissed = { }
            )
        }
        registerButton.performClick()
        rule.waitForIdle()
        errorMessage.assertIsDisplayed()
        registrationSuccessModal.isNotDisplayed()
        registrationSuccessModalButton.isNotDisplayed()
    }

    @Test
    fun registrationScreen_onRegistrationSuccessModalButtonClick_dismissModalAndRouteToHome() {
        var registrationSuccess by mutableStateOf(true)
        val state = RegistrationScreenState(registrationSuccess = registrationSuccess)
        rule.setContent {
            val navController = rememberNavController()
            navControllerState = navController
            NavHost(
                navController = navController,
                startDestination = NavigationKeys.Route.REGISTER
            ) {
                composable(NavigationKeys.Route.HOME) {
                    LoginScreen(
                        state = LoginScreenState(),
                        onValueUpdate = { _, _ -> },
                        login = { },
                        onRegisterClick = { },
                        onValidate = { _, _ -> },
                        uiLayout = uiLayout
                    )
                }
                composable(NavigationKeys.Route.REGISTER) {
                    RegistrationScreen(
                        state = state,
                        onValueChanged = { _, _ -> },
                        onCheckedChanged = { _, _ -> },
                        onValidate = { _ -> },
                        onSubmitClick = {},
                        uiLayout = uiLayout,
                        onRegistrationSuccessDismissed = {
                            registrationSuccess = false
                            navController.navigate(NavigationKeys.Route.HOME)
                        }
                    )
                }
            }
        }
        val navController = navControllerState!!
        rule.waitForIdle()
        registrationSuccessModal.assertIsDisplayed()
        registrationSuccessModalButton.assertIsDisplayed()
        registrationSuccessModalButton.performClick()
        rule.waitForIdle()
        assert(registrationSuccess == false)
        registrationSuccessModal.isNotDisplayed()
        registrationSuccessModalButton.isNotDisplayed()
        assert(navController.currentDestination?.route == NavigationKeys.Route.HOME)
    }
}