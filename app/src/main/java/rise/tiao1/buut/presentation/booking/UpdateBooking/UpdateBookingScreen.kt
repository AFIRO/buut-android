package rise.tiao1.buut.presentation.booking.UpdateBooking

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import rise.tiao1.buut.R
import rise.tiao1.buut.domain.booking.TimeSlot
import rise.tiao1.buut.presentation.components.ActionErrorContainer
import rise.tiao1.buut.presentation.components.BookingConfirmationModal
import rise.tiao1.buut.presentation.components.ErrorMessageContainer
import rise.tiao1.buut.presentation.components.InfoContainer
import rise.tiao1.buut.presentation.components.LoadingIndicator
import rise.tiao1.buut.presentation.components.NotificationModal
import rise.tiao1.buut.presentation.components.TimeSlotComponent
import rise.tiao1.buut.ui.theme.AppTheme
import rise.tiao1.buut.utils.UiLayout
import rise.tiao1.buut.utils.UiLayout.LANDSCAPE_EXPANDED
import rise.tiao1.buut.utils.UiLayout.LANDSCAPE_MEDIUM
import rise.tiao1.buut.utils.UiLayout.LANDSCAPE_SMALL
import rise.tiao1.buut.utils.UiLayout.PORTRAIT_EXPANDED
import rise.tiao1.buut.utils.UiLayout.PORTRAIT_MEDIUM
import rise.tiao1.buut.utils.UiLayout.PORTRAIT_SMALL
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateBookingScreen(
    state: UpdateBookingScreenState,
    navigateUp: () -> Unit = {},
    onDateSelected: (input: Long?) -> Unit = {},
    onConfirmBooking: () -> Unit = {},
    onUpdateBooking: (idOfBookingToUpdate:String) -> Unit = {},
    onDismissBooking: () -> Unit = {},
    onTimeSlotClicked: (TimeSlot) -> Unit = {},
    toBookingsOverview: () -> Unit = {},
    uiLayout: UiLayout,
    idOfBookingToUpdate: String? = null
) {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Image(
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.image_size_standard))
                            .padding(top = dimensionResource(R.dimen.padding_small)),
                        painter = painterResource(R.drawable.buut_logo),
                        contentDescription = stringResource(R.string.buut_logo),
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.inversePrimary
                ),
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button)
                        )
                    }
                },
                modifier = Modifier.testTag("navigation")
            )

        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (state.confirmationModalOpen) {
                BookingConfirmationModal(state.selectedTimeSlot, state.confirmationError, onConfirmBooking, onDismissBooking, onUpdateBooking, idOfBookingToUpdate)
            }
            if (state.notificationModalOpen) {
                NotificationModal(stringResource(R.string.booking_updated_info_follows), onConfirm = toBookingsOverview)
            }
            if (state.datesAreLoading) {
                LoadingIndicator()
            } else if (!state.getFreeDatesError.isNullOrBlank()) {
                ActionErrorContainer(state.getFreeDatesError)
            } else {
                when (uiLayout) {

                    PORTRAIT_SMALL, PORTRAIT_MEDIUM, PORTRAIT_EXPANDED -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(dimensionResource(R.dimen.padding_medium))
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(0.6f)
                                    .fillMaxSize()
                            ) {
                                DatePicker(state, onDateSelected)
                            }
                            Column(
                                modifier = Modifier
                                    .weight(0.4f)
                                    .fillMaxSize()
                            ) {
                                TimeSlots(state, onTimeSlotClicked)
                            }
                        }
                    }

                    LANDSCAPE_SMALL, LANDSCAPE_MEDIUM, LANDSCAPE_EXPANDED -> {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(dimensionResource(R.dimen.padding_medium))

                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(0.7f)
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                DatePicker(state, onDateSelected)
                            }
                            Column(
                                modifier = Modifier
                                    .weight(0.3f)
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.Center
                            ) {
                                TimeSlots(state, onTimeSlotClicked)
                            }
                        }
                    }
                }
            }

        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    state: UpdateBookingScreenState,
    onDateSelected: (input: Long?) -> Unit = {},
) {


    DatePicker(
        state = state.datePickerState,
        title = null,
        headline = null,
        showModeToggle = false,
        modifier = Modifier
            .testTag(stringResource(R.string.calendar)),

        )

    LaunchedEffect(state.datePickerState.selectedDateMillis) {
        if (state.datePickerState.selectedDateMillis != null) {
            onDateSelected(state.datePickerState.selectedDateMillis!!)
        } else {
            onDateSelected(null)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSlots(state: UpdateBookingScreenState, onTimeSlotClicked: (TimeSlot) -> Unit = {}) {
    if (state.timeslotsAreLoading) {
        LoadingIndicator()
    } else if (state.getFreeDatesError?.isNotEmpty() == true) {
        ErrorMessageContainer(state.getFreeDatesError)
    } else if (state.selectableTimeSlots.isEmpty()) {
             InfoContainer(stringResource(R.string.select_date))
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_large)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            state.selectableTimeSlots.forEach { timeSlot ->
                TimeSlotComponent(timeSlot, onTimeSlotClicked)
                Spacer(modifier = Modifier.heightIn(dimensionResource(R.dimen.padding_medium)))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PortraitPreview() {
    AppTheme {
        UpdateBookingScreen(
            UpdateBookingScreenState(), {}, uiLayout = PORTRAIT_SMALL
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    widthDp = 600,
    heightDp = 300,
    uiMode = Configuration.ORIENTATION_LANDSCAPE
)
@Composable
fun LandscapePreview() {
    AppTheme {
        UpdateBookingScreen(
            UpdateBookingScreenState(), {}, uiLayout = LANDSCAPE_SMALL
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    widthDp = 610,
    heightDp = 890,
    uiMode = Configuration.ORIENTATION_PORTRAIT
)
@Composable
fun PortraitMediumPreview() {
    AppTheme {
        UpdateBookingScreen(
            UpdateBookingScreenState(
                selectableTimeSlots = listOf(
                    TimeSlot(
                        LocalDateTime.now().plusDays(2),
                        "Morning", true
                    ),
                    TimeSlot(
                        LocalDateTime.now().plusDays(2),
                        "Afternoon", false
                    ),
                    TimeSlot(
                        LocalDateTime.now().plusDays(2),
                        "Evening", true
                    ),
                )
            ), {}, uiLayout = PORTRAIT_MEDIUM
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    widthDp = 830,
    heightDp = 482,
    uiMode = Configuration.ORIENTATION_LANDSCAPE
)
@Composable
fun LandscapeMediumPreview() {
    AppTheme {
        UpdateBookingScreen(
            UpdateBookingScreenState(
                selectableTimeSlots = listOf(
                    TimeSlot(
                        LocalDateTime.now().plusDays(2),
                        "Morning", true
                    ),
                    TimeSlot(
                        LocalDateTime.now().plusDays(2),
                        "Afternoon", false
                    ),
                    TimeSlot(
                        LocalDateTime.now().plusDays(2),
                        "Evening", true
                    ),
                )
            ), {}, uiLayout = LANDSCAPE_MEDIUM
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    widthDp = 600,
    heightDp = 1000,
    uiMode = Configuration.ORIENTATION_PORTRAIT
)
@Composable
fun PortraitExpandedPreview() {
    AppTheme {
        UpdateBookingScreen(
            UpdateBookingScreenState(), {}, uiLayout = PORTRAIT_EXPANDED
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    widthDp = 1000,
    heightDp = 480,
    uiMode = Configuration.ORIENTATION_LANDSCAPE
)
@Composable
fun LandscapeExpandedPreview() {
    AppTheme {
        UpdateBookingScreen(
            UpdateBookingScreenState(), {}, uiLayout = LANDSCAPE_EXPANDED
        )
    }
}




