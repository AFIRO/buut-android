package rise.tiao1.buut.presentation.profile

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import rise.tiao1.buut.domain.user.Address
import rise.tiao1.buut.domain.user.User
import rise.tiao1.buut.domain.user.useCases.GetUserUseCase
import rise.tiao1.buut.domain.user.useCases.LogoutUseCase
import rise.tiao1.buut.presentation.profile.detailProfile.ProfileScreenState
import rise.tiao1.buut.presentation.profile.detailProfile.ProfileViewModel
import rise.tiao1.buut.utils.StreetType
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class ProfileViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope()
    private val getUser: GetUserUseCase = mockk()
    private val logout: LogoutUseCase = mockk()
    private val viewModel = ProfileViewModel(getUser, logout, dispatcher)
    private val testError = "TestError"

    @Test
    fun initialState_isProduced() = scope.runTest {
        val initialState = viewModel.state.value
        assert(initialState == ProfileScreenState())
    }

    @Test
    fun getCurrentUser_getUserFromUseCase_updatesState() = scope.runTest {
        coEvery { getUser.invoke() } returns getUser()
        viewModel.getCurrentUser()
        dispatcher.scheduler.advanceUntilIdle()
        coVerify { getUser.invoke() }
        assertEquals(viewModel.state.value.user, getUser())
        assertEquals(viewModel.state.value.isLoading, false)
    }

    @Test
    fun getCurrentUser_getUserFromUseCaseFails_updatesState() = scope.runTest {
        coEvery { getUser.invoke() } throws Exception(testError)
        viewModel.getCurrentUser()
        dispatcher.scheduler.advanceUntilIdle()
        coVerify { getUser.invoke() }
        assertEquals(viewModel.state.value.apiError, testError)
        assertEquals(viewModel.state.value.isLoading, false)
    }

    @Test
    fun onNetworkChanged_updatesState() = scope.runTest {
        viewModel.onNetworkStatusChange(false)
        assertEquals(viewModel.state.value.isNetworkAvailable, false)
    }

    fun getUser(): User {
        return User(
            id = "TestId",
            firstName = "TestFirstName",
            lastName = "TestLastName",
            email = "Test@Test.be",
            password = "TestPassword",
            phone = "TestPhoneNumber",
            dateOfBirth = LocalDateTime.of(1996, 8, 19, 0, 0),
            address = Address(StreetType.AFRIKALAAN, "TestHouseNumber", "TestBox"),
            roles = listOf()
        )

    }
}