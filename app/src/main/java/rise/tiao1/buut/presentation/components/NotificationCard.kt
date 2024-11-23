package rise.tiao1.buut.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import rise.tiao1.buut.domain.notification.Notification
import rise.tiao1.buut.utils.NotificationType

@Composable
fun NotificationCard(notification: Notification, onClick: (String, Boolean) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .testTag("notificationCard"),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = if (notification.isRead) {CardDefaults.cardColors(containerColor = Color.LightGray)} else {CardDefaults.cardColors(containerColor = Color.White)},
        onClick = { onClick(notification.notificationId, notification.isRead) }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.title,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            NotificationTypeTag(notification.type)
        }
    }
}

@Composable
fun NotificationTypeTag(type: NotificationType) {
    val backgroundColor = when (type) {
        NotificationType.GENERAL -> MaterialTheme.colorScheme.primary
        NotificationType.ALERT -> MaterialTheme.colorScheme.error
        NotificationType.REMINDER -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.tertiary
    }
    Box(
        modifier = Modifier
            .background(backgroundColor, shape = RoundedCornerShape(4.dp))
            .padding(4.dp)
            .widthIn(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (type == NotificationType.USERREGISTRATION) "REGISTER" else type.name,
            color = Color.White
        )
    }
}