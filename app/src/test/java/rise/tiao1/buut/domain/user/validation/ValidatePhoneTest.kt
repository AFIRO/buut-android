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
class ValidatePhoneTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun phoneIsBlank_returnsCorrectError() = scope.runTest {
        val validatePhone = ValidatePhone()
        val result = validatePhone.execute("")
        assert(result != null)
        assertEquals(
            UiText.StringResource(resId = R.string.invalid_phone_error).getStringId(),
            result?.getStringId()
        )
    }

    @Test
    fun phoneIsInvalid_returnsCorrectError() = scope.runTest {
        val validatePhone = ValidatePhone()
        val result = validatePhone.execute("1234") // too short, should return an error
        assert(result != null)
        assertEquals(
            UiText.StringResource(resId = R.string.invalid_phone_error).getStringId(),
            result?.getStringId()
        )
    }

    @Test
    fun phoneIsValidLandline_returnsNull() = scope.runTest {
        val validatePhone = ValidatePhone()
        val result = validatePhone.execute("021234567") // Valid Belgian landline
        assert(result == null)
    }

    @Test
    fun phoneIsValidMobileWithoutCountryCode_returnsNull() = scope.runTest {
        val validatePhone = ValidatePhone()
        val result =
            validatePhone.execute("0471234567") // Valid Belgian mobile without country code
        assert(result == null)
    }

    @Test
    fun phoneIsInValidMobileWithoutCountryCode_toLong_returnsError() = scope.runTest {
        val validatePhone = ValidatePhone()
        val result =
            validatePhone.execute("04712345670") // Valid Belgian mobile without country code
        assert(result != null)
        assertEquals(
            UiText.StringResource(resId = R.string.invalid_phone_error).getStringId(),
            result?.getStringId()
        )
    }

    @Test
    fun phoneIsInValidMobileWithoutCountryCode_doesNotStartWithZero_returnsError() = scope.runTest {
        val validatePhone = ValidatePhone()
        val result =
            validatePhone.execute("1471234567") // Valid Belgian mobile without country code
        assert(result != null)
        assertEquals(
            UiText.StringResource(resId = R.string.invalid_phone_error).getStringId(),
            result?.getStringId()
        )
    }

    @Test
    fun phoneIsInValidMobileWithoutCountryCode_secondDigitNot4_returnsError() = scope.runTest {
        val validatePhone = ValidatePhone()
        val result =
            validatePhone.execute("0171234567") // Valid Belgian mobile without country code
        assert(result != null)
        assertEquals(
            UiText.StringResource(resId = R.string.invalid_phone_error).getStringId(),
            result?.getStringId()
        )
    }


    @Test
    fun phoneIsValidMobileWithCountryCode_returnsNull() = scope.runTest {
        val validatePhone = ValidatePhone()
        val result = validatePhone.execute("+32471234567") // Valid Belgian mobile with country code
        assert(result == null)
    }

    @Test
    fun phoneIsInValidMobileWithCountryCode_toLong_returnsError() = scope.runTest {
        val validatePhone = ValidatePhone()
        val result =
            validatePhone.execute("+324712345670") // Valid Belgian mobile without country code
        assert(result != null)
        assertEquals(
            UiText.StringResource(resId = R.string.invalid_phone_error).getStringId(),
            result?.getStringId()
        )
    }

    @Test
    fun phoneIsInValidMobileWithCountryCode_toShort_returnsError() = scope.runTest {
        val validatePhone = ValidatePhone()
        val result =
            validatePhone.execute("+3247123456") // Valid Belgian mobile without country code
        assert(result != null)
        assertEquals(
            UiText.StringResource(resId = R.string.invalid_phone_error).getStringId(),
            result?.getStringId()
        )
    }

    @Test
    fun phoneIsInValidMobileWithCountryCode_doesNotStartWithPlus_returnsError() = scope.runTest {
        val validatePhone = ValidatePhone()
        val result =
            validatePhone.execute("-32471234567") // Valid Belgian mobile without country code
        assert(result != null)
        assertEquals(
            UiText.StringResource(resId = R.string.invalid_phone_error).getStringId(),
            result?.getStringId()
        )
    }

    @Test
    fun phoneIsInValidMobileWithCountryCode_countryCodeNotBelgium_returnsError() = scope.runTest {
        val validatePhone = ValidatePhone()
        val result =
            validatePhone.execute("+33171234567") // Valid Belgian mobile without country code
        assert(result != null)
        assertEquals(
            UiText.StringResource(resId = R.string.invalid_phone_error).getStringId(),
            result?.getStringId()
        )
    }

    @Test
    fun phoneIsValidMobileWithDashAndSpace_returnsCorrectError() = scope.runTest {
        val validatePhone = ValidatePhone()
        val result =
            validatePhone.execute("+32 4 712 34 567") // Valid Belgian mobile with country code and spaces
        assert(result != null)
        assertEquals(
            UiText.StringResource(resId = R.string.invalid_phone_error).getStringId(),
            result?.getStringId()
        )
    }

    @Test
    fun phoneIsValidMobileWithCountryCodeAndDash_returnsCorrectError() = scope.runTest {
        val validatePhone = ValidatePhone()
        val result =
            validatePhone.execute("+32-4-712-34-567") // Valid Belgian mobile with country code and dashes
        assert(result != null)
        assertEquals(
            UiText.StringResource(resId = R.string.invalid_phone_error).getStringId(),
            result?.getStringId()
        )
    }

    @Test
    fun phoneIsInvalidLandlineWithInvalidDigits_returnsCorrectError() = scope.runTest {
        val validatePhone = ValidatePhone()
        val result = validatePhone.execute("02123456") // Invalid landline (too short)
        assert(result != null)
        assertEquals(
            UiText.StringResource(resId = R.string.invalid_phone_error).getStringId(),
            result?.getStringId()
        )
    }

    @Test
    fun phoneIsInvalidLandline_doesNotStartWith0_returnsCorrectError() = scope.runTest {
        val validatePhone = ValidatePhone()
        val result = validatePhone.execute("121234567") // Invalid landline (too short)
        assert(result != null)
        assertEquals(
            UiText.StringResource(resId = R.string.invalid_phone_error).getStringId(),
            result?.getStringId()
        )
    }

    @Test
    fun phoneIsInvalidMobileWithExtraDigits_returnsCorrectError() = scope.runTest {
        val validatePhone = ValidatePhone()
        val result = validatePhone.execute("04712345678") // Invalid mobile (too long)
        assert(result != null)
        assertEquals(
            UiText.StringResource(resId = R.string.invalid_phone_error).getStringId(),
            result?.getStringId()
        )
    }

    @Test
    fun phoneIsValidLandlineWithDash_returnsCorrectError() = scope.runTest {
        val validatePhone = ValidatePhone()
        val result = validatePhone.execute("02-123-45-67") // Valid Belgian landline with dashes
        assert(result != null)
        assertEquals(
            UiText.StringResource(resId = R.string.invalid_phone_error).getStringId(),
            result?.getStringId()
        )
    }

    @Test
    fun phoneIsInvalidWithIncorrectCountryCode_returnsCorrectError() = scope.runTest {
        val validatePhone = ValidatePhone()
        val result = validatePhone.execute("+33471234567") // Invalid country code (not Belgium)
        assert(result != null)
        assertEquals(
            UiText.StringResource(resId = R.string.invalid_phone_error).getStringId(),
            result?.getStringId()
        )
    }
}


