package rise.tiao1.buut.domain.user.validation

import android.util.Log
import rise.tiao1.buut.R
import rise.tiao1.buut.utils.UiText
import javax.inject.Inject

const val MOBILE_REGEX = "^04\\d{8}$"

const val MOBILE_COUNTRY_CODE_REGEX = "^\\+324\\d{8}$"

const val LANDLINE_REGEX = "^0[1-9][0-9]{7}$"

class ValidatePhone @Inject constructor(){
    fun execute(phone: String) : UiText? {
        // Check the mobile number without country code
        val mobileValidation = validateMobileNumber(phone)
        if (mobileValidation == null) return null  // Valid mobile without country code

        // Check the mobile number with country code
        val mobileWithCountryCodeValidation = validateMobileNumberWithCountryCode(phone)
        if (mobileWithCountryCodeValidation == null) return null  // Valid mobile with country code

        // Check the landline number
        val landlineValidation = validateLandlineNumber(phone)
        if (landlineValidation == null) return null  // Valid landline number

        // If none of the patterns matched, return error
        return UiText.StringResource(resId = R.string.invalid_phone_error)
    }

    // Validate Mobile Numbers (Without Country Code)
    fun validateMobileNumber(phone: String): UiText? {
        if (!phone.matches(Regex(MOBILE_REGEX))) {
            return UiText.StringResource(resId = R.string.invalid_phone_error)
        }
        return null
    }

    // Validate Mobile Numbers (With Country Code)
    fun validateMobileNumberWithCountryCode(phone: String): UiText? {
        if (!phone.matches(Regex(MOBILE_COUNTRY_CODE_REGEX))) {
            return UiText.StringResource(resId = R.string.invalid_phone_error)
        }
        return null
    }

    // Validate Landline Numbers
    fun validateLandlineNumber(phone: String): UiText? {
        if (!phone.matches(Regex(LANDLINE_REGEX))) {
            return UiText.StringResource(resId = R.string.invalid_phone_error)
        }
        return null
    }
}