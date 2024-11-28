package rise.tiao1.buut.presentation.profile.detailProfile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import rise.tiao1.buut.data.di.MainDispatcher
import rise.tiao1.buut.domain.user.useCases.GetUserUseCase
import javax.inject.Inject
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rise.tiao1.buut.domain.user.useCases.LogoutUseCase

@HiltViewModel
class ProfileViewModel  @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    @MainDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _state = mutableStateOf(ProfileScreenState())
    val state: State<ProfileScreenState>
        get() = _state


    init {
        getCurrentUser()
    }


    fun getCurrentUser() {
        _state.value = state.value.copy(isLoading = true)
        viewModelScope.launch(dispatcher) {
            try {
                val user = getUserUseCase()
                _state.value = state.value.copy(isLoading = false, user = user)
            } catch (e: Exception) {
                _state.value = state.value.copy(
                    isLoading = false,
                    apiError = e.message
                )
            }
        }
    }

    fun onLogoutClicked(navigateToLogin: () -> Unit) {
        viewModelScope.launch(dispatcher) {
            logoutUseCase(state.value.user)
            navigateToLogin()
        }

    }

    fun onEditClicked(navigateToEdit: () -> Unit){
        navigateToEdit()
    }
}