package rise.tiao1.buut.domain.user

import rise.tiao1.buut.data.remote.user.dto.RoleDTO

data class Role(
    val name: String // Represents the role name (e.g., "Admin")
)

fun Role.toRoleDTO() : RoleDTO {
    return RoleDTO(
        name = this.name
    )
}

