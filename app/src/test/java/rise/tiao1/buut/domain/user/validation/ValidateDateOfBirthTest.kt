package rise.tiao1.buut.domain.user.validation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import rise.tiao1.buut.R
import rise.tiao1.buut.utils.UiText
import rise.tiao1.buut.utils.toTestDateString
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class ValidateDateOfBirthTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun dateOfBirth_overMinimum18_returnNull() = scope.runTest {
        val validateDateOfBirth = ValidateDateOfBirth()
        val result =
            validateDateOfBirth.execute(LocalDateTime.now().minusYears(18).toTestDateString())
        assert(result == null)

    }

    @Test
    fun dateOfBirth_empty_returnError() = scope.runTest {
        val validateDateOfBirth = ValidateDateOfBirth()
        val result = validateDateOfBirth.execute("")
        assert(result != null)
        assert(
            result?.getStringId() == UiText.StringResource(R.string.minimum_age_error).getStringId()
        )


    }

    @Test
    fun dateOfBirth_UnderMinimum18_returnError() = scope.runTest {
        val validateDateOfBirth = ValidateDateOfBirth()
        val result =
            validateDateOfBirth.execute(LocalDateTime.now().minusYears(17).toTestDateString())
        assert(result != null)
        assert(
            result?.getStringId() == UiText.StringResource(R.string.minimum_age_error).getStringId()
        )
    }

}