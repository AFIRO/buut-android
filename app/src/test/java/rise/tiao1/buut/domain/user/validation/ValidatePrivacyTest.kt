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
class ValidatePrivacyTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun privacyIsAccepted_returnsNull() = scope.runTest {
        val validatePrivacy = ValidatePrivacy()
        val result = validatePrivacy.execute(true)
        assert(result == null)
    }

    @Test
    fun privacyIsNotAccepted_returnsCorrectError() = scope.runTest {
        val validatePrivacy = ValidatePrivacy()
        val result = validatePrivacy.execute(false)
        assert(result != null)
        assertEquals(
            UiText.StringResource(resId = R.string.privacy_not_accepted_error).getStringId(),
            result?.getStringId()
        )
    }

}