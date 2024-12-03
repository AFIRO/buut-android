package rise.tiao1.buut.data.remote.booking

data class TimeSlotResponse (
    val value: List<TimeSlotDTO>,
    val statusCode: Int
)