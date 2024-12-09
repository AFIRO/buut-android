package rise.tiao1.buut.presentation.components

import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import rise.tiao1.buut.R

@Composable
fun SendEmailToBatteryOwner(recipientEmail: String) {
    val context = LocalContext.current
    val subject = stringResource(R.string.battery_owner_email_subject)
    val body = stringResource(R.string.battery_owner_email_body)

    IconButton(onClick = {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$recipientEmail")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        context.startActivity(Intent.createChooser(intent, "Send Email"))
    }, modifier = Modifier.testTag("sendEmailButton")) {
        Icon(
            imageVector = Icons.Filled.Email,
            contentDescription = "Send Email",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}