package rise.tiao1.buut.presentation.profile

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
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.espresso.device.action.ScreenOrientation
import androidx.test.espresso.device.rules.ScreenOrientationRule
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import rise.tiao1.buut.R
import rise.tiao1.buut.domain.user.Address
import rise.tiao1.buut.domain.user.Role
import rise.tiao1.buut.domain.user.User
import rise.tiao1.buut.utils.NavigationKeys
import rise.tiao1.buut.utils.StreetType
import rise.tiao1.buut.utils.UiLayout
import java.time.LocalDateTime


class ProfileScreenKtMediumPortraitTest {
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
    val testError = "testError"
    val navigation = rule.onNodeWithTag("navigation")
    val loadingIndicator = rule.onNodeWithTag(context.getString(R.string.loading_indicator))
    val errorContainer = rule.onNodeWithText(testError)
    val profileEditButton = rule.onNodeWithTag("profileEditButton")
    val profileLogoutButton = rule.onNodeWithTag("profileLogoutButton")
    val nameInfo = rule.onNodeWithTag("name")
    val mailAndPhoneInfo = rule.onNodeWithTag("mail+phone")
    val dobInfo = rule.onNodeWithTag("dob")
    val addressInfo = rule.onNodeWithTag("address")

    @Test
    fun profileScreen_loadingState_showsLoadingIndicator() {
        rule.setContent {
            ProfileScreen(
                state = ProfileScreenState(user = null, isLoading = true, apiError = ""),
                logout = {},
                navigateTo = {},
                uiLayout = uiLayout
            )
        }
        loadingIndicator.assertIsDisplayed()
        errorContainer.assertIsNotDisplayed()
        navigation.assertIsDisplayed()
        profileEditButton.assertIsNotDisplayed()
        profileLogoutButton.assertIsNotDisplayed()
        nameInfo.assertIsNotDisplayed()
        mailAndPhoneInfo.assertIsNotDisplayed()
        dobInfo.assertIsNotDisplayed()
        addressInfo.assertIsNotDisplayed()
    }


    @Test
    fun profileScreen_errorState_showsErrorContainer() {
        rule.setContent {
            ProfileScreen(
                state = ProfileScreenState(user = null, isLoading = false, apiError = testError),
                logout = {},
                navigateTo = {},
                uiLayout = uiLayout
            )
        }
        loadingIndicator.assertIsNotDisplayed()
        errorContainer.assertIsDisplayed()
        navigation.assertIsDisplayed()
        profileEditButton.assertIsNotDisplayed()
        profileLogoutButton.assertIsNotDisplayed()
        nameInfo.assertIsNotDisplayed()
        mailAndPhoneInfo.assertIsNotDisplayed()
        dobInfo.assertIsNotDisplayed()
        addressInfo.assertIsNotDisplayed()
    }

    @Test
    fun profileScreen_successState_showsProfileContent() {
        rule.setContent {
            ProfileScreen(
                state = ProfileScreenState(user = getUser(), isLoading = false, apiError = ""),
                logout = {},
                navigateTo = {},
                uiLayout = uiLayout
            )
        }
        rule.waitForIdle()
        loadingIndicator.assertIsNotDisplayed()
        errorContainer.assertIsNotDisplayed()
        navigation.assertIsDisplayed()
        profileEditButton.assertIsDisplayed()
        profileLogoutButton.assertIsDisplayed()
        nameInfo.assertIsDisplayed()
        mailAndPhoneInfo.assertIsDisplayed()
        dobInfo.assertIsDisplayed()
        addressInfo.assertIsDisplayed()
    }

    @Test
    fun profileScreen_profileEditButton_navigatesToProfileEditScreen() {
        rule.setContent {
            val navController = rememberNavController()
            navControllerState = navController
            NavHost(
                navController = navController,
                startDestination = NavigationKeys.Route.PROFILE
            ) {
                composable(route = NavigationKeys.Route.PROFILE) {
                    ProfileScreen(
                        state = ProfileScreenState(user = getUser(), isLoading = false, apiError = ""),
                        logout = {},
                        navigateTo = { navController.navigate(NavigationKeys.Route.EDIT_PROFILE) },
                        uiLayout = uiLayout)
                }
                composable(route = NavigationKeys.Route.EDIT_PROFILE) {
                }
            }
        }
        profileEditButton.performClick()
        assertEquals(navControllerState?.currentDestination?.route, NavigationKeys.Route.EDIT_PROFILE)
    }

    @Test
    fun profileScreen_profileLogoutButton_navigatesToLoginScreen() {
        rule.setContent {
            val navController = rememberNavController()
            navControllerState = navController
            NavHost(
                navController = navController,
                startDestination = NavigationKeys.Route.PROFILE
            ) {
                composable(route = NavigationKeys.Route.PROFILE) {
                    ProfileScreen(
                        state = ProfileScreenState(user = getUser(), isLoading = false, apiError = ""),
                        logout = {navController.navigate(NavigationKeys.Route.LOGIN)},
                        navigateTo = { },
                        uiLayout = uiLayout)
                }
                composable(route = NavigationKeys.Route.LOGIN) {
                }
            }
        }
        profileLogoutButton.performClick()
        assertEquals(navControllerState?.currentDestination?.route, NavigationKeys.Route.LOGIN)
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