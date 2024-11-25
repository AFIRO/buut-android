package rise.tiao1.buut.presentation.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun datePicker(
    modifier: Modifier = Modifier,
): LocalDate? {
    val dateTime = LocalDateTime.now()
    val datePickerState = remember {
        DatePickerState(
            initialSelectedDateMillis = dateTime.toMillis(),
            initialDisplayedMonthMillis = null,
            initialDisplayMode = DisplayMode.Picker,
            yearRange = (2024..2025),
            locale = Locale.US,
            selectableDates = CustomSelectableDates,
        )
    }

    DatePicker(
        state = datePickerState,
        modifier = modifier,
        showModeToggle = false,
        title = {},

    )

    var selectedDate = datePickerState.selectedDateMillis?.let { millis ->
        Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }
    return selectedDate


}

@OptIn(ExperimentalMaterial3Api::class)
object CustomSelectableDates: SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis > LocalDateTime.now().plusDays(2).toMillis()
    }
}



fun LocalDateTime.toMillis() = this.atZone(ZoneId.of("GMT")).toInstant().toEpochMilli()