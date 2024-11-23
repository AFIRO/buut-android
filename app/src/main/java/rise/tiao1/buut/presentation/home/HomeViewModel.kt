package rise.tiao1.buut.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import rise.tiao1.buut.data.di.MainDispatcher
import rise.tiao1.buut.domain.booking.useCases.GetBookingsSortedByDateUseCase
import rise.tiao1.buut.domain.notification.useCases.GetNotificationsUseCase
import rise.tiao1.buut.domain.notification.useCases.ToggleNotificationReadStatusUseCase
import rise.tiao1.buut.domain.user.useCases.GetUserUseCase
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getBookingsSortedByDateUseCase: GetBookingsSortedByDateUseCase,
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val toggleNotificationUseCase: ToggleNotificationReadStatusUseCase,
    @MainDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _state = mutableStateOf(HomeScreenState())
    val state: State<HomeScreenState> get() = _state

    init {
        initializeState()
    }


    private fun initializeState() {
        _state.value = state.value.copy(isLoading = true)
        viewModelScope.launch(dispatcher) {
            try {
                val user = getUserUseCase()
                _state.value = state.value.copy(user = user)
                getBookings()
                getNotifications()
                _state.value = state.value.copy(isLoading = false)
            } catch (e: Exception) {
                _state.value = state.value.copy(
                    isLoading = false,
                    apiError = e.message
                )
            }
        }
    }

    private fun getBookings() {
        viewModelScope.launch(dispatcher) {
            val bookings = getBookingsSortedByDateUseCase(
                userId = state.value.user?.id ?: ""
            )
            _state.value = state.value.copy(bookings = bookings)
        }

    }

    private fun getNotifications() {
        viewModelScope.launch(dispatcher) {
            val notifications = getNotificationsUseCase(
                userId = state.value.user?.id ?: ""
            )
            _state.value = state.value.copy(notifications = notifications, unReadNotifications = notifications.count { !it.isRead })
        }
    }

    fun onNotificationClick(notificationId: String, currentStatus: Boolean) {
        viewModelScope.launch(dispatcher) {
            toggleNotificationUseCase(notificationId, currentStatus)
            getNotifications()
        }
    }
}


