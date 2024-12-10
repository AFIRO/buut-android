package rise.tiao1.buut.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
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
import org.junit.Rule
import org.junit.Test
import rise.tiao1.buut.R
import rise.tiao1.buut.presentation.register.RegistrationScreen
import rise.tiao1.buut.presentation.register.RegistrationScreenState
import rise.tiao1.buut.utils.InputKeys
import rise.tiao1.buut.utils.NavigationKeys
import rise.tiao1.buut.utils.UiLayout
import rise.tiao1.buut.utils.UiText

class LoginScreenKtCompactPortraitTest {
    val startOrientation = ScreenOrientation.PORTRAIT
    val updatedOrientation = ScreenOrientation.LANDSCAPE
    val uiLayout = UiLayout.PORTRAIT_SMALL

    @get:Rule
    val rule: ComposeContentTestRule =
        createComposeRule()

    @get:Rule
    val screenOrientationRule: ScreenOrientationRule = ScreenOrientationRule(startOrientation)
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    var navControllerState by mutableStateOf<NavController?>(null)
    val logo = rule.onNodeWithTag("BuutLogo")
    val emailInput = rule.onNodeWithText(context.getString(R.string.email_label))
    val passwordInput = rule.onNodeWithText(context.getString(R.string.password))
    val noAccountLabel = rule.onNodeWithText(context.getString(R.string.no_account_yet))
    val loginButton = rule.onNodeWithText(context.getString(R.string.log_in_button))
    val registerButton = rule.onNodeWithText(context.getString(R.string.register_here))

    @Test
    fun loginScreen_displayCorrectly() {
        rule.setContent {
            LoginScreen(
                state = LoginScreenState(),
                onValueUpdate = { _, _ -> },
                login = { },
                onRegisterClick = { },
                onValidate = { _, _ -> },
                uiLayout = uiLayout
            )
        }

        logo.assertIsDisplayed()
        emailInput.assertIsDisplayed()
        passwordInput.assertIsDisplayed()
        noAccountLabel.assertIsDisplayed()
        loginButton.assertIsDisplayed()
        registerButton.assertIsDisplayed()
    }

    @Test
    fun loginScreen_emailInput_updatesState() {
        var email by mutableStateOf("")
        rule.setContent {
            LoginScreen(
                state = LoginScreenState(email = email),
                onValueUpdate = { input, field ->
                    if (field == InputKeys.EMAIL) {
                        email = input
                    }
                },
                login = { },
                onRegisterClick = { },
                onValidate = { _, _ -> },
                uiLayout = uiLayout
            )
        }


        emailInput.performTextInput("buut@buut.buut")
        Assert.assertEquals("buut@buut.buut", email)
    }

    @Test
    fun loginScreen_emailInputAndOrientationChange_keepsState() {
        var email by mutableStateOf("")
        rule.setContent {
            LoginScreen(
                state = LoginScreenState(email = email),
                onValueUpdate = { input, field ->
                    if (field == InputKeys.EMAIL) {
                        email = input
                    }
                },
                login = { },
                onRegisterClick = { },
                onValidate = { _, _ -> },
                uiLayout = uiLayout
            )
        }


        emailInput.performTextInput("buut@buut.buut")
        Assert.assertEquals("buut@buut.buut", email)
        onDevice().setScreenOrientation(updatedOrientation)
        Assert.assertEquals("buut@buut.buut", email)
    }

    @Test
    fun loginScreen_passwordInput_updatesState() {
        var password by mutableStateOf("")
        rule.setContent {
            LoginScreen(
                state = LoginScreenState(password = password),
                onValueUpdate = { input, field ->
                    if (field == InputKeys.PASSWORD) {
                        password = input
                    }
                },
                login = { },
                onRegisterClick = { },
                onValidate = { _, _ -> },
                uiLayout = uiLayout
            )
        }
        passwordInput.performTextInput("Password1!")
        Assert.assertEquals("Password1!", password)
    }

    @Test
    fun loginScreen_passwordInputAndOrientationChange_keepsState() {
        var password by mutableStateOf("")
        rule.setContent {
            LoginScreen(
                state = LoginScreenState(password = password),
                onValueUpdate = { input, field ->
                    if (field == InputKeys.PASSWORD) {
                        password = input
                    }
                },
                login = { },
                onRegisterClick = { },
                onValidate = { _, _ -> },
                uiLayout = uiLayout
            )
        }
        passwordInput.performTextInput("Password1!")
        Assert.assertEquals("Password1!", password)
        onDevice().setScreenOrientation(updatedOrientation)
        Assert.assertEquals("Password1!", password)
    }


    @Test
    fun loginScreen_loginButtonPressed_triggersLogin() {
        var loginButtonClicked by mutableStateOf(false)
        rule.setContent {
            LoginScreen(
                state = LoginScreenState(),
                onValueUpdate = { _, _ -> },
                login = { loginButtonClicked = true },
                onRegisterClick = { },
                onValidate = { _, _ -> },
                uiLayout = uiLayout
            )
        }

        emailInput.performTextInput("test@example.com")
        passwordInput.performTextInput("password123")
        loginButton.performClick()

        Assert.assertTrue(loginButtonClicked)
    }

    @Test
    fun loginScreen_errorInEmailState_showsError() {
        rule.setContent {
            LoginScreen(
                state = LoginScreenState(emailError = UiText.StringResource(R.string.email_is_blank_error)),
                onValueUpdate = { _, _ -> },
                login = { },
                onRegisterClick = { },
                onValidate = { _, _ -> },
                uiLayout = uiLayout
            )
        }

        rule.onNodeWithText(context.getString(R.string.email_is_blank_error))
    }

    @Test
    fun loginScreen_errorInPasswordState_showsError() {
        rule.setContent {
            LoginScreen(
                state = LoginScreenState(passwordError = UiText.StringResource(R.string.password_not_valid_error)),
                onValueUpdate = { _, _ -> },
                login = { },
                onRegisterClick = { },
                onValidate = { _, _ -> },
                uiLayout = uiLayout
            )
        }

        rule.onNodeWithText(context.getString(R.string.password_not_valid_error))
    }

    @Test
    fun loginScreen_errorInApiState_showsError() {
        rule.setContent {
            LoginScreen(
                state = LoginScreenState(apiError = "Api error"),
                onValueUpdate = { _, _ -> },
                login = { },
                onRegisterClick = { },
                onValidate = { _, _ -> },
                uiLayout = uiLayout
            )
        }

        rule.onNodeWithText("Api error")
    }

    @Test
    fun loginScreen_registerButtonPressed_routesToRegistration() {
        rule.setContent {
            val navController = rememberNavController()
            navControllerState = navController
            NavHost(navController = navController, startDestination = NavigationKeys.Route.HOME) {
                composable(NavigationKeys.Route.HOME) {
                    LoginScreen(
                        state = LoginScreenState(),
                        onValueUpdate = { _, _ -> },
                        login = { },
                        onRegisterClick = { navController.navigate(NavigationKeys.Route.REGISTER) },
                        onValidate = { _, _ -> },
                        uiLayout = uiLayout
                    )
                }
                composable(NavigationKeys.Route.REGISTER) {
                    RegistrationScreen(
                        state = RegistrationScreenState(),
                        onValueChanged = { _, _ -> },
                        onCheckedChanged = { _, _ -> },
                        onValidate = { _ -> },
                        onSubmitClick = {},
                        uiLayout = uiLayout,
                    )
                }
            }
        }
        val navController = navControllerState!!
        registerButton.performClick()
        assert(navController.currentDestination?.route == NavigationKeys.Route.REGISTER)
    }

    @Test
    fun loginScreen_noConnection_displayCorrectly() {
        rule.setContent {
            LoginScreen(
                state = LoginScreenState(isNetworkAvailable = false),
                onValueUpdate = { _, _ -> },
                login = { },
                onRegisterClick = { },
                onValidate = { _, _ -> },
                uiLayout = uiLayout
            )
        }

        logo.assertIsDisplayed()
        emailInput.assertIsDisplayed()
        passwordInput.assertIsDisplayed()
        noAccountLabel.assertIsDisplayed()
        loginButton.assertIsDisplayed()
        registerButton.assertIsDisplayed()
        rule.onNodeWithText(context.getString(R.string.no_internet_connection_login))
            .assertIsDisplayed()
    }
}