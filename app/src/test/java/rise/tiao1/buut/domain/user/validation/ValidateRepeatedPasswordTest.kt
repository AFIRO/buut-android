package rise.tiao1.buut.domain.user.validation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi

class ValidateRepeatedPasswordTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun passwordsDontMatch_returnsCorrectError() = scope.runTest {
        val validateRepeatedPassword = ValidateRepeatedPassword()
        val result = validateRepeatedPassword.execute("password1!", "password2!")
        assert(result != null)
    }

    @Test
    fun passwordsMatch_returnsNull() = scope.runTest {
        val validateRepeatedPassword = ValidateRepeatedPassword()
        val result = validateRepeatedPassword.execute("password1!", "password1!")
        assert(result == null)
    }
}