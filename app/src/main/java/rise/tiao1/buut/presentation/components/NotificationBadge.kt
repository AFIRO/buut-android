package rise.tiao1.buut.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun NotificationBadge(count: Int) {
    if (count > 0) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.error, shape = CircleShape)
                .size(20.dp) // Adjust size as needed
                .wrapContentSize(Alignment.Center)
                .testTag("notificationBadge")
        ) {
            Text(
                text = count.toString(),
                color = Color.White,
                modifier = Modifier.testTag("notificationBadgeText")
            )
        }
    }
}