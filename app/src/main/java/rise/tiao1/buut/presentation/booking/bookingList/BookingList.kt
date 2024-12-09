package rise.tiao1.buut.presentation.booking.bookingList

import android.content.Context
import android.net.ConnectivityManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
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
import rise.tiao1.buut.presentation.components.InfoContainer
import rise.tiao1.buut.presentation.components.LoadingIndicator
import rise.tiao1.buut.presentation.home.HomeScreenState
import rise.tiao1.buut.utils.toDateString
import rise.tiao1.buut.utils.toTimeString
import java.time.LocalDateTime

@Composable
fun BookingList(
    state: HomeScreenState,
    onEditClicked: (String) -> Unit,
) {
    val connectivityManager =
        LocalContext.current.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val isNetworkAvailable = connectivityManager.activeNetwork != null


    Box(modifier = Modifier.fillMaxSize()) {
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

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = if (!isNetworkAvailable || !state.apiError.isNullOrBlank()) 110.dp else 0.dp)
        ) {
            itemsIndexed(state.bookings) { index, booking ->
                BookingItem(
                    item = booking,
                    isExpanded = index == firstUpcomingBookingIndex,
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_tiny)),
                    onEditClicked = onEditClicked,
                    editEnabled = isNetworkAvailable
                )
            }
        }

        if (!isNetworkAvailable) {
            ActionErrorContainer(
                LocalContext.current.getString(R.string.no_internet_connection),
            )
        } else if (state.isLoading) {
            LoadingIndicator()
        } else if (!state.apiError.isNullOrBlank()) {
            ActionErrorContainer(state.apiError)
        } else if (state.bookings.isEmpty()) {
            InfoContainer(stringResource(R.string.user_has_no_bookings))
        }
    }
}

@Composable
fun BookingItem(
    item: Booking,
    isExpanded: Boolean = false,
    modifier: Modifier,
    onEditClicked: (String) -> Unit,
    editEnabled: Boolean = true
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
                            modifier = Modifier.testTag("bookingEditButton"),
                            enabled = editEnabled
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
                )

            }
        }
    }
}

@Composable
fun BookingDetails(
    boat: String? = null,
    battery: String? = null,
) {
    Column {
        Text(
            text = if (boat != null) stringResource(R.string.boat) + ": $boat" else stringResource(
                R.string.no_boat_assigned
            ),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_small))
        )

        Text(
            text = if (battery != null) stringResource(R.string.battery) + ": $battery" else stringResource(
                R.string.no_battery_assigned
            ),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}