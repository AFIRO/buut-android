package rise.tiao1.buut.domain.user.validation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import rise.tiao1.buut.R
import rise.tiao1.buut.utils.UiText

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class ValidateEmailTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun emailIsBlank_returnsCorrectError() = scope.runTest {
        val validateEmail = ValidateEmail()
        val result = validateEmail.execute("")
        assert(result != null)
        assertEquals(
            UiText.StringResource(resId = R.string.email_is_blank_error).getStringId(),
            result?.getStringId()
        )
    }

    @Test
    fun emailIsInvalid_returnsCorrectError() = scope.runTest {
        val validateEmail = ValidateEmail()
        val result = validateEmail.execute("invalid-email")
        assert(result != null)
        assertEquals(
            UiText.StringResource(resId = R.string.email_not_valid_error).getStringId(),
            result?.getStringId()
        )

    }

    @Test
    fun emailIsValid_returnsNull() = scope.runTest {
        val validateEmail = ValidateEmail()
        val result = validateEmail.execute("tester@buut.be")
        assert(result == null)
    }
}