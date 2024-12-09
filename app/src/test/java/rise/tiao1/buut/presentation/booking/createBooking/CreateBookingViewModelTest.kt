package rise.tiao1.buut.presentation.booking.createBooking

import androidx.compose.material3.ExperimentalMaterial3Api
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import rise.tiao1.buut.domain.booking.TimeSlot
import rise.tiao1.buut.domain.booking.useCases.ConfiguredSelectableDates
import rise.tiao1.buut.domain.booking.useCases.CreateBookingsUseCase
import rise.tiao1.buut.domain.booking.useCases.GetSelectableDatesUseCase
import rise.tiao1.buut.domain.booking.useCases.GetSelectableTimeSlotsUseCase

class CreateBookingViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope()
    private val getSelectableDatesUseCase: GetSelectableDatesUseCase = mockk()
    private val getSelectableTimeSlotsUseCase: GetSelectableTimeSlotsUseCase = mockk()
    private val createBookingUseCase: CreateBookingsUseCase = mockk()
    private val today = java.time.LocalDateTime.now()

    @Test
    fun initialState_isProduced() = scope.runTest {
        val viewModel = getViewModel()
        val initialState = viewModel.state.value
        assert(initialState.datesAreLoading)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun getSelectablesDates_startCall_updatesLoadingState() = scope.runTest {
        coEvery { getSelectableDatesUseCase() } coAnswers {
            kotlinx.coroutines.delay(1000L)
            ConfiguredSelectableDates(emptyList())
        }

        val viewModel = getViewModel()
        viewModel.getSelectableDates()

        assert(viewModel.state.value.datesAreLoading)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun getSelectableDates_failedCall_updatesErrorState() = scope.runTest {
        val errorMessage = "Failed to fetch dates"
        coEvery { getSelectableDatesUseCase() } throws Exception(errorMessage)

        val viewModel = getViewModel()
        viewModel.getSelectableDates()

        dispatcher.scheduler.advanceUntilIdle()

        assert(viewModel.state.value.getFreeDatesError == errorMessage)
        assert(!viewModel.state.value.datesAreLoading)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun getSelectableDates_succesfulCall_updatesStateSelectableDatesValue() = scope.runTest {
        val selectableDate = 1630454400000L
        val selectableDates = ConfiguredSelectableDates(listOf(selectableDate))

        coEvery { getSelectableDatesUseCase() } returns selectableDates

        val viewModel = getViewModel()
        viewModel.getSelectableDates()

        dispatcher.scheduler.advanceUntilIdle()

        assert(viewModel.state.value.datePickerState.selectableDates == selectableDates)
    }

    @Test
    fun getSelectableTimeSlots_succesfulCall_returnsTimeslots() = scope.runTest {
        val expected = getSelectableTimeSlots()
        coEvery { getSelectableTimeSlotsUseCase(any()) } returns getSelectableTimeSlots()

        val viewModel = getViewModel()
        viewModel.getSelectableTimeslots(1630454400000L)

        dispatcher.scheduler.advanceUntilIdle()

        assert(viewModel.state.value.selectableTimeSlots == expected)
        assert(!viewModel.state.value.timeslotsAreLoading)
    }

    @Test
    fun getSelectableTimeSlots_failedCall_updatesErrorState() = scope.runTest {
        val errorMessage = "Failed to fetch timeslots"
        coEvery { getSelectableTimeSlotsUseCase(any()) } throws Exception(errorMessage)

        val viewModel = getViewModel()
        viewModel.getSelectableTimeslots(1630454400000L)

        dispatcher.scheduler.advanceUntilIdle()

        assert(viewModel.state.value.getFreeDatesError == errorMessage)
        assert(!viewModel.state.value.timeslotsAreLoading)
    }

    @Test
    fun updateSelectedDate_callSuccessful_updatesTimeSlots() = scope.runTest {
        val expected = getSelectableTimeSlots()
        coEvery { getSelectableTimeSlotsUseCase(any()) } returns getSelectableTimeSlots()
        val viewModel = getViewModel()
        viewModel.updateSelectedDate(1630454400000L)
        dispatcher.scheduler.advanceUntilIdle()
        assert(viewModel.state.value.selectableTimeSlots == expected)
        assert(!viewModel.state.value.timeslotsAreLoading)
    }

    @Test
    fun updateSelectedDate_callFailed_updatesErrorState() = scope.runTest {
        val errorMessage = "Failed to fetch timeslots"
        coEvery { getSelectableTimeSlotsUseCase(any()) } throws Exception(errorMessage)
        val viewModel = getViewModel()
        viewModel.updateSelectedDate(1630454400000L)
        dispatcher.scheduler.advanceUntilIdle()
        assert(viewModel.state.value.getFreeDatesError == errorMessage)
        assert(!viewModel.state.value.timeslotsAreLoading)
    }

    @Test
    fun onTimeSlotClicked_call_updatesState() = scope.runTest {
        val timeslot = getSelectableTimeSlots()[0]
        val viewModel = getViewModel()
        viewModel.onTimeSlotClicked(timeslot)
        assert(viewModel.state.value.selectedTimeSlot == timeslot)
        assert(viewModel.state.value.confirmationModalOpen)
    }

    @Test
    fun onDismissBooking_call_updatesState() = scope.runTest {
        val viewModel = getViewModel()
        viewModel.onDismissBooking()
        assert(viewModel.state.value.selectedTimeSlot == null)
        assert(!viewModel.state.value.confirmationModalOpen)
    }

    @Test
    fun onConfirmBooking_call_updatesState() = scope.runTest {
        coEvery { createBookingUseCase(any()) } returns Unit
        val viewModel = getViewModel()
        viewModel.onConfirmBooking()
        dispatcher.scheduler.advanceUntilIdle()
        assert(!viewModel.state.value.confirmationModalOpen)
        assert(viewModel.state.value.notificationModalOpen)
    }

    @Test
    fun onConfirmBooking_callFailed_updatesErrorState() = scope.runTest {
        coEvery { createBookingUseCase(any()) } throws Exception()
        val viewModel = getViewModel()
        viewModel.onConfirmBooking()
        dispatcher.scheduler.advanceUntilIdle()
        assert(viewModel.state.value.confirmationError != null)
    }

    private fun getViewModel(): CreateBookingViewModel {
        return CreateBookingViewModel(
            getSelectableDatesUseCase,
            getSelectableTimeSlotsUseCase,
            createBookingUseCase,
            dispatcher
        )
    }

    fun getSelectableTimeSlots(): List<TimeSlot> {
        return listOf(
            TimeSlot(today, "Morning", true),
            TimeSlot(today, "Afternoon", false),
            TimeSlot(today, "Evening", true)
        )
    }

}

