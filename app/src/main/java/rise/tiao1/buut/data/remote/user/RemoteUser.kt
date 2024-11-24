package rise.tiao1.buut.data.remote.user

import com.google.type.DateTime
import rise.tiao1.buut.data.remote.user.dto.AddressDTO
import rise.tiao1.buut.data.remote.user.dto.RoleDTO
import rise.tiao1.buut.domain.user.validation.ValidateDateOfBirth
import java.time.LocalDate
import java.time.LocalDateTime

data class RemoteUser(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val birthDate: String,
    val address: AddressDTO,
    val roles: List<RoleDTO>,
)