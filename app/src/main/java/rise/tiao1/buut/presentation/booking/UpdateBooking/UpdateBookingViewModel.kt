package rise.tiao1.buut.presentation.booking.UpdateBooking

import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import rise.tiao1.buut.data.di.MainDispatcher
import rise.tiao1.buut.domain.booking.TimeSlot
import rise.tiao1.buut.domain.booking.addTime
import rise.tiao1.buut.domain.booking.useCases.GetSelectableDatesUseCase
import rise.tiao1.buut.domain.booking.useCases.GetSelectableTimeSlotsUseCase
import rise.tiao1.buut.domain.booking.useCases.UpdateBookingsUseCase
import java.time.LocalDate
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class UpdateBookingViewModel @Inject constructor(
    private val getSelectableDatesUseCase: GetSelectableDatesUseCase,
    private val getSelectableTimeSlotsUseCase: GetSelectableTimeSlotsUseCase,
    private val updateBookingUseCase: UpdateBookingsUseCase,
    @MainDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    @OptIn(ExperimentalMaterial3Api::class)
    private val _state = mutableStateOf(UpdateBookingScreenState())
    val state: State<UpdateBookingScreenState>
        get() = _state

    init {
        getSelectableDates()
    }


    @OptIn(ExperimentalMaterial3Api::class)
    fun getSelectableDates() {
        _state.value = state.value.copy(datesAreLoading = true)
        viewModelScope.launch(dispatcher) {
            try {
                val selectableDates = getSelectableDatesUseCase()

                val updatedDatePickerState = DatePickerState(
                    initialDisplayMode = DisplayMode.Picker,
                    selectableDates = selectableDates,
                    initialSelectedDateMillis = null,
                    yearRange = (LocalDate.now().year..LocalDate.now().plusYears(1L).year),
                    locale = Locale.US
                )

                _state.value = state.value.copy(
                    datePickerState = updatedDatePickerState,
                    isUpdated = true,
                    datesAreLoading = false,
                )

            } catch (e: Exception) {
                _state.value =
                    state.value.copy(datesAreLoading = false, getFreeDatesError = e.message)

            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun getSelectableTimeslots(input: Long) {
        _state.value = state.value.copy(timeslotsAreLoading = true)
        viewModelScope.launch(dispatcher) {
            try {
                val selectableTimeslots = getSelectableTimeSlotsUseCase(input)
                _state.value = state.value.copy(
                    selectableTimeSlots = selectableTimeslots,
                    timeslotsAreLoading = false,
                )

            } catch (e: Exception) {
                _state.value =
                    state.value.copy(timeslotsAreLoading = false, getFreeDatesError = e.message)
            }

        }
    }

    fun updateSelectedDate(input: Long?) {
        if (input != null) {
            getSelectableTimeslots(input)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun onTimeSlotClicked(input: TimeSlot) {
        _state.value = state.value.copy(selectedTimeSlot = input, confirmationModalOpen = true)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun onDismissBooking() {
        _state.value = state.value.copy(selectedTimeSlot = null, confirmationModalOpen = false, confirmationError = "")
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun onUpdateBooking(idOfBookingToUpdate:String) {
        viewModelScope.launch(dispatcher) {
            try {
                state.value.selectedTimeSlot?.let { updateBookingUseCase(idOfBookingToUpdate,it.addTime()) }
                _state.value = state.value.copy(confirmationModalOpen = false, notificationModalOpen = true)
            } catch (e : Exception) {
                _state.value = state.value.copy(confirmationError = e.message)

            }
        }
    }
}