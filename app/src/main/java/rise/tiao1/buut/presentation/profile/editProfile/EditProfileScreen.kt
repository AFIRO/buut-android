package rise.tiao1.buut.presentation.profile.editProfile

import android.content.res.Configuration
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import rise.tiao1.buut.domain.user.Address
import rise.tiao1.buut.domain.user.Role
import rise.tiao1.buut.domain.user.User
import rise.tiao1.buut.presentation.components.ActionErrorContainer
import rise.tiao1.buut.presentation.components.AutoCompleteTextFieldComponent
import rise.tiao1.buut.presentation.components.ButtonComponent
import rise.tiao1.buut.presentation.components.HeaderOne
import rise.tiao1.buut.presentation.components.LoadingIndicator
import rise.tiao1.buut.presentation.components.OutlinedTextFieldComponent
import rise.tiao1.buut.presentation.profile.detailProfile.getUser
import rise.tiao1.buut.ui.theme.AppTheme
import rise.tiao1.buut.utils.InputKeys
import rise.tiao1.buut.utils.StreetType
import rise.tiao1.buut.utils.UiLayout
import rise.tiao1.buut.utils.UiLayout.LANDSCAPE_EXPANDED
import rise.tiao1.buut.utils.UiLayout.LANDSCAPE_MEDIUM
import rise.tiao1.buut.utils.UiLayout.LANDSCAPE_SMALL
import rise.tiao1.buut.utils.UiLayout.PORTRAIT_EXPANDED
import rise.tiao1.buut.utils.UiLayout.PORTRAIT_MEDIUM
import rise.tiao1.buut.utils.UiLayout.PORTRAIT_SMALL
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
    onCancelClick: () -> Unit = {},
    onValidate: (field: String) -> Unit,
    navigateUp: () -> Unit = {},
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
                modifier = Modifier.testTag("topBar"),
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button)
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            EditProfileScreenContent(
                state = state,
                navigateTo = navigateTo,
                onConfirmClick = onConfirmClick,
                onCancelClick = onCancelClick,
                onValueChanged = onValueChanged,
                onValidate = onValidate,
                uiLayout = uiLayout
            )
        }
    }
}

@Composable
fun EditProfileScreenContent(
    state: EditProfileScreenState,
    navigateTo: (String) -> Unit,
    onConfirmClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onValueChanged: (input: String, field: String) -> Unit,
    onValidate: (field: String) -> Unit,
    uiLayout: UiLayout,
    modifier: Modifier = Modifier
) {

    if (state.isLoading) {
        LoadingIndicator()
    } else if (state.apiError?.isNotEmpty() == true) {
        ActionErrorContainer(state.apiError)
    } else {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {

            EditProfileProfileContent(
                state = state,
                onValueChanged = onValueChanged,
                onValidate = onValidate,
                uiLayout = uiLayout
            )
            EditProfileButtonContent(
                state = state,
                onConfirmClick= onConfirmClick,
                onCancelClick = onCancelClick,
                navigateTo = navigateTo,
                uiLayout = uiLayout
            )
        }
    }
}

@Composable
fun CancelButton(
    state: EditProfileScreenState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ButtonComponent(
        label = R.string.cancel,
        onClick = onClick,
        isLoading = state.isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError
        ),
        modifier = modifier.testTag("profileEditCancelButton")
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
        modifier = modifier.testTag("profileEditConfirmButton")

    )
}




@Composable
fun EditProfileButtonContent(
    state: EditProfileScreenState,
    onConfirmClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
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
            )

            Spacer(modifier = Modifier.heightIn(dimensionResource(R.dimen.padding_medium)))

            CancelButton(
                state = state,
                modifier = Modifier
                    .widthIn(dimensionResource(R.dimen.button_width)),
                onClick = onCancelClick
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
                    .testTag("profileEditConfirmButton")
            )

            CancelButton(
                state = state,
                onClick = onCancelClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag("profileEditCancelButton")
            )
        }
    }
}

@Composable
fun EditProfileScreenNameComponent(
    state: EditProfileScreenState,
    onValueChanged: (input: String, field: String) -> Unit,
    onValidate: (field: String) -> Unit,
    uiLayout: UiLayout,
    uiColors: TextFieldColors,
) {
    Column {
        // first name input field
        OutlinedTextFieldComponent(
            value = state.firstName,
            onValueChanged = { onValueChanged(it, InputKeys.FIRST_NAME) },
            onFocusLost = { onValidate(InputKeys.FIRST_NAME) },
            isError = false,
            errorMessage = state.firstNameError?.asString(),
            label = R.string.firstName,
            colors = uiColors,
            alternativeUnfocusedLabelColor = Color.Gray,
            modifier = Modifier.testTag("firstNameTextField")
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
            alternativeUnfocusedLabelColor = Color.Gray,
            modifier = Modifier.testTag("lastNameTextField")
        )
    }
}

@Composable
fun EditProfileScreenContactComponent(
    state: EditProfileScreenState,
    onValueChanged: (input: String, field: String) -> Unit,
    onValidate: (field: String) -> Unit,
    uiLayout: UiLayout,
    uiColors: TextFieldColors,
) {
    Column{
        // phone input field
        OutlinedTextFieldComponent(
            value = state.phone,
            onValueChanged = { onValueChanged(it, InputKeys.PHONE) },
            onFocusLost = { onValidate(InputKeys.PHONE) },
            isError = false,
            errorMessage = state.phoneError?.asString(),
            label = R.string.phone,
            colors = uiColors,
            alternativeUnfocusedLabelColor = Color.Gray,
            modifier = Modifier.testTag("phoneTextField")
        )
    }
}

@Composable
fun EditProfileScreenAddressComponent(
    state: EditProfileScreenState,
    onValueChanged: (input: String, field: String) -> Unit,
    onValidate: (field: String) -> Unit,
    uiLayout: UiLayout,
    uiColors: TextFieldColors,
) {
    Column {

        AutoCompleteTextFieldComponent(
            value = state.street,
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
                    alternativeUnfocusedLabelColor = Color.Gray,
                    modifier = Modifier.testTag("houseNumberTextField")


                )
            }
            Column(modifier = Modifier.weight(0.3f)) {
                OutlinedTextFieldComponent(
                    value = state.box,
                    onValueChanged = { onValueChanged(it, InputKeys.BOX) },
                    onFocusLost = { onValidate(InputKeys.BOX) },
                    label = R.string.box,
                    colors = uiColors,
                    alternativeUnfocusedLabelColor = Color.Gray,
                    modifier = Modifier.testTag("addressBoxTextField")
                )
            }
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

    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Log.d(
            TAG, uiLayout.name
        )
        if (uiLayout != LANDSCAPE_SMALL) {
            HeaderOne(stringResource(R.string.edit_profile_page_title),)
        }

        if (uiLayout == LANDSCAPE_SMALL || uiLayout == LANDSCAPE_MEDIUM || uiLayout == LANDSCAPE_EXPANDED){
             Row (
                 horizontalArrangement = Arrangement.spacedBy(8.dp),
                 modifier = Modifier
                     .fillMaxWidth()
                     .padding(dimensionResource(R.dimen.padding_medium))
             ){
                 Column(
                     horizontalAlignment = Alignment.End,
                     modifier = Modifier.weight(1f)
                 ){
                     EditProfileScreenNameComponent(
                         state = state,
                         onValueChanged = onValueChanged,
                         onValidate = onValidate,
                         uiLayout = uiLayout,
                         uiColors = uiColors,
                     )
                     EditProfileScreenContactComponent(
                         state = state,
                         onValueChanged = onValueChanged,
                         onValidate = onValidate,
                         uiLayout = uiLayout,
                         uiColors = uiColors
                     )
                 }
                 Column (
                     horizontalAlignment = Alignment.Start,
                     modifier = Modifier.weight(1f)
                 ){
                     EditProfileScreenAddressComponent(
                         state = state,
                         onValueChanged = onValueChanged,
                         onValidate = onValidate,
                         uiLayout = uiLayout,
                         uiColors = uiColors,
                         )
                 }

                }
        }
        else {
            Column {
                EditProfileScreenNameComponent(
                    state = state,
                    onValueChanged = onValueChanged,
                    onValidate = onValidate,
                    uiLayout = uiLayout,
                    uiColors = uiColors
                )

                EditProfileScreenAddressComponent(
                    state = state,
                    onValueChanged = onValueChanged,
                    onValidate = onValidate,
                    uiLayout = uiLayout,
                    uiColors = uiColors
                )

                EditProfileScreenContactComponent(
                    state = state,
                    onValueChanged = onValueChanged,
                    onValidate = onValidate,
                    uiLayout = uiLayout,
                    uiColors = uiColors
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    uiMode = Configuration.ORIENTATION_PORTRAIT
)
@Composable
fun EditProfilePortraitSmallPreview() {
    AppTheme {
        EditProfileScreen(
            state = EditProfileScreenState(user = getUser(), isLoading = false, apiError = ""),
            navigateTo = {},
            onValueChanged = { _, _ -> } ,
            onValidate = {},
            uiLayout = PORTRAIT_SMALL,
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 610,
    heightDp = 890,
    uiMode = Configuration.ORIENTATION_PORTRAIT
)
@Composable
fun EditProfilePortraitMediumPreview() {
    AppTheme {
        EditProfileScreen(
            state = EditProfileScreenState(user = getUser(), isLoading = false, apiError = ""),
            navigateTo = {},
            onValueChanged = { _, _ -> } ,
            onValidate = {},
            uiLayout = PORTRAIT_MEDIUM,
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 600,
    heightDp = 1000,
    uiMode = Configuration.ORIENTATION_PORTRAIT
)
@Composable
fun EditProfileExpandedPortraitPreview() {
    AppTheme {
        EditProfileScreen(
            state = EditProfileScreenState(user = getUser(), isLoading = false, apiError = ""),
            navigateTo = {},
            onValueChanged = { _, _ -> } ,
            onValidate = {},
            uiLayout = PORTRAIT_EXPANDED
        )
    }
}



@Preview(
    showBackground = true,
    widthDp = 600,
    heightDp = 300,
    uiMode = Configuration.ORIENTATION_LANDSCAPE
)
@Composable
fun EditProfileLandscapeSmallPreview() {
    AppTheme {
        EditProfileScreen(
            state = EditProfileScreenState(user = getUser(), isLoading = false, apiError = ""),
            navigateTo = {},
            onValueChanged = { _, _ -> } ,
            onValidate = {},
            uiLayout = LANDSCAPE_SMALL
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 830,
    heightDp = 482,
    uiMode = Configuration.ORIENTATION_LANDSCAPE
)
@Composable
fun EditProfileLandscapeMediumPreview() {
    AppTheme {
        EditProfileScreen(
            state = EditProfileScreenState(user = getUser(), isLoading = false, apiError = ""),
            navigateTo = {},
            onValueChanged = { _, _ -> } ,
            onValidate = {},
            uiLayout = LANDSCAPE_MEDIUM,
        )
    }
}
@Preview(
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240",
    uiMode = Configuration.ORIENTATION_LANDSCAPE
)
@Composable
fun EditProfileExpandedLandscapePreview() {
    AppTheme {
        EditProfileScreen(
            state = EditProfileScreenState(user = getUser(), isLoading = false, apiError = ""),
            navigateTo = {},
            onValueChanged = { _, _ -> } ,
            onValidate = {},
            uiLayout = LANDSCAPE_MEDIUM
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