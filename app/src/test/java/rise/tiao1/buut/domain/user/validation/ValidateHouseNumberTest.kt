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
class ValidateHouseNumberTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun houseNumberIsBlank_returnsCorrectError() = scope.runTest {
        val validateHouseNumber = ValidateHouseNumber()
        val result = validateHouseNumber.execute("")
        assert(result != null)
        assertEquals(
            UiText.StringResource(
                resId = R.string.invalid_house_number_error,
                LOWEST_POSSIBLE_HOUSE_NUMBER
            ).getStringId(), result?.getStringId()
        )
    }

    @Test
    fun houseNumberIsNegative_returnsCorrectError() = scope.runTest {
        val validateHouseNumber = ValidateHouseNumber()
        val result = validateHouseNumber.execute("-1")
        assert(result != null)
        assertEquals(
            UiText.StringResource(
                resId = R.string.invalid_house_number_error,
                LOWEST_POSSIBLE_HOUSE_NUMBER
            ).getStringId(), result?.getStringId()
        )
    }

    @Test
    fun houseNumberContainsLetters_returnsCorrectError() = scope.runTest {
        val validateHouseNumber = ValidateHouseNumber()
        val result = validateHouseNumber.execute("abc")
        assert(result != null)
        assertEquals(
            UiText.StringResource(
                resId = R.string.invalid_house_number_error,
                LOWEST_POSSIBLE_HOUSE_NUMBER
            ).getStringId(), result?.getStringId()
        )
    }

    @Test
    fun houseNumberIsNotBlank_returnsNull() = scope.runTest {
        val validateHouseNumber = ValidateHouseNumber()
        val result = validateHouseNumber.execute("1")
        assert(result == null)
    }
}