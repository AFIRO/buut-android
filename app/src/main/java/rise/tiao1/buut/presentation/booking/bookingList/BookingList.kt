package rise.tiao1.buut.presentation.booking.bookingList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.DirectionsBoatFilled
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Houseboat
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import rise.tiao1.buut.R
import rise.tiao1.buut.domain.booking.Booking
import rise.tiao1.buut.presentation.components.ActionErrorContainer
import rise.tiao1.buut.presentation.components.CallBatteryOwnerButton
import rise.tiao1.buut.presentation.components.InfoContainer
import rise.tiao1.buut.presentation.components.LoadingIndicator
import rise.tiao1.buut.presentation.components.SendEmailToBatteryOwner
import rise.tiao1.buut.presentation.home.HomeScreenState
import rise.tiao1.buut.ui.theme.md_theme_light_background
import rise.tiao1.buut.ui.theme.md_theme_light_inverseSurface
import rise.tiao1.buut.ui.theme.md_theme_light_onBackground
import rise.tiao1.buut.ui.theme.md_theme_light_onPrimary
import rise.tiao1.buut.ui.theme.md_theme_light_outline
import rise.tiao1.buut.ui.theme.md_theme_light_outlineVariant
import rise.tiao1.buut.ui.theme.md_theme_light_primaryContainer
import rise.tiao1.buut.ui.theme.md_theme_light_secondary
import rise.tiao1.buut.ui.theme.md_theme_light_surface
import rise.tiao1.buut.ui.theme.md_theme_light_surfaceVariant
import rise.tiao1.buut.ui.theme.md_theme_light_tertiary
import rise.tiao1.buut.utils.toDateString
import rise.tiao1.buut.utils.toTimeString
import java.time.LocalDateTime


@Composable
fun BookingList(
    state: HomeScreenState,
    onEditClicked: (String) -> Unit,
) {
    when {
        state.isLoading -> {
            LoadingIndicator()
        }

        !state.apiError.isNullOrBlank() -> {
            ActionErrorContainer(state.apiError)
        }

        state.bookings.isEmpty() -> {
            InfoContainer(stringResource(R.string.user_has_no_bookings))
        }

        else -> {
            val lazyListState = rememberLazyListState()
            val coroutineScope = rememberCoroutineScope()

            val firstUpcomingBookingIndex = state.bookings.indexOfFirst {
                it.date.isAfter(LocalDateTime.now().minusDays(1))
            }

            LaunchedEffect(firstUpcomingBookingIndex) {
                if (firstUpcomingBookingIndex >= 0) {
                    coroutineScope.launch {
                        lazyListState.scrollToItem(firstUpcomingBookingIndex, scrollOffset = 0)
                    }
                }
            }

            LazyColumn(state = lazyListState) {
                itemsIndexed(state.bookings) { index, booking ->
                    BookingItem(
                        item = booking,
                        isExpanded = index == firstUpcomingBookingIndex,
                        modifier = Modifier.padding(dimensionResource(R.dimen.padding_tiny)),
                        onEditClicked = onEditClicked
                    )
                }
            }
        }
    }
}

@Composable
fun BookingItem(
    item: Booking,
    isExpanded: Boolean = false,
    modifier: Modifier,
    onEditClicked: (String) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(isExpanded) }
    val isHistory = item.date.isBefore(LocalDateTime.now().minusDays(1))

    Card(
        modifier = modifier
            .alpha(if (isHistory) 0.5f else 1f)
            .semantics { testTag = if (isHistory) "PastBooking" else "UpcomingBooking" }
            .semantics { testTag = "BookingItem" }
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.padding_medium),
                vertical = dimensionResource(R.dimen.padding_small)
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))) {
                    Box(modifier = Modifier.widthIn(180.dp)) {
                        Column {
                            Text(
                                text = item.date.toDateString(),
                                style = MaterialTheme.typography.labelLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_small))
                            )
                            Text(
                                text = item.date.toTimeString(),
                                style = MaterialTheme.typography.labelMedium,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                    if (item.boat == null
                        && item.battery == null
                        && item.date.isAfter(LocalDateTime.now())
                    ) {
                        IconButton(
                            onClick = { onEditClicked(item.id) },
                            modifier = Modifier.testTag("bookingEditButton")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = stringResource(R.string.edit_booking_button),
                            )
                        }
                    }
                }


                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.semantics { this.testTag = "ExpandButton" }
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = stringResource(R.string.expand_button_content_description),
                    )
                }

            }
            if (expanded) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_medium))
                )
                BookingDetails(
                    item.boat,
                    item.battery,
                    item.batteryUserFirstName,
                    item.batteryUserLastName,
                    item.batteryUserEmail,
                    item.batteryUserPhoneNumber,
                )

            }
        }
    }
}

@Composable
fun BookingDetails(
    boat: String? = null,
    battery: String? = null,
    batteryUserFirstName: String? = null,
    batteryUserLastName: String? = null,
    batteryUserEmail: String? = null,
    batteryUserPhoneNumber: String? = null,
) {
    var batteryDetailsExpanded by rememberSaveable { mutableStateOf(false) }
    Column {
        if (boat != null) {

            ListItem(
                headlineContent = {
                    Text(
                        "$boat",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Filled.DirectionsBoatFilled,
                        contentDescription =  stringResource(R.string.boat),
                        Modifier.padding(
                            start = dimensionResource(R.dimen.padding_small),
                            end = dimensionResource(R.dimen.padding_medium)
                        ),
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                colors = ListItemDefaults.colors(
                    md_theme_light_surfaceVariant )
            )
        } else {
            Text(
                text = stringResource(
                    R.string.no_boat_assigned
                ),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_small))
            )
        }

        if (battery != null) {
            ListItem(
                headlineContent = {
                    Text(
                        "$battery",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Filled.BatteryChargingFull,
                        contentDescription =  stringResource(R.string.boat),
                        Modifier.padding(
                            start = dimensionResource(R.dimen.padding_small),
                            end = dimensionResource(R.dimen.padding_medium)
                        ),
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                colors = ListItemDefaults.colors(
                    md_theme_light_surfaceVariant )
            )
        } else {
            Text(
                text = stringResource(
                    R.string.no_battery_assigned
                ),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_small))
            )
        }

        if (batteryUserFirstName != null || batteryUserLastName != null || batteryUserEmail != null || batteryUserPhoneNumber != null) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.click_for_battery_user_info))
                IconButton(
                    onClick = { batteryDetailsExpanded = !batteryDetailsExpanded },
                    modifier = Modifier.semantics { this.testTag = "BatteryExpandButton" }
                ) {
                    Icon(
                        imageVector = if (batteryDetailsExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = stringResource(R.string.battery_info_expanded_content_info),
                    )
                }
            }
        }
    }

    if (batteryDetailsExpanded) {
        Card(modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))) {
            BatteryDetails(
                batteryUserFirstName,
                batteryUserLastName,
                batteryUserEmail,
                batteryUserPhoneNumber
            )
        }
    }
}


@Composable
fun BatteryDetails(
    batteryUserFirstName: String? = null,
    batteryUserLastName: String? = null,
    batteryUserEmail: String? = null,
    batteryUserPhoneNumber: String? = null
) {
    if (batteryUserFirstName != null || batteryUserLastName != null || batteryUserEmail != null || batteryUserPhoneNumber != null) {
        Column {
            if (batteryUserFirstName != null && batteryUserLastName != null) {
                ListItem(
                    headlineContent = {
                        Text(
                            "$batteryUserFirstName $batteryUserLastName",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Filled.SupervisedUserCircle,
                            contentDescription = "Battery User",
                            Modifier.padding(
                                start = dimensionResource(R.dimen.padding_small),
                                end = dimensionResource(R.dimen.padding_medium)
                            ),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                )
            }

            if (batteryUserPhoneNumber != null) {
                ListItem(
                    headlineContent = {
                        Text(
                            batteryUserPhoneNumber,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    leadingContent = { CallBatteryOwnerButton(batteryUserPhoneNumber) }
                )
            }

            if (batteryUserEmail != null) {
                ListItem(
                    headlineContent = {
                        Text(
                            batteryUserEmail,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    leadingContent = { SendEmailToBatteryOwner(batteryUserEmail) },
                )
            }
        }
    }
}