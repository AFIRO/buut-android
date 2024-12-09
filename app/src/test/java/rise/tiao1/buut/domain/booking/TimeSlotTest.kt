package rise.tiao1.buut.domain.booking

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import rise.tiao1.buut.data.remote.booking.TimeSlotDTO
import rise.tiao1.buut.utils.toApiDateString
import rise.tiao1.buut.utils.toLocalDateTimeFromApiString
import java.time.LocalDateTime

class TimeSlotTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)
    private val today = LocalDateTime.now()

    @Test
    fun timeSlotDTOToTimeSlot_returnsCorrectTimeSlot() = scope.runTest {
        val receivedTimeSlotFromApi = getTimeSlotDTO()
        val expected = getTimeSlot()
        val result = receivedTimeSlotFromApi.toTimeSlot()
        assertEquals(result, expected)
    }


    private fun getTimeSlotDTO(): TimeSlotDTO {
        return TimeSlotDTO(
            date = today.toApiDateString(),
            slot = "Morning",
            available = true
        )
    }

    private fun getTimeSlot(): TimeSlot {
        return TimeSlot(
            date = today.toApiDateString().toLocalDateTimeFromApiString(),
            slot = "Morning",
            available = true
        )
    }
}