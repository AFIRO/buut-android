package rise.tiao1.buut.domain.user.validation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import rise.tiao1.buut.R
import rise.tiao1.buut.utils.UiText

@ExperimentalCoroutinesApi
class ValidateTermsTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun termsAccepted_returnsNull() = scope.runTest {
        val validateTerms = ValidateTerms()
        val result = validateTerms.execute(true)
        assert(result == null)

    }

    @Test
    fun termsNotAccepted_returnsError() = scope.runTest {
        val validateTerms = ValidateTerms()
        val result = validateTerms.execute(false)
        assert(result != null)
        assert(
            result?.getStringId() == UiText.StringResource(R.string.terms_not_accepted_error)
                .getStringId()
        )
    }

}