package rise.tiao1.buut.domain.user.validation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import rise.tiao1.buut.R
import rise.tiao1.buut.utils.StreetType
import rise.tiao1.buut.utils.UiText

@ExperimentalCoroutinesApi
class ValidateStreetTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun streetIsBlank_returnsCorrectError() = scope.runTest {
        val validateStreet = ValidateStreet()
        val result = validateStreet.execute("")
        assert(result != null)
        assertEquals(
            UiText.StringResource(resId = R.string.street_is_blank_error).getStringId(),
            result?.getStringId()
        )
    }

    @Test
    fun streetIsInvalid_returnsCorrectError() = scope.runTest {
        val validateStreet = ValidateStreet()
        val result = validateStreet.execute("Buutstraat")
        assert(result != null)
        assertEquals(
            UiText.StringResource(resId = R.string.invalid_street).getStringId(),
            result?.getStringId()
        )
    }

    @Test
    fun streetIsValid_returnsNull() = scope.runTest {
        val validateStreet = ValidateStreet()
        for (street in StreetType.entries) {
            val result = validateStreet.execute(street.streetName)
            assert(result == null)
        }
    }
}