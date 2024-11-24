package rise.tiao1.buut.data.local.user

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.type.DateTime
import rise.tiao1.buut.domain.user.Address
import rise.tiao1.buut.domain.user.Role
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "user")
data class LocalUser(
    @PrimaryKey()
    @ColumnInfo
    val id: String,
    @ColumnInfo
    val firstName: String,
    @ColumnInfo
    val lastName: String,
    @ColumnInfo
    val email: String,
    @ColumnInfo
    val phone: String?,
    @ColumnInfo
    val dateOfBirth: String?,
    @Embedded
    val address: Address?,
    @ColumnInfo
    val roles: String
    )

