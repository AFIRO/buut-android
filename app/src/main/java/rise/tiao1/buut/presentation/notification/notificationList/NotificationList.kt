package rise.tiao1.buut.presentation.notification.notificationList

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import rise.tiao1.buut.R
import rise.tiao1.buut.domain.notification.Notification
import rise.tiao1.buut.presentation.components.ActionErrorContainer
import rise.tiao1.buut.presentation.components.InfoContainer
import rise.tiao1.buut.presentation.components.LoadingIndicator
import rise.tiao1.buut.presentation.components.NotificationCard
import rise.tiao1.buut.presentation.home.HomeScreenState

@Composable
fun NotificationList(state: HomeScreenState, onNotificationClick: (String, Boolean) -> Unit) {

    when {
        state.isLoading -> {
            LoadingIndicator()
        }

        !state.apiError.isNullOrBlank() -> {
            ActionErrorContainer(state.apiError)
        }

        state.notifications.isEmpty() -> {
            InfoContainer(stringResource(R.string.user_has_no_notifications))
        }

        else -> {
            val lazyListState = rememberLazyListState()

            LazyColumn(state = lazyListState) {
                itemsIndexed(state.notifications) {index, notification ->
                    if (notification is Notification) {
                        NotificationCard(notification, onNotificationClick)
                    }
                }
            }
        }
    }
}