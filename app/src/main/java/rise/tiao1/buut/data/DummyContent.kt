package rise.tiao1.buut.data

import rise.tiao1.buut.data.remote.booking.BatteryDTO
import rise.tiao1.buut.data.remote.booking.BoatDTO
import rise.tiao1.buut.data.remote.booking.BookingDTO
import rise.tiao1.buut.data.remote.booking.TimeSlotDTO
import rise.tiao1.buut.data.remote.user.RemoteUser
import rise.tiao1.buut.data.remote.user.dto.AddressDTO
import rise.tiao1.buut.data.remote.user.dto.RoleDTO
import rise.tiao1.buut.domain.user.Address
import rise.tiao1.buut.domain.user.Role
import rise.tiao1.buut.utils.StreetType
import rise.tiao1.buut.utils.toApiDateString
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val USER_ID_WITH_BOOKINGS = "auth0|6713adbf2d2a7c11375ac64c"

object DummyContent {
    fun getDummyUsers() = arrayListOf(
        RemoteUser(
            "auth0|6713adbf2d2a7c11375ac64c",
            "TestVoornaam1",
            "TestAchternaam1",
            "TestEmail1@hogent.be",
            phoneNumber = "Testphone",
            birthDate = LocalDateTime.of(1996, 8, 19, 0, 0).toString(),
            address = AddressDTO(StreetType.AFRIKALAAN, "TestHouseNumber", "TestBox"),
            roles = listOf(RoleDTO("admin"))
        ),
        RemoteUser(
            "auth0|6713ad614fda04f4b9ae2156",
            "TestVoornaam2",
            "TestAchternaam2",
            "TestEmail2@hogent.be",
            phoneNumber = "Testphone",
            birthDate = LocalDateTime.of(1996, 8, 19, 0, 0).toString(),
            address = AddressDTO(StreetType.AFRIKALAAN, "TestHouseNumber", "TestBox"),
            roles = listOf(RoleDTO("admin"))
        ),
        RemoteUser(
            "auth0|6713ad524e8a8907fbf0d57f",
            "TestVoornaam3",
            "TestAchternaam3",
            "TestEmail3@hogent.be",
            phoneNumber = "Testphone",
            birthDate = LocalDateTime.of(1996, 8, 19, 0, 0).toString(),
            address = AddressDTO(StreetType.AFRIKALAAN, "TestHouseNumber", "TestBox"),
            roles = listOf(RoleDTO("admin"))
        )
    )

    fun getDummyBookings(userId: String? = null): List<BookingDTO> {
        var count = 0
        val bookings = ArrayList<BookingDTO>().apply {
            repeat(30) {
                add(
                    BookingDTO(
                        id = "id${++count}",
                        date = getRandomDate().toApiDateString(),
                        boat = BoatDTO(name = "boot${it + 1}"),
                        battery = BatteryDTO(name = "batterij${it + 1}")
                    )
                )
            }
            add(
                BookingDTO(
                    id = "id${++count}",
                    date = getRandomDate().toApiDateString(),
                    boat = BoatDTO(name = "boot${55}"),
                    battery = BatteryDTO(name = "batterij${55}"),
                )
            )
        }
        if (userId == null)
            return emptyList()
        else if (userId == USER_ID_WITH_BOOKINGS) {
            return bookings
        }
        else {
            return bookings.filter { it.userId == userId }
        }

    }

    private fun getRandomDate(): LocalDateTime {
        val start = LocalDateTime.now().plusDays(2)
        val end = LocalDateTime.of(2024, 12, 1, 0, 0)
        val randomDays = (0..(end.dayOfYear - start.dayOfYear)).random()
        return start.plusDays(randomDays.toLong())
    }

    fun getDummyFreeTimeSlots(startDate: String, endDate: String) : List<TimeSlotDTO> {
        val start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE).atStartOfDay()
        val end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE).atStartOfDay()
        var result = mutableListOf<TimeSlotDTO>()

        var current = start
        while (current.isBefore(end) || current.isEqual(end)) {
            result.add(TimeSlotDTO(
                date = current.toApiDateString(),
                slot = "Morning",
                available = true
            ))
            current = current.plusDays(1)
        }
        return result
    }


}