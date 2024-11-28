package rise.tiao1.buut.presentation.profile.editProfile

import rise.tiao1.buut.domain.user.User
import rise.tiao1.buut.utils.UiText

data class EditProfileScreenState (
    val user: User? = null,
    val firstName: String = "",
    val lastName: String = "",
    val street: String = "",
    val houseNumber: String = "",
    val box: String = "",
    val phone: String = "",
    val email: String = "",
    val dateOfBirth: String = "",

    val firstNameError: UiText? = null,
    val lastNameError: UiText? = null,
    val streetError: UiText? = null,
    val houseNumberError: UiText? = null,
    val boxError: UiText? = null,
    val phoneError: UiText? = null,
    val emailError: UiText? = null,
    val dateOfBirthError: UiText? = null,
    val isLoading: Boolean = true,
    val formHasErrors: Boolean= false,
    val updateSuccess: Boolean = false,

    val apiError: String? = ""
)

