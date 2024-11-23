package rise.tiao1.buut.presentation

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.auth0.android.authentication.storage.CredentialsManager
import dagger.hilt.android.AndroidEntryPoint
import rise.tiao1.buut.presentation.booking.createBooking.CreateBookingScreen
import rise.tiao1.buut.presentation.booking.createBooking.CreateBookingViewModel
import rise.tiao1.buut.presentation.home.HomeScreen
import rise.tiao1.buut.presentation.home.HomeViewModel
import rise.tiao1.buut.presentation.login.LoginScreen
import rise.tiao1.buut.presentation.login.LoginViewModel
import rise.tiao1.buut.presentation.profile.ProfileScreen
import rise.tiao1.buut.presentation.profile.ProfileViewModel
import rise.tiao1.buut.presentation.register.RegistrationScreen
import rise.tiao1.buut.presentation.register.RegistrationViewModel
import rise.tiao1.buut.ui.theme.AppTheme
import rise.tiao1.buut.utils.NavigationKeys.Route
import rise.tiao1.buut.utils.UiLayout
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var credentialsManager: CredentialsManager

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val windowSize = calculateWindowSizeClass(this)
                BuutApp(windowSize = windowSize.widthSizeClass)
            }
        }
    }

    @Composable
    fun BuutApp(windowSize: WindowWidthSizeClass) {
        val navController = rememberNavController()
        val configuration = LocalConfiguration.current
        val uiLayout = setUiLayout(windowSize, configuration)

        NavHost(
            navController,
            startDestination = setStartingPage()
        ) {
            composable(route = Route.LOGIN) {
                val loginViewModel: LoginViewModel = hiltViewModel()
                LoginScreen(
                    state = loginViewModel.state.value,
                    onValueUpdate = { input, field: String ->
                        loginViewModel.update(input, field)
                    },
                    login = {
                        loginViewModel.login {
                            navController.navigate(Route.HOME) {
                                navController.popBackStack()
                            }

                        }
                    },
                    onRegisterClick = {
                        navController.navigate(Route.REGISTER)
                    },
                    onValidate = { input, field: String ->
                        loginViewModel.validate(input, field)
                    },
                    uiLayout = uiLayout
                )
            }
            composable(route = Route.HOME) {
                val viewModel: HomeViewModel = hiltViewModel()
                HomeScreen(
                    state = viewModel.state.value,
                    navigateTo =  { route:String -> navController.navigate(route)} ,
                    uiLayout = uiLayout,
                    onNotificationClick =  { notificationId:String, Boolean: Boolean -> viewModel.onNotificationClick(notificationId, Boolean) }
                )
            }
            composable(route = Route.REGISTER) {
                val registrationViewModel: RegistrationViewModel = hiltViewModel()
                RegistrationScreen(
                    state = registrationViewModel.state.value,
                    onValueChanged = { input: String, field: String ->
                        registrationViewModel.update(input, field)
                    },
                    onCheckedChanged = { input: Boolean, field: String ->
                        registrationViewModel.update(input, field)
                    },
                    onValidate = { field: String ->
                        registrationViewModel.validate(field)
                    },
                    onSubmitClick = { registrationViewModel.onRegisterClick() },
                    onRegistrationSuccessDismissed = {
                        registrationViewModel.onRegistrationSuccessDismissed(
                            navigateToHome = { navController.navigate(Route.LOGIN) })
                    },
                    uiLayout = uiLayout
                )
            }
            composable(route = Route.CREATE_BOOKING) {
                val createBookingViewModel: CreateBookingViewModel = hiltViewModel()
                CreateBookingScreen(
                    state = createBookingViewModel.state.value,
                    navigateUp = {navController.navigateUp()},
                    onDateSelected = { input: Long? ->
                        createBookingViewModel.updateSelectedDate(input)
                    },
                    onConfirmBooking = {
                        createBookingViewModel.onConfirmBooking()
                    },
                    onDismissBooking = {
                        createBookingViewModel.onDismissBooking()
                    },
                    onTimeSlotClicked = { input ->
                        createBookingViewModel.onTimeSlotClicked(input)
                    },
                    toBookingsOverview = {
                        navController.navigate(Route.HOME)
                    },
                    uiLayout = uiLayout
                )
            }
            composable(route = Route.PROFILE){
                val profileViewModel: ProfileViewModel = hiltViewModel()
                ProfileScreen(
                    state = profileViewModel.state.value,
                    logout = {
                        profileViewModel.onLogoutClicked {
                            navController.navigate(Route.LOGIN) {
                                navController.popBackStack()
                            }
                        }
                    },
                    navigateTo = { route:String -> navController.navigate(route)},
                    uiLayout = uiLayout
                )
            }
        }
    }

    private fun setStartingPage(): String {
        if (credentialsManager.hasValidCredentials())
            return Route.HOME
        else
            return Route.LOGIN
    }

    private fun setUiLayout(
        windowSize: WindowWidthSizeClass,
        configuration: Configuration
    ): UiLayout {

        return when (windowSize) {
            WindowWidthSizeClass.Compact -> {
                if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                     UiLayout.PORTRAIT_SMALL
                else
                     UiLayout.LANDSCAPE_SMALL
            }

            WindowWidthSizeClass.Medium -> {
                if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                     UiLayout.PORTRAIT_MEDIUM
                else
                     UiLayout.LANDSCAPE_MEDIUM
            }

            WindowWidthSizeClass.Expanded -> {
                if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                     UiLayout.PORTRAIT_EXPANDED
                else
                     UiLayout.LANDSCAPE_EXPANDED
            }
            else -> {
                 UiLayout.PORTRAIT_SMALL
            }

        }
    }

}