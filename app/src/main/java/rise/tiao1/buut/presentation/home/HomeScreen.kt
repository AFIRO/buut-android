package rise.tiao1.buut.presentation.home

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.PrimaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import rise.tiao1.buut.R
import rise.tiao1.buut.domain.booking.Booking
import rise.tiao1.buut.domain.notification.Notification
import rise.tiao1.buut.presentation.booking.bookingList.BookingList
import rise.tiao1.buut.presentation.components.Navigation
import rise.tiao1.buut.presentation.components.NotificationBadge
import rise.tiao1.buut.presentation.notification.notificationList.NotificationList
import rise.tiao1.buut.ui.theme.AppTheme
import rise.tiao1.buut.utils.NavigationKeys
import rise.tiao1.buut.utils.NotificationType
import rise.tiao1.buut.utils.UiLayout
import rise.tiao1.buut.utils.UiLayout.LANDSCAPE_EXPANDED
import rise.tiao1.buut.utils.UiLayout.LANDSCAPE_MEDIUM
import rise.tiao1.buut.utils.UiLayout.LANDSCAPE_SMALL
import rise.tiao1.buut.utils.UiLayout.PORTRAIT_EXPANDED
import rise.tiao1.buut.utils.UiLayout.PORTRAIT_MEDIUM
import rise.tiao1.buut.utils.UiLayout.PORTRAIT_SMALL
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeScreenState,
    navigateTo: (String) -> Unit,
    uiLayout: UiLayout,
    onNotificationClick: (String, Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier
                                .size(dimensionResource(R.dimen.image_size_standard))
                                .padding(top = dimensionResource(R.dimen.padding_small)),
                            painter = painterResource(R.drawable.buut_logo),
                            contentDescription = stringResource(R.string.buut_logo),
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.inversePrimary
                )
            )
        },
        bottomBar = {
            if (uiLayout == PORTRAIT_SMALL || uiLayout == PORTRAIT_MEDIUM) {
                Navigation(
                    navigateTo = navigateTo,
                    uiLayout = uiLayout,
                    currentPage = NavigationKeys.Route.HOME
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (uiLayout == PORTRAIT_SMALL || uiLayout == PORTRAIT_MEDIUM) {
                Column {
                    Content(state, onNotificationClick)
                }
            } else {
                Row {
                    Navigation(
                        uiLayout = uiLayout,
                        navigateTo = navigateTo,
                        currentPage = NavigationKeys.Route.HOME,
                        content = {Content(state, onNotificationClick)}
                    )
                    Content(state, onNotificationClick)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Content(state: HomeScreenState, onNotificationClick: (String, Boolean) -> Unit) {
    val tabItems = listOf(
        TabItem(title= stringResource(R.string.notifications_title)),
        TabItem(title= stringResource(R.string.booking_list_title))
    )
    var selectedTabIndex by rememberSaveable { mutableIntStateOf( 0) }
    val pagerState = rememberPagerState {
        tabItems.size
    }

    LaunchedEffect (selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    LaunchedEffect (pagerState.currentPage, pagerState.isScrollInProgress) {
        if(!pagerState.isScrollInProgress)
            selectedTabIndex = pagerState.currentPage
    }

    Column {
        TabRow(selectedTabIndex = selectedTabIndex,
            indicator = { tabPositions ->
                PrimaryIndicator(
                    Modifier
                        .tabIndicatorOffset(tabPositions[selectedTabIndex])
                )
            }) {
            tabItems.forEachIndexed { index, item ->
                Tab(
                    selected = index == selectedTabIndex,
                    onClick = {
                        selectedTabIndex = index
                    },
                    text = {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.labelMedium
                        )

                    },
                    modifier = Modifier.testTag(item.title),
                    icon = {if (index == 0) NotificationBadge(state.unReadNotifications)}
                )
            }
        }
        HorizontalPager(
            state= pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { index ->
            Box (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(R.dimen.padding_medium))
            ) {
                when (tabItems[index].title){
                    stringResource(R.string.booking_list_title) -> {
                        BookingList(state)
                    }
                    stringResource(R.string.notifications_title) -> {
                        NotificationList(state, onNotificationClick)
                    }
                }
            }
        }
    }
}

data class TabItem(
    val title: String,
)

private fun getPreviewHomeScreenState(emptyList: Boolean = false): HomeScreenState {
    return HomeScreenState(
        null, if (emptyList) emptyList() else listOf(
            Booking(
                id = "0",
                date = LocalDateTime.now().minusDays(2),
                boat = "Boat 0",
                battery = "Battery Z"
            ),
            Booking(
                id = "1",
                date = LocalDateTime.now(),
                boat = "Boat 1",
                battery = "Battery A"
            ),
            Booking(
                id = "2",
                date = LocalDateTime.now().plusDays(1),
                boat = "Boat 2",
                battery = "Battery B"
            ),
            Booking(
                id = "3",
                date = LocalDateTime.now().plusDays(2),
                boat = "Boat 3",
                battery = "Battery C"
            )
        ),

        if (emptyList) emptyList() else listOf(
            Notification(
                notificationId = "0",
                userId = "0",
                title = "Notification 0",
                message = "Notification message 0",
                isRead = false,
                type = NotificationType.GENERAL,
                createdAt = LocalDateTime.now().minusDays(2),
                relatedEntityId = "0"
            ),
            Notification(
                notificationId = "1",
                userId = "0",
                title = "Notification 1",
                message = "Notification message 1",
                isRead = false,
                type = NotificationType.ALERT,
                createdAt = LocalDateTime.now(),
                relatedEntityId = "1"
            ),
            Notification(
                notificationId = "1",
                userId = "0",
                title = "Notification 1",
                message = "Notification message 1",
                isRead = true,
                type = NotificationType.REMINDER,
                createdAt = LocalDateTime.now(),
                relatedEntityId = "1"
            ),
            Notification(
                notificationId = "1",
                userId = "0",
                title = "Notification 1",
                message = "Notification message 1",
                isRead = false,
                type = NotificationType.BOOKING,
                createdAt = LocalDateTime.now(),
                relatedEntityId = "1"
            )
        ),
        isLoading = false,
        unReadNotifications = 3
    )
}

@Preview(showBackground = true)
@Composable
fun PortraitPreview() {
    AppTheme {
        HomeScreen(
            getPreviewHomeScreenState(true), {},  PORTRAIT_SMALL, {_,_->}
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 600,
    heightDp = 300,
    uiMode = Configuration.ORIENTATION_LANDSCAPE
)
@Composable
fun LandscapePreview() {
    AppTheme {
        HomeScreen(
            getPreviewHomeScreenState(), {},  LANDSCAPE_SMALL, {_,_->}
        )
    }
}


@Preview(
    showBackground = true,
    widthDp = 610,
    heightDp = 890,
    uiMode = Configuration.ORIENTATION_PORTRAIT
)
@Composable
fun PortraitMediumPreview() {
    AppTheme {
        HomeScreen(
            getPreviewHomeScreenState(), {},  PORTRAIT_MEDIUM, {_,_->}
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 830,
    heightDp = 482,
    uiMode = Configuration.ORIENTATION_LANDSCAPE
)
@Composable
fun LandscapeMediumPreview() {
    AppTheme {
        HomeScreen(
            getPreviewHomeScreenState(), {},  LANDSCAPE_MEDIUM, {_,_->}
        )
    }
}


@Preview(
    showBackground = true,
    widthDp = 600,
    heightDp = 1000,
    uiMode = Configuration.ORIENTATION_PORTRAIT
)
@Composable
fun PortraitExpandedPreview() {
    AppTheme {
        HomeScreen(
            getPreviewHomeScreenState(), {},  PORTRAIT_EXPANDED, {_,_->}
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 1000,
    heightDp = 480,
    uiMode = Configuration.ORIENTATION_LANDSCAPE
)
@Composable
fun LandscapeExpandedPreview() {
    AppTheme {
        HomeScreen(
            getPreviewHomeScreenState(), {},  LANDSCAPE_EXPANDED, {_,_->}
        )
    }
}

