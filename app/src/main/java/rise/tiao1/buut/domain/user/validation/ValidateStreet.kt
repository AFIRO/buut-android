package rise.tiao1.buut.domain.user.validation

import android.util.Log
import rise.tiao1.buut.R
import rise.tiao1.buut.utils.StreetType
import rise.tiao1.buut.utils.UiText
import java.util.Locale
import javax.inject.Inject

class ValidateStreet @Inject constructor() {
    fun execute(street: String): UiText? {
        val formattedStreet = street
            .lowercase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

        if (formattedStreet.isBlank())
            return UiText.StringResource(resId = R.string.street_is_blank_error)

        if (!StreetType.entries.map { it.streetName }.contains(formattedStreet)) {
            return UiText.StringResource(resId = R.string.invalid_street)
        }

        return null
    }
}
