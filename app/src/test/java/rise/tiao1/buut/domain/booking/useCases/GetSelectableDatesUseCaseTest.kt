package rise.tiao1.buut.domain.booking.useCases

import androidx.compose.material3.ExperimentalMaterial3Api
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import rise.tiao1.buut.data.repositories.BookingRepository
import rise.tiao1.buut.domain.booking.TimeSlot
import rise.tiao1.buut.presentation.components.toMillis
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class GetSelectableDatesUseCaseTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)
    private val bookingRepository = mockk<BookingRepository>()
    private val today = LocalDateTime.now()
    private val useCase = GetSelectableDatesUseCase(
        bookingRepository = bookingRepository
    )


    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun getSelectableDates_ReturnsSelectableDates() =
        scope.runTest {
            coEvery { bookingRepository.getAvailableDays() } returns getSelectableTimeSlots()
            val expected = today.atZone(ZoneId.of("GMT")).toInstant().toEpochMilli()
            val actual = useCase()
            assert(actual.isSelectableDate(expected))
        }


    fun getSelectableTimeSlots(): List<TimeSlot>{
        return listOf(
            TimeSlot(today, "Morning", true),
            TimeSlot(today, "Afternoon", false),
            TimeSlot(today, "Evening", true)
        )
    }
}