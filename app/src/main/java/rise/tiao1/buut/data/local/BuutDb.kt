package rise.tiao1.buut.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import rise.tiao1.buut.data.local.user.LocalUser
import rise.tiao1.buut.data.local.user.UserDao

/**
 * Room Database class for the application.
 * Defines the entities and version of the database, and provides access to the DAO.
 */
@Database(
    entities = [LocalUser::class],
    version = 9,
    exportSchema = false
)

abstract class BuutDb : RoomDatabase() {
    /**
     * Provides access to the Data Access Object (DAO) for database operations.
     */
    abstract val dao: UserDao
}