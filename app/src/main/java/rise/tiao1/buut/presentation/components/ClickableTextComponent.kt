package rise.tiao1.buut.presentation.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun ClickableTextComponent(
    @StringRes leadingText: Int? = null,
    @StringRes clickableText: Int,
    @StringRes trailingText: Int? = null,
    onClick: () -> Unit,
    isEnabled: Boolean = true
) {
    Row {
        if(leadingText != null) {
            Text(
                text = stringResource(leadingText),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text= stringResource(clickableText),
            color = Color.White,
            fontStyle = FontStyle.Italic,
            textDecoration = TextDecoration.Underline,
            modifier = if (isEnabled) Modifier.clickable { onClick() } else Modifier,
            fontWeight = FontWeight.Bold
        )
        if(trailingText != null) {
            Text(
                text = stringResource(trailingText),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}