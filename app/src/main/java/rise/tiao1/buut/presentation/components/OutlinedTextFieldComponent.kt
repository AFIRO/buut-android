package rise.tiao1.buut.presentation.components

import androidx.annotation.StringRes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun OutlinedTextFieldComponent(
    modifier: Modifier = Modifier,
    value: String,
    onValueChanged: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null,
    @StringRes label: Int,
    keyboardOptions : KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next
    ),
    onFocusLost: () -> Unit,
    readOnly: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = Color.White,
        focusedContainerColor = Color.White,
        focusedBorderColor = Color.Blue,
        errorContainerColor = Color.White,
        focusedLabelColor = Color.White,
        unfocusedLabelColor = Color.Black
    ),
    alternativeUnfocusedLabelColor: Color = Color.White
) {
    val touched = remember { mutableStateOf(false) }
    val isFocused = remember { mutableStateOf(false) }
    val unfocusedColor = if (value.isEmpty()) colors.unfocusedLabelColor else alternativeUnfocusedLabelColor

    OutlinedTextField(
        value = value,
        onValueChange = onValueChanged,
        label = { Text(stringResource(label)) },
        isError = isError,
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = colors.unfocusedContainerColor,
            focusedContainerColor = colors.focusedContainerColor,
            focusedBorderColor = colors.focusedContainerColor,
            errorContainerColor = colors.errorContainerColor,
            focusedLabelColor = colors.focusedContainerColor,
            unfocusedLabelColor = unfocusedColor

        ),
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .onFocusChanged { focusState ->
            if (focusState.isFocused) {
                isFocused.value = true
                touched.value = true
            } else {
                isFocused.value = false
                if (touched.value)
                    onFocusLost()
            }
        },
        readOnly = readOnly,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon
    )

    if (!errorMessage.isNullOrEmpty())
        ErrorMessageContainer(errorMessage)

}