package rise.tiao1.buut.data.remote.user.dto


import com.google.gson.annotations.SerializedName


data class PutUserDTO(
    val id: String,
    @SerializedName("FirstName")
    val firstName: String,
    @SerializedName("LastName")
    val lastName: String,
    @SerializedName("Address")
    val address: AddressDTO?,
    @SerializedName("PhoneNumber")
    val phone: String?,
    @SerializedName("Email")
    val email: String,
    @SerializedName("Password")
    val password: String?,
    @SerializedName("BirthDate")
    val dateOfBirth: String?,
    @SerializedName("Roles")
    val roles: List<RoleDTO>,
)

