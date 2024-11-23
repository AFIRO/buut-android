package rise.tiao1.buut.presentation.home

import rise.tiao1.buut.domain.booking.Booking
import rise.tiao1.buut.domain.user.User


data class HomeScreenState (
    val user: User? = null,
    val bookings: List<Booking> = emptyList(),
    val notifications: List<Any> = emptyList(),
    val isLoading: Boolean = true,
    val apiError: String? = "",
    val unReadNotifications: Int = 0

)