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
class ValidateLastNameTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun lastNameIsBlank_returnsCorrectError() = scope.runTest {
        val validateLastName = ValidateLastName()
        val result = validateLastName.execute("")
        assert(result != null)
        assertEquals(
            UiText.StringResource(resId = R.string.last_name_is_blank_error).getStringId(),
            result?.getStringId()
        )

    }

    @Test
    fun lastNameIsNotBlank_returnsNull() = scope.runTest {
        val validateLastName = ValidateLastName()
        val result = validateLastName.execute("SkibidiBuutMan")
        assert(result == null)
    }
}