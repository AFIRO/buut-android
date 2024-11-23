package rise.tiao1.buut.utils

enum class NotificationType (val notificationType: String) {
    GENERAL ("General"),
    ALERT ("Alert"),
    REMINDER ("Reminder"),
    BOOKING ("Booking"),
    BOAT ("Boat"),
    BATTERY ("Battery"),
    USERREGISTRATION ("UserRegistration");

    companion object {
        fun fromString(notificationType: String): NotificationType? {
            return NotificationType.entries.find { it.notificationType.equals(notificationType, ignoreCase = true) }
        }
    }

}