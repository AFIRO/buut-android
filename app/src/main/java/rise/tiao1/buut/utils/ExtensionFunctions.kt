package rise.tiao1.buut.utils

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import retrofit2.HttpException
import rise.tiao1.buut.data.local.user.LocalUser
import rise.tiao1.buut.data.remote.user.RemoteUser
import rise.tiao1.buut.domain.user.Address
import rise.tiao1.buut.domain.user.Role
import rise.tiao1.buut.domain.user.User
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.TextStyle
import java.util.Locale


fun RemoteUser.toLocalUser(): LocalUser {
    return LocalUser(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
        address = Address(this.address.street, this.address.houseNumber, this.address.box),
        phone = this.phoneNumber,
        dateOfBirth = this.birthDate,
        roles = this.roles.map { Role(name = it.name) }.toJson()
    )
}



fun LocalUser.toUser(): User {
    return User(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
        address = this.address,
        phone = this.phone,
        dateOfBirth = this.dateOfBirth?.toLocalDateTimeFromApiString(),
        roles = this.roles.toRoleList()
    )
}

fun String.toDate(): LocalDate? {
    val formatters = listOf(
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("d/M/yyyy")
    )
    for (formatter in formatters) {
        try {
            return LocalDate.parse(this, formatter)
        } catch (e: DateTimeParseException) {
            // Ignore and try the next formatter
        }
    }
    return null
}

fun LocalDateTime.toTestDateString(): String {
    val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
    return this.format(formatter)
}

fun String.toApiDateString(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return this.toDate()?.format(formatter) ?: ""
}

fun LocalDateTime.toApiDateString(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    return this.format(formatter)
}

fun LocalDateTime.toMillis(): Long{
    return this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun Long.toDateString(): String {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault()).toDateString()
}

fun LocalDateTime.toDateString(): String {
    val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
    val dayOfWeek = this.dayOfWeek
    val formattedDayOfWeek = dayOfWeek
        .getDisplayName(TextStyle.FULL, Locale.getDefault())
        .lowercase()
        .replaceFirstChar { it.uppercase() }
    return "$formattedDayOfWeek  ${formatter.format(this)}"
}

fun LocalDateTime.toTimeString():String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return this.format(formatter)
}

fun String.toLocalDateTime(): LocalDateTime? {
    val formatters = listOf(
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("d/M/yyyy")
    )
    for (formatter in formatters) {
        try {
            return LocalDateTime.parse(this, formatter)
        } catch (e: DateTimeParseException) {
            // Ignore and try the next formatter
        }
    }
    return null
}

fun String.toLocalDateTimeFromApiString(): LocalDateTime {
    val date = if (this.length < 19) this.padEnd(19, '0') else this.substring(0, 19)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    return LocalDateTime.parse(date, formatter)
}

fun HttpException.toApiErrorMessage(): String {
    val apiError = this.response()?.errorBody()?.string()
    return apiError ?: "Unkown error"
}

fun List<Role>.toJson(): String {
    return Gson().toJson(this)
}

fun String.toRoleList(): List<Role> {
    if (this.isBlank()) {
        return emptyList()
    }
    // Deserialize the JSON into a List of Role objects
    val roleList: List<Role> = Gson().fromJson(this, object : TypeToken<List<Role>>() {}.type)

    return roleList
}

