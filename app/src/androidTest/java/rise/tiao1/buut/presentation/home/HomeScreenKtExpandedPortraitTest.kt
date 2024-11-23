package rise.tiao1.buut.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavController
import androidx.test.espresso.device.action.ScreenOrientation
import androidx.test.espresso.device.rules.ScreenOrientationRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import rise.tiao1.buut.R
import rise.tiao1.buut.domain.booking.Booking
import rise.tiao1.buut.domain.notification.Notification
import rise.tiao1.buut.domain.user.User
import rise.tiao1.buut.utils.NotificationType
import rise.tiao1.buut.utils.UiLayout
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class HomeScreenKtExpandedPortraitTest {
    val startOrientation = ScreenOrientation.PORTRAIT
    val updatedOrientation = ScreenOrientation.LANDSCAPE
    val uiLayout = UiLayout.PORTRAIT_EXPANDED
    @get:Rule
    val rule = createComposeRule()
    @get:Rule
    val screenOrientationRule: ScreenOrientationRule = ScreenOrientationRule(ScreenOrientation.PORTRAIT)
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    val today = LocalDateTime.now()
    val testError = "testError"
    val loadingIndicator = rule.onNodeWithTag(context.getString(R.string.loading_indicator))
    val errorContainer = rule.onNodeWithText(testError)
    var navControllerState by mutableStateOf<NavController?>(null)
    val notificationTabSelector = rule.onNodeWithTag(context.getString(R.string.notifications_title))
    val bookingTabSelecor = rule.onNodeWithTag(context.getString(R.string.booking_list_title))
    val bookingItem = rule.onNodeWithTag("BookingItem")
    val notificationBadge = rule.onNodeWithTag("notificationBadge",true)
    val notificationBadgeText = rule.onNodeWithTag("notificationBadgeText",true)
    val noNotificationsText = rule.onNodeWithText(context.getString(R.string.user_has_no_notifications))
    val noBookingsText = rule.onNodeWithText(context.getString(R.string.user_has_no_bookings))
    val notificationCard = rule.onNodeWithTag("notificationCard")
    val bookingExpansionButton = rule.onNodeWithContentDescription(context.getString(R.string.expand_button_content_description))

    @Test
    fun homeScreen_loadingState_isDisplayedCorrectly(){
        rule.setContent {
            HomeScreen(
                state = getState(isLoading = true),
                navigateTo = {},
                uiLayout = uiLayout,
                onNotificationClick = { _,_ -> }
            )
        }

        notificationTabSelector.assertIsDisplayed()
        bookingTabSelecor.assertIsDisplayed()
        loadingIndicator.assertIsDisplayed()
        bookingItem.assertIsNotDisplayed()
        notificationBadge.assertIsNotDisplayed()
        notificationBadgeText.assertIsNotDisplayed()
        noNotificationsText.assertIsNotDisplayed()
        notificationCard.assertIsNotDisplayed()
        bookingTabSelecor.performClick()
        loadingIndicator.assertIsDisplayed()
        noBookingsText.assertIsNotDisplayed()

    }

    @Test
    fun homeScreen_errorState_isDisplayedCorrectly(){
        rule.setContent {
            HomeScreen(
                state = getState(apiError = testError),
                navigateTo = {},
                uiLayout = uiLayout,
                onNotificationClick = { _,_ -> }
            )
        }

        notificationTabSelector.assertIsDisplayed()
        bookingTabSelecor.assertIsDisplayed()
        loadingIndicator.assertIsNotDisplayed()
        bookingItem.assertIsNotDisplayed()
        notificationBadge.assertIsNotDisplayed()
        notificationBadgeText.assertIsNotDisplayed()
        notificationCard.assertIsNotDisplayed()
        noNotificationsText.assertIsNotDisplayed()
        errorContainer.assertIsDisplayed()
        bookingTabSelecor.performClick()
        errorContainer.assertIsDisplayed()
        loadingIndicator.assertIsNotDisplayed()
        noBookingsText.assertIsNotDisplayed()
    }


    @Test
    fun homeScreen_noNotifications_isDisplayedCorrectly(){
        rule.setContent {
            HomeScreen(
                state = getState(notifications = emptyList(), unReadNotifications = 0),
                navigateTo = {},
                uiLayout = uiLayout,
                onNotificationClick = { _,_ -> }
            )
        }

        notificationTabSelector.assertIsDisplayed()
        bookingTabSelecor.assertIsDisplayed()
        loadingIndicator.assertIsNotDisplayed()
        bookingItem.assertIsNotDisplayed()
        notificationBadge.assertIsNotDisplayed()
        notificationBadgeText.assertIsNotDisplayed()
        notificationCard.assertIsNotDisplayed()
        noNotificationsText.assertIsDisplayed()
        errorContainer.assertIsNotDisplayed()
    }

    @Test
    fun homeScreen_noBookings_isDisplayedCorrectly(){
        rule.setContent {
            HomeScreen(
                state = getState(bookings = emptyList()),
                navigateTo = {},
                uiLayout = uiLayout,
                onNotificationClick = { _,_ -> }
            )
        }
        
        notificationTabSelector.assertIsDisplayed()
        bookingTabSelecor.assertIsDisplayed()
        loadingIndicator.assertIsNotDisplayed()
        errorContainer.assertIsNotDisplayed()
        bookingTabSelecor.performClick()
        noBookingsText.assertIsDisplayed()
        bookingItem.assertIsNotDisplayed()
    }

    @Test
    fun homeScreen_notificationsExist_isDisplayedCorrectly(){
        val notifications = listOf(
            Notification(
                notificationId = "1",
                title = "testTitle",
                message = "testMessage",
                isRead = false,
                userId = "",
                type = NotificationType.GENERAL,
                createdAt = today,
                relatedEntityId = ""
            ),
            Notification(
                notificationId = "2",
                title = "testTitle",
                message = "testMessage",
                isRead = false,
                userId = "",
                type = NotificationType.GENERAL,
                createdAt = today.plusDays(1),
                relatedEntityId = ""
            ),
            Notification(
                notificationId = "3",
                title = "testTitle",
                message = "testMessage",
                isRead = false,
                userId = "",
                type = NotificationType.GENERAL,
                createdAt = today.plusDays(3),
                relatedEntityId = ""
            ),
            )
        rule.setContent {
            HomeScreen(
                state = getState(notifications = notifications, unReadNotifications = notifications.size),
                navigateTo = {},
                uiLayout = uiLayout,
                onNotificationClick = { _,_ -> }
            )
        }

        rule.waitForIdle()
        noNotificationsText.assertIsNotDisplayed()
        notificationBadge.assertIsDisplayed()
        notificationBadgeText.assertIsDisplayed()
        notificationBadgeText.assertTextEquals(notifications.size.toString())
        val displayedNotifications = rule.onAllNodesWithTag("notificationCard").fetchSemanticsNodes()
        assert(displayedNotifications.size == notifications.size)
    }


    @Test
    fun homeScreen_onClickNotification_stateShiftsCorrectly(){

        val notifications = listOf(
            Notification(
                notificationId = "1",
                title = "testTitle",
                message = "testMessage",
                isRead = false,
                userId = "",
                type = NotificationType.GENERAL,
                createdAt = today,
                relatedEntityId = ""
            ),
        )
        var clickedNotificationId: String? = null
        val onNotificationClick: (String, Boolean) -> Unit = { notificationId,_ ->
            clickedNotificationId = notificationId
            notifications.first { it.notificationId == notificationId }.isRead = true
        }


        rule.setContent {
            HomeScreen(
                state = getState(notifications = notifications, unReadNotifications = notifications.size),
                navigateTo = {},
                uiLayout = uiLayout,
                onNotificationClick = onNotificationClick
            )
        }

        notificationCard.performClick()
        rule.waitForIdle()
        assert(clickedNotificationId == notifications[0].notificationId)
        assert(notifications[0].isRead)
    }


    @Test
    fun homeScreen_bookingsExist_isDisplayedCorrectly(){
        val bookings = listOf(
            Booking(id= "1", date = LocalDateTime.now().plusDays(1)),
            Booking(id= "2", date = LocalDateTime.now().plusDays(2)),
            Booking(id= "3", date = LocalDateTime.now().plusDays(3))
        )

        rule.setContent {
            HomeScreen(
                state = getState(bookings = bookings),
                navigateTo = {},
                uiLayout = uiLayout,
                onNotificationClick = { _,_ -> }
            )
        }

        rule.waitForIdle()
        bookingTabSelecor.performClick()
        val displayedBookings = rule.onAllNodesWithTag("UpcomingBooking").fetchSemanticsNodes()
        assert(displayedBookings.size == bookings.size)
    }


    @Test
    fun homeScreen_onlyFirstUpcomingBooking_isExpanded() {
        val bookings = listOf(
            Booking(id= "1", date = LocalDateTime.now().minusDays(2)),
            Booking(id= "2", date = LocalDateTime.now().plusDays(1), boat = "expandedBoat", battery = "expandedBattery"),
            Booking(id= "3", date = LocalDateTime.now().plusDays(2), boat="collapsedBoat", battery = "collapsedBattery")
        )

        rule.setContent {
            HomeScreen(
                state = getState(bookings = bookings),
                navigateTo = {},
                uiLayout = uiLayout,
                onNotificationClick = { _,_ -> }
            )
        }

        bookingTabSelecor.performClick()
        rule.onNodeWithText("Boat: expandedBoat").assertIsDisplayed()
        rule.onNodeWithText("Battery: expandedBattery").assertIsDisplayed()
        rule.onNodeWithText("Boat: collapsedBoat").assertIsNotDisplayed()
        rule.onNodeWithText("Battery: collapsedBattery").assertIsNotDisplayed()
    }

    @Test
    fun homeScreen_pastBookings_areFaded() {
        val pastBooking = Booking(id= "1", date = LocalDateTime.now().minusDays(1))

        rule.setContent {
            HomeScreen(
                state = getState(bookings = listOf(pastBooking)),
                navigateTo = {},
                uiLayout = uiLayout,
                onNotificationClick = { _,_ -> }
            )
        }

        bookingTabSelecor.performClick()
        rule.onNodeWithTag("PastBooking").assertIsDisplayed()
        rule.onNodeWithTag("UpcomingBooking").assertIsNotDisplayed()
    }

    @Test
    fun homeScreen_expandBooking_onIconClick() {
        val booking = Booking(id= "1", date = LocalDateTime.now().minusDays(2), boat = "testBoat")

        rule.setContent {
            HomeScreen(
                state = getState(bookings = listOf(booking)),
                navigateTo = {},
                uiLayout = uiLayout,
                onNotificationClick = { _,_ -> }
            )
        }

        bookingTabSelecor.performClick()
        rule.onNodeWithTag("ExpandButton").performClick()
        rule.onNodeWithText("${context.getString(R.string.boat)}: ${booking.boat}").assertIsDisplayed()
    }

    @Test
    fun homeScreen_collapseBooking_onIconClick() {
        val booking = Booking(id= "1", date = LocalDateTime.now().minusDays(1), boat = "testBoat")

        rule.setContent {
            HomeScreen(
                state = getState(bookings = listOf(booking)),
                navigateTo = {},
                uiLayout = uiLayout,
                onNotificationClick = { _,_ -> }
            )
        }

        bookingTabSelecor.performClick()

        bookingExpansionButton.performClick()
        bookingExpansionButton.performClick()
        rule.onNodeWithText("${context.getString(R.string.boat)}: ${booking.boat}").assertDoesNotExist()
    }

    @Test
    fun homeScreen_missingBoatMessage_isDisplayed() {
        val booking = Booking(id= "1", date = LocalDateTime.now().plusDays(1), boat = null)

        rule.setContent {
            HomeScreen(
                state = getState(bookings = listOf(booking)),
                navigateTo = {},
                uiLayout = uiLayout,
                onNotificationClick = { _,_ -> }
            )
        }

        bookingTabSelecor.performClick()
        rule.onNodeWithText(context.getString(R.string.no_boat_assigned)).assertIsDisplayed()
    }

    @Test
    fun homeScreen_missingBatteryMessage_isDisplayed() {
        val booking = Booking(id= "1", date = LocalDateTime.now().plusDays(1), battery = null)

        rule.setContent {
            HomeScreen(
                state = HomeScreenState(bookings = listOf(booking), isLoading = false),
                navigateTo = {},
                uiLayout = uiLayout,
                onNotificationClick = { _,_ -> }
            )
        }

        bookingTabSelecor.performClick()
        rule.onNodeWithTag(context.getString(R.string.booking_list_title)).performClick()
        rule.onNodeWithText(context.getString(R.string.no_battery_assigned)).assertIsDisplayed()
    }


    private fun getState(user: User? = null, notifications : List<Notification> = emptyList(), bookings : List<Booking> = emptyList(), isLoading : Boolean = false, apiError: String = "", unReadNotifications: Int = 0 ) : HomeScreenState {
        return HomeScreenState(
            user = user,
            notifications = notifications,
            bookings = bookings,
            isLoading = isLoading,
            apiError = apiError,
            unReadNotifications = unReadNotifications)
    }
}