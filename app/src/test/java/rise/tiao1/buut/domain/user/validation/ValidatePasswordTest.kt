package rise.tiao1.buut.domain.user.validation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import rise.tiao1.buut.R
import rise.tiao1.buut.utils.UiText

@ExperimentalCoroutinesApi
class ValidatePasswordTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun passwordIsBlank_returnsCorrectError() = scope.runTest {
        val validatePassword = ValidatePassword()
        val result = validatePassword.execute("")
        assert(result != null)
        assertEquals(
            UiText.StringResource(
                resId = R.string.invalid_password_length_error,
                PASSWORD_LENGTH
            ).getStringId(), result?.getStringId()
        )

    }

    @Test
    fun passwordIsTooShort_returnsCorrectError() = scope.runTest {
        val validatePassword = ValidatePassword()
        val result = validatePassword.execute("Passr!1")
        assert(result != null)
        assertEquals(
            UiText.StringResource(
                resId = R.string.invalid_password_length_error,
                PASSWORD_LENGTH
            ).getStringId(), result?.getStringId()
        )
    }

    @Test
    fun passwordContainsNoDigit_returnsCorrectError() = scope.runTest {
        val validatePassword = ValidatePassword()
        val result = validatePassword.execute("Password!")
        assert(result != null)
        assertEquals(
            UiText.StringResource(resId = R.string.invalid_password_no_digit_error).getStringId(),
            result?.getStringId()
        )
    }

    @Test
    fun passwordContainsNoUpperCase_returnsCorrectError() = scope.runTest {
        val validatePassword = ValidatePassword()
        val result = validatePassword.execute("password1!")
        assert(result != null)
        assertEquals(
            UiText.StringResource(resId = R.string.invalid_password_no_upper_case_error)
                .getStringId(), result?.getStringId()
        )

    }

    @Test
    fun passwordContainsNoSpecialCharacter_returnsCorrectError() = scope.runTest {
        val validatePassword = ValidatePassword()
        val result = validatePassword.execute("Password1")
        assert(result != null)
        assertEquals(
            UiText.StringResource(resId = R.string.invalid_password_no_special_character_error)
                .getStringId(), result?.getStringId()
        )
    }

    @Test
    fun passwordIsValid_returnsNull() = scope.runTest {
        val validatePassword = ValidatePassword()
        val result = validatePassword.execute("Password1!")
        assert(result == null)
    }

}