package rise.tiao1.buut.presentation.profile

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import rise.tiao1.buut.R
import rise.tiao1.buut.data.remote.user.dto.RoleDTO
import rise.tiao1.buut.domain.user.Address
import rise.tiao1.buut.domain.user.Role
import rise.tiao1.buut.domain.user.User
import rise.tiao1.buut.presentation.components.ButtonComponent
import rise.tiao1.buut.presentation.components.ErrorMessageContainer
import rise.tiao1.buut.presentation.components.HeaderOne
import rise.tiao1.buut.presentation.components.LoadingIndicator
import rise.tiao1.buut.presentation.components.Navigation
import rise.tiao1.buut.presentation.editProfile.TAG
import rise.tiao1.buut.ui.theme.AppTheme
import rise.tiao1.buut.utils.NavigationKeys
import rise.tiao1.buut.utils.StreetType
import rise.tiao1.buut.utils.UiLayout
import rise.tiao1.buut.utils.UiLayout.LANDSCAPE_EXPANDED
import rise.tiao1.buut.utils.UiLayout.LANDSCAPE_MEDIUM
import rise.tiao1.buut.utils.UiLayout.LANDSCAPE_SMALL
import rise.tiao1.buut.utils.UiLayout.PORTRAIT_EXPANDED
import rise.tiao1.buut.utils.UiLayout.PORTRAIT_MEDIUM
import rise.tiao1.buut.utils.UiLayout.PORTRAIT_SMALL
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.collections.listOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    state: ProfileScreenState,
    logout: () -> Unit,
    navigateTo: (String) -> Unit,
    uiLayout: UiLayout,
    navigateUp: () -> Unit = {},
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
                modifier = Modifier.testTag("navigation"),
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button)
                        )
                    }
                },
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
                    Content(state, navigateTo, logout, uiLayout)
                }
            } else {
                Row {
                    Navigation(
                        uiLayout = uiLayout,
                        navigateTo = navigateTo,
                        currentPage = NavigationKeys.Route.PROFILE,
                        content = { Content(state, navigateTo, logout, uiLayout) }
                    )
                    Content(state, navigateTo, logout, uiLayout)
                }
            }
        }
    }
}


@Composable
fun Content(
    state: ProfileScreenState,
    navigateTo: (String) -> Unit,
    logout: () -> Unit,
    uiLayout: UiLayout,
) {
    if (state.isLoading) {
        LoadingIndicator()
    }
    if (state.apiError?.isNotEmpty() == true) {
        Log.e(TAG, state.apiError)
        ErrorMessageContainer(state.apiError)
    } else {
        Column(verticalArrangement = Arrangement.Top, modifier = Modifier.verticalScroll(rememberScrollState())) {
            ProfileContent(state, uiLayout)
            ButtonContent(state, navigateTo, logout, uiLayout)
        }
    }
}

@Composable
fun ProfileContent(state: ProfileScreenState, uiLayout: UiLayout) {
    if (uiLayout != LANDSCAPE_SMALL) {
        HeaderOne(stringResource(R.string.profile_button))
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = dimensionResource(R.dimen.padding_small),
                horizontal = dimensionResource(R.dimen.padding_large)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
    ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally // Or Alignment.Start for left alignment
                ) {
                    Text(
                        text = "${state.user?.firstName} ${state.user?.lastName}",
                        modifier = Modifier.testTag("name")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${state.user?.email ?: ""} ${state.user?.phone ?: ""}",
                        modifier = Modifier.testTag("mail+phone")

                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${state.user?.dateOfBirth?.format(DateTimeFormatter.ofLocalizedDate(
                            FormatStyle.SHORT))}",
                        modifier = Modifier.testTag("dob")
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${state.user?.address?.street?.streetName} ${state.user?.address?.houseNumber} ${state.user?.address?.box ?: ""}",
                        modifier = Modifier.testTag("address")
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                }
            }
    }
}

@Composable
fun ButtonContent(
    state: ProfileScreenState,
    navigateTo: (String) -> Unit,
    logout: () -> Unit,
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
            ButtonComponent(
                label = R.string.edit_profile_button,
                onClick = { navigateTo(NavigationKeys.Route.EDIT_PROFILE) },
                isLoading = state.isLoading,
                modifier = Modifier
                    .widthIn(dimensionResource(R.dimen.button_width))
                    .testTag("profileEditButton")
            )

            Spacer(modifier = Modifier.heightIn(dimensionResource(R.dimen.padding_medium)))

            ButtonComponent(
                label = R.string.log_out_button,
                onClick = logout,
                isLoading = state.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                modifier = Modifier
                    .widthIn(dimensionResource(R.dimen.button_width))
                    .testTag("profileLogoutButton")
            )

        }

    } else {

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium)),
        ) {

            ButtonComponent(
                label = R.string.edit_profile_button,
                onClick = { navigateTo(NavigationKeys.Route.EDIT_PROFILE) },
                isLoading = state.isLoading,
                modifier = Modifier.weight(1f).testTag("profileEditButton")
            )

            ButtonComponent(
                label = R.string.log_out_button,
                onClick = logout,
                isLoading = state.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                modifier = Modifier.weight(1f).testTag("profileLogoutButton")
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PortraitPreview() {
    AppTheme {
        ProfileScreen(
            state = ProfileScreenState(user = getUser(), isLoading = false, apiError = ""),
            logout = {},
            navigateTo = {},
            uiLayout = PORTRAIT_SMALL
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
fun LandscapePreview() {
    AppTheme {
        ProfileScreen(
            state = ProfileScreenState(user = getUser(), isLoading = false, apiError = ""),
            logout = {},
            navigateTo = {},
            uiLayout = LANDSCAPE_SMALL
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
fun PortraitMediumPreview() {
    AppTheme {
        ProfileScreen(
            state = ProfileScreenState(user = getUser(), isLoading = false, apiError = ""),
            logout = {},
            navigateTo = {},
            uiLayout = PORTRAIT_MEDIUM
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
fun LandscapeMediumPreview() {
    AppTheme {
        ProfileScreen(
            state = ProfileScreenState(user = getUser(), isLoading = false, apiError = ""),
            logout = {},
            navigateTo = {},
            uiLayout = LANDSCAPE_MEDIUM
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
fun PortraitExpandedPreview() {
    AppTheme {
        ProfileScreen(
            state = ProfileScreenState(user = getUser(), isLoading = false, apiError = ""),
            logout = {},
            navigateTo = {},
            uiLayout = PORTRAIT_EXPANDED
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 1000,
    heightDp = 480,
    uiMode = Configuration.ORIENTATION_LANDSCAPE
)
@Composable
fun LandscapeExpandedPreview() {
    AppTheme {
        ProfileScreen(
            state = ProfileScreenState(user = getUser(), isLoading = false, apiError = ""),
            logout = {},
            navigateTo = {},
            uiLayout = LANDSCAPE_EXPANDED
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
        dateOfBirth =  LocalDateTime.now(),
        address = Address(StreetType.AFRIKALAAN, "TestHouseNumber", "TestBox"),
                roles = listOf(Role("admin"))
    )
}