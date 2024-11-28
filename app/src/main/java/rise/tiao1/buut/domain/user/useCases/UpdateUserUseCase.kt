package rise.tiao1.buut.domain.user.useCases

import android.content.SharedPreferences
import com.auth0.android.jwt.JWT
import rise.tiao1.buut.data.repositories.UserRepository
import rise.tiao1.buut.domain.user.User
import rise.tiao1.buut.domain.user.toPutUserDTO
import rise.tiao1.buut.domain.user.toUserDTO
import rise.tiao1.buut.utils.SharedPreferencesKeys
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {

    suspend operator fun invoke(
    user : User,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
    ) {
        try {
            userRepository.updateUser(user.toPutUserDTO())
            onSuccess()
        } catch (e: Exception) {
            onError("Error updating user: ${e.message}")
        }

    }
}