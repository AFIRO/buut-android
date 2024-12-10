package rise.tiao1.buut.presentation.home


import android.content.Context
import android.net.ConnectivityManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import rise.tiao1.buut.R
import rise.tiao1.buut.domain.notification.Notification
import rise.tiao1.buut.presentation.components.ActionErrorContainer
import rise.tiao1.buut.presentation.components.InfoContainer
import rise.tiao1.buut.presentation.components.LoadingIndicator
import rise.tiao1.buut.presentation.components.NotificationCard

@Composable
fun NotificationList(state: HomeScreenState, onNotificationClick: (String, Boolean) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        val lazyListState = rememberLazyListState()
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = if (!state.isNetworkAvailable || !state.apiError.isNullOrBlank()) 110.dp else 0.dp)
        ) {
            itemsIndexed(state.notifications) { index, notification ->
                if (notification is Notification) {
                    NotificationCard(notification, onNotificationClick)
                }
            }
        }

        if (!state.isNetworkAvailable) {
            ActionErrorContainer(
                LocalContext.current.getString(R.string.no_internet_connection),
            )
        } else if (state.isLoading) {
            LoadingIndicator()
        } else if (!state.apiError.isNullOrBlank()) {
            ActionErrorContainer(state.apiError)
        } else if (state.notifications.isEmpty()) {
            InfoContainer(stringResource(R.string.user_has_no_notifications))
        }
    }
}