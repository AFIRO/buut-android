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
class RegisterUserUseCaseTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    var repository: UserRepository = mockk()

    @Test
    fun registerUser_returnsSuccess(): Unit = scope.runTest {
        coEvery { repository.registerUser(any()) } returns Unit
        val user = getUser()
        val registerUserUseCase = RegisterUserUseCase(repository)
        var result = false
        registerUserUseCase.invoke(
            user,
            onSuccess = { result = true },
            onError = { result = false })
        assert(result)
    }

    @Test
    fun registerUserException_returnsError(): Unit = scope.runTest {
        coEvery { repository.registerUser(any()) } throws Exception("Registration Failed")
        val user = getUser()
        val registerUserUseCase = RegisterUserUseCase(repository)
        var result = false
        registerUserUseCase.invoke(
            user,
            onSuccess = { result = false },
            onError = { result = true })
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