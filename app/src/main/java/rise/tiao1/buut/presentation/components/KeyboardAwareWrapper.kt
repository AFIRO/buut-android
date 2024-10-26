package rise.tiao1.buut.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun KeyboardAware (
    content: @Composable () -> Unit
) {
   Box (modifier = Modifier.imePadding()) {
       content()
   }
}