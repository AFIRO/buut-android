package rise.tiao1.buut.utils

/**
 * Object containing navigation keys and constants used for navigating between screens.
 */
object NavigationKeys {

    /**
     * Inner object containing constants representing route names used in navigation.
     */
    object Route {
        /**
         * Route for the authentication screen.
         */
        const val AUTH = "auth"

        /**
         * Route for the login screen.
         */
        const val LOGIN = "login"

        /**
         * Route for the home screen.
         */
        const val HOME = "home"

        /**
         * Route for the registration screen.
         */
        const val REGISTER = "register"

        /**
         * Route for the create booking screen.
         */

        const val CREATE_BOOKING = "create_booking"

        /**
         * Route for the update booking screen.
         */

        const val UPDATE_BOOKING = "update_booking/{bookingId}"


        /**
         * Route for the profile screen.
         */

        const val PROFILE = "profile"

        /**
         * Route for the edit profile screen.
         */

        const val EDIT_PROFILE = "edit_profile"
    }
}
