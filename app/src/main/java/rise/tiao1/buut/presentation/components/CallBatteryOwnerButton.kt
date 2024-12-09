package rise.tiao1.buut.presentation.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.core.content.ContextCompat

@Composable
fun CallBatteryOwnerButton(phoneNumber: String) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val uri = Uri.parse("tel:$phoneNumber")
                val intent = Intent(Intent.ACTION_CALL, uri)
                context.startActivity(intent)
            }
        }
    )

    IconButton(onClick = {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            launcher.launch(Manifest.permission.CALL_PHONE)
        } else {
            val uri = Uri.parse("tel:$phoneNumber")
            val intent = Intent(Intent.ACTION_CALL, uri)
            context.startActivity(intent)
        }
    }, modifier = Modifier.testTag("callBatteryOwnerButton")) {
        Icon(
            imageVector = Icons.Filled.Phone,
            contentDescription = "Call",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}