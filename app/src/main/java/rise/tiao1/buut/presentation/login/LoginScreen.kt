package rise.tiao1.buut.presentation.login

import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import rise.tiao1.buut.R
import rise.tiao1.buut.presentation.components.ActionErrorContainer
import rise.tiao1.buut.presentation.components.ButtonComponent
import rise.tiao1.buut.presentation.components.BuutLogo
import rise.tiao1.buut.presentation.components.ClickableTextComponent
import rise.tiao1.buut.presentation.components.ErrorMessageContainer
import rise.tiao1.buut.presentation.components.MainBackgroundImage
import rise.tiao1.buut.presentation.components.OutlinedTextFieldComponent
import rise.tiao1.buut.presentation.components.PasswordTextFieldComponent
import rise.tiao1.buut.ui.theme.AppTheme
import rise.tiao1.buut.utils.InputKeys
import rise.tiao1.buut.utils.UiLayout


@Composable
fun LoginScreen(
    state: LoginScreenState,
    onValueUpdate: (input: String, field: String) -> Unit,
    login: () -> Unit,
    onRegisterClick: () -> Unit,
    onValidate: (input: String, field: String) -> Unit,
    uiLayout: UiLayout
) {

    val scrollState = rememberScrollState()
    val connectivityManager =
        LocalContext.current.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val isNetworkAvailable = connectivityManager.activeNetwork != null

    Box()
    {
        MainBackgroundImage()

        if (uiLayout == UiLayout.LANDSCAPE_SMALL || uiLayout == UiLayout.LANDSCAPE_MEDIUM) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BuutLogo()
                Spacer(modifier = Modifier.widthIn(40.dp))
                Column {
                    LoginScreenContent(
                        state, onValueUpdate, login, onRegisterClick, onValidate
                    )
                    if (!isNetworkAvailable) {
                        ActionErrorContainer(LocalContext.current.getString(R.string.no_internet_connection_login))
                    }
                }

            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                BuutLogo()
                Spacer(modifier = Modifier.heightIn(70.dp))
                LoginScreenContent(
                    state, onValueUpdate, login, onRegisterClick, onValidate, isNetworkAvailable
                )
                if (!isNetworkAvailable) {
                    ActionErrorContainer(LocalContext.current.getString(R.string.no_internet_connection_login))
                }
            }
        }
    }

}

@Composable
fun LoginScreenContent(
    state: LoginScreenState,
    onValueUpdate: (input: String, field: String) -> Unit,
    login: () -> Unit,
    onRegisterClick: () -> Unit,
    onValidate: (input: String, field: String) -> Unit,
    registerEnabled: Boolean = true

) {
    Column(
        modifier = Modifier.width(300.dp)
    ) {
        Column()
        {
            OutlinedTextFieldComponent(
                value = state.email ?: "",
                onValueChanged = { onValueUpdate(it, InputKeys.EMAIL) },
                errorMessage = state.emailError?.asString(),
                label = R.string.email_label,
                onFocusLost = { onValidate(state.email, InputKeys.EMAIL) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )
            PasswordTextFieldComponent(
                value = state.password ?: "",
                onValueChanged = { onValueUpdate(it, InputKeys.PASSWORD) },
                errorMessage = state.passwordError?.asString(),
                label = R.string.password,
                onFocusLost = { onValidate(state.password, InputKeys.PASSWORD) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )
            ErrorMessageContainer(
                errorMessage = state.apiError,
            )
        }
        Column(
        ) {
            ButtonComponent(
                label = R.string.log_in_button,
                onClick = { login() },
                isLoading = state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.heightIn(6.dp))
            ClickableTextComponent(
                leadingText = R.string.no_account_yet,
                clickableText = R.string.register_here,
                onClick = onRegisterClick,
                isEnabled = registerEnabled
            )
        }
    }
}


@Preview(
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    uiMode = Configuration.ORIENTATION_PORTRAIT
)
@Composable
fun LoginPortraitSmallPreview() {
    AppTheme {
        LoginScreen(
            LoginScreenState(),
            { _, _ -> },
            {},
            {},
            { _, _ -> },
            uiLayout = UiLayout.PORTRAIT_SMALL
        )
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    uiMode = Configuration.ORIENTATION_LANDSCAPE
)
@Composable
fun LoginLandscapeSmallPreview() {
    AppTheme {
        LoginScreen(
            LoginScreenState(),
            { _, _ -> },
            {},
            {},
            { _, _ -> },
            uiLayout = UiLayout.LANDSCAPE_SMALL
        )
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240",
    uiMode = Configuration.ORIENTATION_PORTRAIT
)
@Composable
fun LoginExpandedPortraitPreview() {
    AppTheme {
        LoginScreen(
            LoginScreenState(),
            { _, _ -> },
            {},
            {},
            { _, _ -> },
            uiLayout = UiLayout.PORTRAIT_EXPANDED
        )
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240",
    uiMode = Configuration.ORIENTATION_LANDSCAPE
)
@Composable
fun LoginExpandedLandscapePreview() {
    AppTheme {
        LoginScreen(
            LoginScreenState(),
            { _, _ -> },
            {},
            {},
            { _, _ -> },
            uiLayout = UiLayout.LANDSCAPE_EXPANDED
        )
    }
}