package rise.tiao1.buut.data.local.booking

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.type.DateTime
import java.time.LocalDateTime

@Entity(tableName = "booking")
data class LocalBooking (
    @PrimaryKey
    @ColumnInfo
    val id: String,
    @ColumnInfo
    val date: String,
    @ColumnInfo
    val time: String? = null,
    @ColumnInfo
    val boat: String? = null,
    @ColumnInfo
    val battery: String? = null,
    @ColumnInfo
    val batteryUserFirstName: String? = null,
    @ColumnInfo
    val batteryUserLastName: String? = null,
    @ColumnInfo
    val batteryUserEmail: String? = null,
    @ColumnInfo
    val batteryUserPhoneNumber: String? = null,
    @ColumnInfo
    val userId: String? = null
)


