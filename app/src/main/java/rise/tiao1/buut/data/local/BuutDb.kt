package rise.tiao1.buut.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import rise.tiao1.buut.data.local.booking.BookingDao
import rise.tiao1.buut.data.local.booking.LocalBooking
import rise.tiao1.buut.data.local.notification.LocalNotification
import rise.tiao1.buut.data.local.notification.NotificationDao
import rise.tiao1.buut.data.local.user.LocalUser
import rise.tiao1.buut.data.local.user.UserDao

/**
 * Room Database class for the application.
 * Defines the entities and version of the database, and provides access to the DAO.
 */
@Database(
    entities = [LocalUser::class, LocalBooking::class, LocalNotification::class],
    version = 19,
    exportSchema = false
)

abstract class BuutDb : RoomDatabase() {
    /**
     * Provides access to the Data Access Object (DAO) for database operations.
     */
    abstract val userDao: UserDao
    abstract val bookingDao: BookingDao
    abstract val notificationDao: NotificationDao
}
