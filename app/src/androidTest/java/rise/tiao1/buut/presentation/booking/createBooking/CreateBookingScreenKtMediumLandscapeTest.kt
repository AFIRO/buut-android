package rise.tiao1.buut.presentation.booking.createBooking

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.espresso.device.action.ScreenOrientation
import androidx.test.espresso.device.rules.ScreenOrientationRule
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import rise.tiao1.buut.R
import rise.tiao1.buut.domain.booking.TimeSlot
import rise.tiao1.buut.presentation.home.HomeScreen
import rise.tiao1.buut.presentation.home.HomeScreenState
import rise.tiao1.buut.utils.NavigationKeys
import rise.tiao1.buut.utils.UiLayout
import java.time.LocalDateTime


class CreateBookingScreenKtMediumLandscapeTest {
    val startOrientation = ScreenOrientation.LANDSCAPE
    val updatedOrientation = ScreenOrientation.PORTRAIT
    val uiLayout = UiLayout.LANDSCAPE_MEDIUM

    @get:Rule
    val rule: ComposeContentTestRule =
        createComposeRule()

    @get:Rule
    val screenOrientationRule: ScreenOrientationRule = ScreenOrientationRule(startOrientation)
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    var navControllerState by mutableStateOf<NavController?>(null)
    val testError = "testError"
    val today = LocalDateTime.now()

    val navigation = rule.onNodeWithTag("navigation")
    val loadingIndicator = rule.onNodeWithTag(context.getString(R.string.loading_indicator))
    val errorContainer = rule.onNodeWithText(testError)
    val calendar = rule.onNodeWithTag(context.getString(R.string.calendar))
    val infoContainer = rule.onNodeWithText(context.getString(R.string.select_date))
    val morningTimeslot = rule.onNodeWithText("Morning")
    val afternoonTimeslot = rule.onNodeWithText("Afternoon")
    val eveningTimeslot = rule.onNodeWithText("Evening")
    val bookingConfirmationModal = rule.onNodeWithTag("bookingConfirmationModal")
    val modalTitle = rule.onNodeWithText(context.getString(R.string.confirm_booking_modal_header))
    val modalMessage =
        rule.onNodeWithText(context.getString(R.string.confirm_booking_prompt) + " ${getSelectableTimeSlots()[0].slot}?")
    val modalConfirmButton = rule.onNodeWithText(context.getString(R.string.confirm))
    val modalCancelButton = rule.onNodeWithText(context.getString(R.string.cancel))
    val notificationModal = rule.onNodeWithTag("notificationModal")
    val modalCloseButton = rule.onNodeWithText(context.getString(R.string.close_label))
    val modalNotificationText =
        rule.onNodeWithText(context.getString(R.string.booking_info_follows))


    @Test
    fun createBookingScreen_loadingState_showsLoadingIndicator() {
        rule.setContent {
            CreateBookingScreen(
                state = CreateBookingScreenState(datesAreLoading = true),
                uiLayout = uiLayout
            )
        }
        loadingIndicator.assertIsDisplayed()
        calendar.assertIsNotDisplayed()
        errorContainer.assertIsNotDisplayed()
    }

    @Test
    fun createBookingScreen_errorState_displaysErrorMessage() {
        rule.setContent {
            CreateBookingScreen(
                state = CreateBookingScreenState(getFreeDatesError = testError),
                uiLayout = uiLayout
            )
        }
        errorContainer.assertIsDisplayed()
    }

    @Test
    fun createBookingScreen_possibleDatesLoaded_screenDisplayedCorrectly() {
        rule.setContent {
            CreateBookingScreen(
                state = CreateBookingScreenState(datesAreLoading = false),
                uiLayout = uiLayout
            )
        }
        calendar.assertIsDisplayed()
        infoContainer.assertIsDisplayed()
    }

    @Test
    fun createBookingScreen_timeSlotsLoaded_TimeslotsDisplayedCorrectly() {
        rule.setContent {
            CreateBookingScreen(
                state = CreateBookingScreenState(
                    selectableTimeSlots = getSelectableTimeSlots()
                ),
                uiLayout = uiLayout
            )
        }
        morningTimeslot.assertIsDisplayed()
        afternoonTimeslot.assertIsDisplayed()
        eveningTimeslot.assertIsDisplayed()
    }

    @Test
    fun createBookingScreen_onTimeSlotClicked_modalDisplayedCorrectlyAndTimeSlotSelected() {
        var displayModal by mutableStateOf(false)
        var selectedTimeSlot by mutableStateOf<TimeSlot?>(null)
        rule.setContent {
            CreateBookingScreen(
                state = CreateBookingScreenState(
                    selectableTimeSlots = getSelectableTimeSlots(),
                    confirmationModalOpen = displayModal,
                    selectedTimeSlot = selectedTimeSlot
                ),
                uiLayout = uiLayout,
                onTimeSlotClicked = {
                    displayModal = true
                    selectedTimeSlot = getSelectableTimeSlots()[0]
                }
            )
        }
        morningTimeslot.performClick()
        assert(selectedTimeSlot == getSelectableTimeSlots()[0])
        assert(displayModal)
        rule.waitForIdle()
        bookingConfirmationModal.assertIsDisplayed()
        modalTitle.assertIsDisplayed()
        modalMessage.assertIsDisplayed()
        modalConfirmButton.assertIsDisplayed()
        modalCancelButton.assertIsDisplayed()
    }

    @Test
    fun createBookingScreen_onModalDismiss_modalClosesCorrectlyAndTimeSlotCleared() {
        var displayModal by mutableStateOf(false)
        var selectedTimeSlot by mutableStateOf<TimeSlot?>(null)
        rule.setContent {
            CreateBookingScreen(
                state = CreateBookingScreenState(
                    selectableTimeSlots = getSelectableTimeSlots(),
                    confirmationModalOpen = displayModal,
                    selectedTimeSlot = selectedTimeSlot
                ),
                uiLayout = uiLayout,
                onTimeSlotClicked = {
                    displayModal = true
                    selectedTimeSlot = getSelectableTimeSlots()[0]
                },
                onDismissBooking = {
                    displayModal = false
                    selectedTimeSlot = null
                }
            )
        }
        morningTimeslot.performClick()
        rule.waitForIdle()
        modalCancelButton.performClick()
        assert(!displayModal)
        assert(selectedTimeSlot == null)
        modalTitle.assertIsNotDisplayed()
        modalMessage.assertIsNotDisplayed()
        modalConfirmButton.assertIsNotDisplayed()
        modalCancelButton.assertIsNotDisplayed()
    }

    @Test
    fun createBookingScreen_onBookingConfirm_modalClosesCorrectlyAndShowsNewModal() {
        var displayModal by mutableStateOf(false)
        var selectedTimeSlot by mutableStateOf<TimeSlot?>(null)
        var displayNotificationModal by mutableStateOf(false)
        rule.setContent {
            CreateBookingScreen(
                state = CreateBookingScreenState(
                    selectableTimeSlots = getSelectableTimeSlots(),
                    confirmationModalOpen = displayModal,
                    selectedTimeSlot = selectedTimeSlot,
                    notificationModalOpen = displayNotificationModal
                ),
                uiLayout = uiLayout,
                onTimeSlotClicked = {
                    displayModal = true
                    selectedTimeSlot = getSelectableTimeSlots()[0]
                },
                onDismissBooking = {
                    displayModal = false
                    selectedTimeSlot = null
                },
                onConfirmBooking = {
                    displayModal = false
                    displayNotificationModal = true
                }
            )
        }
        morningTimeslot.performClick()
        rule.waitForIdle()
        modalConfirmButton.performClick()
        rule.waitForIdle()
        assert(!displayModal)
        modalTitle.assertIsNotDisplayed()
        modalMessage.assertIsNotDisplayed()
        modalConfirmButton.assertIsNotDisplayed()
        modalCancelButton.assertIsNotDisplayed()
        assert(displayNotificationModal)
        modalNotificationText.assertIsDisplayed()
        modalCloseButton.assertIsDisplayed()
        notificationModal.assertIsDisplayed()
    }

    @Test
    fun createBookingScreen_onBookingError_showsError() {
        var displayModal by mutableStateOf(false)
        var selectedTimeSlot by mutableStateOf<TimeSlot?>(null)
        var error by mutableStateOf<String?>(null)
        rule.setContent {
            CreateBookingScreen(
                state = CreateBookingScreenState(
                    selectableTimeSlots = getSelectableTimeSlots(),
                    confirmationModalOpen = displayModal,
                    selectedTimeSlot = selectedTimeSlot,
                    confirmationError = error
                ),
                uiLayout = uiLayout,
                onTimeSlotClicked = {
                    displayModal = true
                    selectedTimeSlot = getSelectableTimeSlots()[0]
                },
                onDismissBooking = {
                    displayModal = false
                    selectedTimeSlot = null
                },
                onConfirmBooking = {
                    error = testError
                }
            )
        }
        morningTimeslot.performClick()
        rule.waitForIdle()
        modalConfirmButton.performClick()
        rule.waitForIdle()
        assertEquals(error, testError)
        errorContainer.assertIsDisplayed()
    }

    @Test
    fun createBookingScreen_onBookingSucces_modalClosesCorrectlyAndNavigateHome() {
        var displayModal by mutableStateOf(false)
        var selectedTimeSlot by mutableStateOf<TimeSlot?>(null)
        var displayNotificationModal by mutableStateOf(false)
        rule.setContent {
            val navController = rememberNavController()
            navControllerState = navController
            NavHost(
                navController = navController,
                startDestination = NavigationKeys.Route.CREATE_BOOKING
            ) {
                composable(route = NavigationKeys.Route.CREATE_BOOKING) {
                    CreateBookingScreen(
                        state = CreateBookingScreenState(
                            selectableTimeSlots = getSelectableTimeSlots(),
                            confirmationModalOpen = displayModal,
                            selectedTimeSlot = selectedTimeSlot,
                            notificationModalOpen = displayNotificationModal
                        ),
                        uiLayout = uiLayout,
                        onTimeSlotClicked = {
                            displayModal = true
                            selectedTimeSlot = getSelectableTimeSlots()[0]
                        },
                        onDismissBooking = {
                            displayModal = false
                            selectedTimeSlot = null
                        },
                        onConfirmBooking = {
                            displayModal = false
                            displayNotificationModal = true
                        },
                        toBookingsOverview = {
                            navController.navigate(NavigationKeys.Route.HOME)
                        }
                    )
                }
                composable(route = NavigationKeys.Route.HOME) {
                    HomeScreen(
                        state = HomeScreenState(),
                        navigateTo = { },
                        uiLayout = uiLayout,
                        onNotificationClick = { _,_ -> }
                    )
                }
            }
        }
        morningTimeslot.performClick()
        rule.waitForIdle()
        modalConfirmButton.performClick()
        rule.waitForIdle()
        assert(!displayModal)
        modalTitle.assertIsNotDisplayed()
        modalMessage.assertIsNotDisplayed()
        modalConfirmButton.assertIsNotDisplayed()
        modalCancelButton.assertIsNotDisplayed()
        assert(displayNotificationModal)
        modalNotificationText.assertIsDisplayed()
        modalCloseButton.assertIsDisplayed()
        notificationModal.assertIsDisplayed()
        modalCloseButton.performClick()
        rule.waitForIdle()
        assertEquals(navControllerState?.currentDestination?.route, NavigationKeys.Route.HOME)
    }


    fun getSelectableTimeSlots(): List<TimeSlot> {
        return listOf(
            TimeSlot(today, "Morning", true),
            TimeSlot(today, "Afternoon", false),
            TimeSlot(today, "Evening", true)
        )
    }
}