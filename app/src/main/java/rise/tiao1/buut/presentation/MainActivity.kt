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
import rise.tiao1.buut.presentation.booking.BookingScreen
import rise.tiao1.buut.presentation.booking.BookingViewModel
import rise.tiao1.buut.presentation.home.HomeScreen
import rise.tiao1.buut.presentation.home.HomeViewModel
import rise.tiao1.buut.presentation.login.LoginScreen
import rise.tiao1.buut.presentation.login.LoginViewModel
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
                startDestination =  setStartingPage()
            ) {
                composable(route = Route.LOGIN) {
                    val loginViewModel: LoginViewModel = hiltViewModel()
                    LoginScreen(
                        state = loginViewModel.state.value,
                        onValueUpdate = {input, field: String ->
                            loginViewModel.update(input, field)
                        },
                        login = {
                            loginViewModel.login {
                                navController.navigate(Route.HOME)
                            }
                        },
                        onRegisterClick = {
                            navController.navigate(Route.REGISTER)
                        },
                        onValidate = {input, field: String ->
                            loginViewModel.validate(input, field)
                        }
                    )
                }
                composable(route = Route.HOME) {
                    val viewModel: HomeViewModel = hiltViewModel()
                    HomeScreen(
                        state = viewModel.state.value,
                        logout = {
                            viewModel.logout {
                                navController.navigate(Route.LOGIN)
                            }
                        },
                        toReservationPage = {navController.navigate(Route.RESERVATION)},
                        uiLayout = uiLayout
                    )
                }
                composable(route = Route.REGISTER) {
                    val registrationViewModel: RegistrationViewModel = hiltViewModel()
                    RegistrationScreen(
                        state = registrationViewModel.state.value,
                        onValueChanged = { input: String, field :String->
                            registrationViewModel.update(input, field)
                        },
                        onCheckedChanged = { input: Boolean, field: String ->
                            registrationViewModel.update(input, field)
                        },
                        onValidate = { field : String ->
                            registrationViewModel.validate(field)
                        },
                        onSubmitClick = { registrationViewModel.onRegisterClick() }
                    )
                }
                composable(route = Route.RESERVATION) {
                    val bookingViewModel: BookingViewModel = hiltViewModel()
                    BookingScreen (
                        state = bookingViewModel.state.value,
                        onValueChanged = { input: Long? ->
                            bookingViewModel.update(input)
                        }
                    )
                }
            }
        }

   private fun setStartingPage(): String{
        if (!credentialsManager.hasValidCredentials())
            return Route.LOGIN
        else
            return Route.HOME
    }

    private fun setUiLayout(windowSize: WindowWidthSizeClass, configuration : Configuration): UiLayout{


        when (windowSize) {
            WindowWidthSizeClass.Compact -> {
                if(configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                    return UiLayout.PORTRAIT_SMALL
                else
                    return UiLayout.LANDSCAPE
            }
            WindowWidthSizeClass.Medium -> {
                if(configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                    return UiLayout.PORTRAIT_LARGE
                else
                    return UiLayout.LANDSCAPE
            }
            WindowWidthSizeClass.Expanded -> {
                if(configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                    return UiLayout.PORTRAIT_LARGE
                else
                    return UiLayout.LANDSCAPE
            }
            else -> {
                return UiLayout.LANDSCAPE
            }
        }
    }

}






