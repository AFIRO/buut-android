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
class ValidateFirstNameTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun firstNameIsBlank_returnsCorrectError() = scope.runTest {
        val validateFirstName = ValidateFirstName()
        val result = validateFirstName.execute("")
        assert(result != null)
        assertEquals(
            UiText.StringResource(resId = R.string.first_name_is_blank_error).getStringId(),
            result?.getStringId()
        )
    }

    @Test
    fun firstNameIsNotBlank_returnsNull() = scope.runTest {
        val validateFirstName = ValidateFirstName()
        val result = validateFirstName.execute("SkibidiBuutMan")
        assert(result == null)
    }
}