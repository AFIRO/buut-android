package rise.tiao1.buut.presentation.booking.UpdateBooking

import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import rise.tiao1.buut.domain.booking.TimeSlot
import java.time.LocalDate
import java.util.Locale

data class UpdateBookingScreenState @OptIn(ExperimentalMaterial3Api::class) constructor(

    val datePickerState: DatePickerState = DatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = null,
        yearRange = (LocalDate.now().year..LocalDate.now().plusYears(1L).year),
        locale = Locale.US
    ),
    val isUpdated: Boolean = true,
    val datesAreLoading: Boolean = false,
    val getFreeDatesError: String? = "",
    val timeslotsAreLoading: Boolean = false,
    val selectableTimeSlots: List<TimeSlot> = emptyList(),
    val confirmationModalOpen: Boolean = false,
    val selectedTimeSlot: TimeSlot? = null,
    val confirmationError: String? = "",
    val notificationModalOpen: Boolean = false,
)

