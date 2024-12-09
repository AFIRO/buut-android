package rise.tiao1.buut.domain.booking.useCases

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import rise.tiao1.buut.data.repositories.BookingRepository
import rise.tiao1.buut.domain.booking.TimeSlot
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class GetSelectableTimeSlotsUseCaseTest {
    private val today = LocalDateTime.now()
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)
    private val bookingRepository = mockk<BookingRepository>()

    private val useCase = GetSelectableTimeSlotsUseCase(
        bookingRepository = bookingRepository
    )

    @Test
    fun GetSelectableTimeSlotsUseCase_WhenInvoked_ReturnsListOfTimeSlots() = scope.runTest {
        val TestDate =
            LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        coEvery { bookingRepository.getFreeTimeSlotsForDateRange(any()) } returns getSelectableTimeSlots()
        val expected = getSelectableTimeSlots()
        val actual = useCase(TestDate)
        assertEquals(expected, actual)
    }

    fun getSelectableTimeSlots(): List<TimeSlot> {
        return listOf(
            TimeSlot(today, "Morning", true),
            TimeSlot(today, "Afternoon", false),
            TimeSlot(today, "Evening", true)
        )
    }
}