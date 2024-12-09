package rise.tiao1.buut.domain.user.useCases

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import rise.tiao1.buut.data.repositories.UserRepository
import rise.tiao1.buut.domain.user.Address
import rise.tiao1.buut.domain.user.User
import rise.tiao1.buut.utils.StreetType
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class UpdateUserUseCaseTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    var repository: UserRepository = mockk()
    var updateUserUseCase = UpdateUserUseCase(repository)

    @Test
    fun updateUser_returnsSucces() = scope.runTest {
        coEvery { repository.updateUser(any()) } returns Unit
        val user = getUser()
        var result = false
        updateUserUseCase(user, onSuccess = { result = true }, onError = { result = false })
        assert(result)

    }

    @Test
    fun updateUser_repoGivesExcetion_returnsSucces() = scope.runTest {
        coEvery { repository.updateUser(any()) } throws Exception("Registration Failed")
        val user = getUser()
        var result = false
        updateUserUseCase(user, onSuccess = { result = false }, onError = { result = true })
        assert(result)

    }


    fun getUser(): User {
        return User(
            id = "TestId",
            firstName = "TestFirstName",
            lastName = "TestLastName",
            email = "TestEmail",
            password = "TestPassword",
            phone = "TestPhone",
            dateOfBirth = LocalDateTime.of(1996, 8, 19, 0, 0),
            address = Address(StreetType.AFRIKALAAN, "TestHouseNumber", "TestBox"),
            roles = listOf()
        )
    }
}