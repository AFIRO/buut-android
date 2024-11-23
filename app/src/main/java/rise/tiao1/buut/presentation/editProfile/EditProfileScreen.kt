package rise.tiao1.buut.presentation.editProfile

import android.renderscript.ScriptGroup
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import rise.tiao1.buut.R
import rise.tiao1.buut.data.remote.user.dto.RoleDTO
import rise.tiao1.buut.domain.user.Address
import rise.tiao1.buut.domain.user.Role
import rise.tiao1.buut.domain.user.User
import rise.tiao1.buut.presentation.components.AutoCompleteTextFieldComponent
import rise.tiao1.buut.presentation.components.ButtonComponent
import rise.tiao1.buut.presentation.components.ErrorMessageContainer
import rise.tiao1.buut.presentation.components.LoadingIndicator
import rise.tiao1.buut.presentation.components.Navigation
import rise.tiao1.buut.presentation.components.OutlinedTextFieldComponent
import rise.tiao1.buut.presentation.profile.getUser
import rise.tiao1.buut.ui.theme.AppTheme
import rise.tiao1.buut.utils.InputKeys
import rise.tiao1.buut.utils.NavigationKeys
import rise.tiao1.buut.utils.StreetType
import rise.tiao1.buut.utils.UiLayout
import rise.tiao1.buut.utils.UiLayout.PORTRAIT_EXPANDED
import rise.tiao1.buut.utils.UiLayout.PORTRAIT_MEDIUM
import rise.tiao1.buut.utils.UiLayout.PORTRAIT_SMALL
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.collections.listOf

val TAG = "EditProfileScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    state: EditProfileScreenState,
    navigateTo: (String) -> Unit,
    onValueChanged: (input: String, field: String) -> Unit,
    onConfirmClick: () -> Unit = {},
    onValidate: (field: String) -> Unit,
    uiLayout: UiLayout
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier
                                .size(dimensionResource(R.dimen.image_size_standard))
                                .padding(top = dimensionResource(R.dimen.padding_small)),
                            painter = painterResource(R.drawable.buut_logo),
                            contentDescription = stringResource(R.string.buut_logo),
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.inversePrimary
                ),
                modifier = Modifier.testTag("navigation")
            )
        },
        bottomBar = {
            if (uiLayout == PORTRAIT_SMALL || uiLayout == PORTRAIT_MEDIUM) {
                Navigation(
                    navigateTo = navigateTo,
                    uiLayout = uiLayout,
                    currentPage = NavigationKeys.Route.HOME
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (uiLayout == PORTRAIT_SMALL || uiLayout == PORTRAIT_MEDIUM) {
                Column {
                    EditProfileScreenContent(
                        state = state,
                        navigateTo = navigateTo,
                        onConfirmClick = onConfirmClick,
                        onValueChanged = onValueChanged,
                        onValidate = onValidate,
                        uiLayout = uiLayout
                    )
                }
            } else {
                Row {
                    Navigation(
                        uiLayout = uiLayout,
                        navigateTo = navigateTo,
                        currentPage = NavigationKeys.Route.PROFILE,
                        content = { EditProfileScreenContent(
                            state = state,
                            navigateTo = navigateTo,
                            onConfirmClick = onConfirmClick,
                            onValueChanged = onValueChanged,
                            onValidate = onValidate,
                            uiLayout = uiLayout
                        ) }
                    )
                    EditProfileScreenContent(
                        state = state,
                        navigateTo = navigateTo,
                        onConfirmClick = onConfirmClick,
                        onValueChanged = onValueChanged,
                        onValidate = onValidate,
                        uiLayout = uiLayout
                    )
                }
            }
        }
    }
}

@Composable
fun EditProfileScreenContent(
    state: EditProfileScreenState,
    navigateTo: (String) -> Unit,
    onConfirmClick: () -> Unit = {},
    onValueChanged: (input: String, field: String) -> Unit,
    onValidate: (field: String) -> Unit,
    uiLayout: UiLayout,
    modifier: Modifier = Modifier
) {
    Log.d(TAG, "Line 160: " + state.isLoading)
    if (state.isLoading) {
        LoadingIndicator()
    } else if (state.apiError?.isNotEmpty() == true) {
        ErrorMessageContainer(state.apiError)
    } else {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            EditProfileProfileContent(state, onValueChanged, onValidate, uiLayout)
            EditProfileButtonContent(
                state = state,
                onConfirmClick= onConfirmClick,
                navigateTo = navigateTo,
                uiLayout = uiLayout)
        }
    }
}

@Composable
fun CancelButton(
    state: EditProfileScreenState,
    modifier: Modifier = Modifier
) {
    ButtonComponent(
        label = R.string.cancel,
        onClick = { Log.d(TAG, "cancel clicked") },
        isLoading = state.isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError
        ),
        modifier = modifier
    )
}

@Composable
fun ConfirmButton(
    state: EditProfileScreenState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ButtonComponent(
        label = R.string.confirm,
        onClick = onClick,
        isLoading = state.isLoading,
        modifier = modifier

    )
}




@Composable
fun EditProfileButtonContent(
    state: EditProfileScreenState,
    onConfirmClick: () -> Unit = {},
    navigateTo: (String) -> Unit,
    uiLayout: UiLayout
) {
    if (uiLayout == PORTRAIT_SMALL || uiLayout == PORTRAIT_MEDIUM || uiLayout == PORTRAIT_EXPANDED) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Spacer(modifier = Modifier.heightIn(dimensionResource(R.dimen.padding_medium)))
            ConfirmButton(
                state = state,
                onClick = onConfirmClick,
                modifier = Modifier
                    .widthIn(dimensionResource(R.dimen.button_width))
                    .testTag("profileEditButton")
            )

            Spacer(modifier = Modifier.heightIn(dimensionResource(R.dimen.padding_medium)))

            CancelButton(
                state = state,
                modifier = Modifier
                    .widthIn(dimensionResource(R.dimen.button_width))
                    .testTag("profileEditButton")
            )

        }

    } else {

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium)),
        ) {

            ConfirmButton(
                state = state,
                onClick = onConfirmClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag("profileEditButton")
            )

            CancelButton(
                state = state,
                modifier = Modifier
                    .widthIn(dimensionResource(R.dimen.button_width))
                    .testTag("profileEditButton")
            )
        }
    }
}

@Composable
fun EditProfileProfileContent(
    state: EditProfileScreenState,
    onValueChanged: (input: String, field: String) -> Unit,
    onValidate: (field: String) -> Unit,
    uiLayout: UiLayout
) {

    val uiColors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = Color.White,
        focusedContainerColor = Color.LightGray,
        focusedBorderColor = Color.LightGray,
        errorContainerColor = Color.Red,
        focusedLabelColor = Color.LightGray,
        unfocusedLabelColor = Color.Black
    )
    // first name input field
    OutlinedTextFieldComponent(
        value = state.firstName,
        onValueChanged = { onValueChanged(it, InputKeys.FIRST_NAME) },
        onFocusLost = { onValidate(InputKeys.FIRST_NAME) },
        isError = false,
        errorMessage = state.firstNameError?.asString(),
        label = R.string.firstName,
        colors = uiColors,
        alternativeUnfocusedLabelColor = Color.Gray
    )

    // last name input field
    OutlinedTextFieldComponent(
        value = state.lastName,
        onValueChanged = { onValueChanged(it, InputKeys.LAST_NAME) },
        onFocusLost = { onValidate(InputKeys.LAST_NAME) },
        isError = false,
        errorMessage = state.lastNameError?.asString(),
        label = R.string.last_name,
        colors = uiColors,
        alternativeUnfocusedLabelColor = Color.Gray
    )

    Column (verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_tiny))) {

        AutoCompleteTextFieldComponent(
            value = state.street.toString(),
            onValueChanged = { onValueChanged(it, InputKeys.STREET) },
            onFocusLost = { onValidate(InputKeys.STREET) },
            isError = state.streetError != null,
            errorMessage = state.streetError?.asString(),
            label = R.string.street,
            optionList = StreetType.entries.map { it.streetName },
            colors = uiColors,
            alternativeUnfocusedLabelColor = Color.Gray
        )


        Row(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_tiny))
        )  {
            Column(modifier = Modifier.weight(0.6f)){
                OutlinedTextFieldComponent(
                    value = state.houseNumber,
                    onValueChanged = { onValueChanged(it, InputKeys.HOUSE_NUMBER) },
                    onFocusLost = { onValidate(InputKeys.HOUSE_NUMBER) },
                    isError = state.houseNumberError != null,
                    errorMessage = state.houseNumberError?.asString(),
                    label = R.string.house_number,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    colors = uiColors,
                    alternativeUnfocusedLabelColor = Color.Gray


                )
            }
            Column(modifier = Modifier.weight(0.3f)) {
                OutlinedTextFieldComponent(
                    value = state.box,
                    onValueChanged = { onValueChanged(it, InputKeys.BOX) },
                    onFocusLost = { onValidate(InputKeys.BOX) },
                    label = R.string.box,
                    colors = uiColors,
                    alternativeUnfocusedLabelColor = Color.Gray
                )
            }
        }
    }

    // email input field
    OutlinedTextFieldComponent(
        value = state.email,
        onValueChanged = { onValueChanged(it, InputKeys.EMAIL) },
        onFocusLost = { onValidate(InputKeys.EMAIL) },
        isError = false,
        errorMessage = state.emailError?.asString(),
        label = R.string.email_label,
        colors = uiColors,
        alternativeUnfocusedLabelColor = Color.Gray
    )


    // phone input field
    OutlinedTextFieldComponent(
        value = state.phone,
        onValueChanged = { onValueChanged(it, InputKeys.PHONE) },
        onFocusLost = { onValidate(InputKeys.PHONE) },
        isError = false,
        errorMessage = state.phoneError?.asString(),
        label = R.string.phone,
        colors = uiColors,
        alternativeUnfocusedLabelColor = Color.Gray
    )


}


@Preview(showBackground = true)
@Composable
fun PortraitPreview() {
    AppTheme {
        EditProfileScreen(
            state = EditProfileScreenState(user = getUser(), isLoading = false, apiError = ""),
            navigateTo = {},
            uiLayout = PORTRAIT_SMALL,
            onValueChanged = { _, _ -> } ,
            onValidate = {},
        )
    }
}

fun getUser(): User {
    return User(
        id = "TestId",
        firstName = "TestFirstName",
        lastName = "TestLastName",
        email = "Test@Test.be",
        password = "TestPassword",
        phone = "TestPhoneNumber",
        dateOfBirth = LocalDateTime.now(),
        address = Address(StreetType.AFRIKALAAN, "TestHouseNumber", "TestBox"),
        roles = listOf(Role("admin"))
    )
}