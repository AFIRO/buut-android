package rise.tiao1.buut.presentation.editProfile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.device.action.ScreenOrientation
import androidx.test.espresso.device.rules.ScreenOrientationRule
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.delay
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import rise.tiao1.buut.R
import rise.tiao1.buut.domain.user.Address
import rise.tiao1.buut.domain.user.Role
import rise.tiao1.buut.domain.user.User
import rise.tiao1.buut.presentation.profile.ProfileScreen
import rise.tiao1.buut.presentation.profile.ProfileScreenState
import rise.tiao1.buut.utils.NavigationKeys
import rise.tiao1.buut.utils.StreetType
import rise.tiao1.buut.utils.UiLayout
import java.time.LocalDateTime

class EditProfileScreenKtCompactPortraitTest {
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
    val testError = "testError"
    val topBar = rule.onNodeWithTag("topBar")
    val loadingIndicator = rule.onNodeWithTag(context.getString(R.string.loading_indicator))
    val errorContainer = rule.onNodeWithText(testError)
    val profileEditConfirmButton = rule.onNodeWithTag("profileEditConfirmButton")
    val profileEditCancelButton = rule.onNodeWithTag("profileEditCancelButton")
    val firstNameTextField = rule.onNodeWithTag("firstNameTextField")
    val lastNameTextField = rule.onNodeWithTag("lastNameTextField")
    val phoneTextField = rule.onNodeWithTag("phoneTextField")
    val streetTextField = rule.onNodeWithTag("autocompleter")
    val houseNumberTextField = rule.onNodeWithTag("houseNumberTextField")
    val addressBoxTextField = rule.onNodeWithTag("addressBoxTextField")
    val headerText = rule.onNodeWithTag("headerOne")

    @Test
    fun editProfileScreen_loadingState_showsLoadingIndicator() {
        rule.setContent {
            EditProfileScreen(
                state = EditProfileScreenState(user = null, isLoading = true, apiError = ""),
                onValueChanged = { _: String, _: String -> },
                navigateTo = {},
                uiLayout = uiLayout,
                onConfirmClick = {},
                onCancelClick = {},
                onValidate = {}
            )
        }
        rule.waitForIdle()

        // indicator and error
        loadingIndicator.assertIsDisplayed()
        errorContainer.assertIsNotDisplayed()

        // topbar and headder
        headerText.assertIsNotDisplayed()
        topBar.assertIsDisplayed()

        //buttons
        profileEditConfirmButton.assertIsNotDisplayed()
        profileEditCancelButton.assertIsNotDisplayed()

        // textfields
        firstNameTextField.assertIsNotDisplayed()
        lastNameTextField.assertIsNotDisplayed()
        phoneTextField.assertIsNotDisplayed()
        streetTextField.assertIsNotDisplayed()
        houseNumberTextField.assertIsNotDisplayed()
        addressBoxTextField.assertIsNotDisplayed()
    }

    @Test
    fun profileScreen_errorState_showsErrorContainer() {
        rule.setContent {
            EditProfileScreen(
                state = EditProfileScreenState(user = null, isLoading = false, apiError = testError),
                onValueChanged = { _: String, _: String -> },
                navigateTo = {},
                uiLayout = uiLayout,
                onConfirmClick = {},
                onCancelClick = {},
                onValidate = {}
            )
        }

        rule.waitForIdle()

        // loading and error
        loadingIndicator.assertIsNotDisplayed()
        errorContainer.assertIsDisplayed()

        // topbar and headder
        headerText.assertIsNotDisplayed()
        topBar.assertIsDisplayed()

        // buttons
        profileEditConfirmButton.assertIsNotDisplayed()
        profileEditCancelButton.assertIsNotDisplayed()

        //textfields
        firstNameTextField.assertIsNotDisplayed()
        lastNameTextField.assertIsNotDisplayed()
        phoneTextField.assertIsNotDisplayed()
        streetTextField.assertIsNotDisplayed()
        houseNumberTextField.assertIsNotDisplayed()
        addressBoxTextField.assertIsNotDisplayed()
    }

    @Test
    fun profileScreen_successState_showsEditableProfileContent() {
        rule.setContent {
            EditProfileScreen(
                state = EditProfileScreenState(user = getUser(), isLoading = false, apiError = ""),
                onValueChanged = { _: String, _: String -> },
                navigateTo = {},
                uiLayout = uiLayout,
                onConfirmClick = {},
                onCancelClick = {},
                onValidate = {}
            )
        }

        rule.waitForIdle()

        //error and loading
        loadingIndicator.assertIsNotDisplayed()
        errorContainer.assertIsNotDisplayed()

        //topbar
        topBar.assertIsDisplayed()
        headerText.assertIsDisplayed()

        //buttons
        profileEditConfirmButton.assertIsDisplayed()
        profileEditCancelButton.assertIsDisplayed()

        //textfields
        firstNameTextField.assertIsDisplayed()
        lastNameTextField.assertIsDisplayed()
        phoneTextField.assertIsDisplayed()
        houseNumberTextField.assertIsDisplayed()
        addressBoxTextField.assertIsDisplayed()
    }

    @Test
    fun profileScreen_profileEditConfirmButton_navigatesToProfile() {
        rule.setContent {
            val navController = rememberNavController()
            navControllerState = navController
            NavHost(
                navController = navController,
                startDestination = NavigationKeys.Route.EDIT_PROFILE
            ) {
                composable(route = NavigationKeys.Route.EDIT_PROFILE) {
                    EditProfileScreen(
                        state = EditProfileScreenState(user = getUser(), isLoading = false, apiError = ""),
                        navigateTo = { navController.navigate(NavigationKeys.Route.PROFILE) },
                        onValueChanged = { _: String, _: String -> },
                        uiLayout = uiLayout,
                        onConfirmClick = {navController.navigate(NavigationKeys.Route.PROFILE)},
                        onCancelClick = {},
                        onValidate = {}
                    )

                }
                composable(route = NavigationKeys.Route.PROFILE) {
                }
            }
        }

        rule.waitForIdle()
        profileEditConfirmButton.performClick()

        rule.waitForIdle()
        assertEquals(navControllerState?.currentDestination?.route, NavigationKeys.Route.PROFILE)
    }

    @Test
    fun profileScreen_profileEditCancelButton_navigatesToProfile() {
        rule.setContent {
            val navController = rememberNavController()
            navControllerState = navController
            NavHost(
                navController = navController,
                startDestination = NavigationKeys.Route.EDIT_PROFILE
            ) {
                composable(route = NavigationKeys.Route.EDIT_PROFILE) {
                    EditProfileScreen(
                        state = EditProfileScreenState(user = getUser(), isLoading = false, apiError = ""),
                        navigateTo = { navController.navigate(NavigationKeys.Route.PROFILE) },
                        onValueChanged = { _: String, _: String -> },
                        uiLayout = uiLayout,
                        onConfirmClick = {},
                        onCancelClick = {navController.navigate(NavigationKeys.Route.PROFILE)},
                        onValidate = {},

                    )

                }
                composable(route = NavigationKeys.Route.PROFILE) {
                }
            }
        }

        rule.waitForIdle()
        profileEditCancelButton.performClick()


        rule.waitForIdle()
        assertEquals(NavigationKeys.Route.PROFILE, navControllerState?.currentDestination?.route)

    }

    fun getUser(): User {
        return User(
            id = "TestId",
            firstName = "TestFirstName",
            lastName = "TestLastName",
            email = "Test@Test.be",
            password = "TestPassword",
            phone = "TestPhoneNumber",
            dateOfBirth = LocalDateTime.of(1996, 8, 19, 0, 0),
            address = Address(StreetType.AFRIKALAAN, "TestHouseNumber", "TestBox"),
            roles = listOf(Role(name = "Admin"))
        )
    }
}