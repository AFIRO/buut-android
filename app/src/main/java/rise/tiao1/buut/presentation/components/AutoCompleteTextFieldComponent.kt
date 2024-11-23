package rise.tiao1.buut.presentation.components

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoCompleteTextFieldComponent(
    value: String = "",
    onValueChanged: (String) -> Unit,
    onFocusLost: () -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null,
    optionList: List<String> = emptyList(),
    @StringRes label: Int,
    colors: TextFieldColors? = null,
    alternativeUnfocusedLabelColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    val expanded = remember { mutableStateOf(false) }
    val typedValue = remember { mutableStateOf(value) }

    val filteredOptions = remember(typedValue.value) {
        derivedStateOf {
            if (typedValue.value.isNotBlank()) {
                optionList.filter { it.contains(typedValue.value, ignoreCase = true) }
            } else {
                optionList
            }
        }
    }

    // If colors are provided, use them; otherwise, use default colors
    val appliedColors = colors ?: OutlinedTextFieldDefaults.colors()
    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = !expanded.value },
        modifier = modifier.testTag(stringResource(label))
    ) {
        OutlinedTextFieldComponent(
            value = typedValue.value,
            onValueChanged = {newValue ->
                typedValue.value = newValue
                expanded.value = true
            },
            onFocusLost = onFocusLost,
            label = label,
            modifier = modifier.menuAnchor(),
            isError = isError,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
            },
            colors = appliedColors,
            alternativeUnfocusedLabelColor = alternativeUnfocusedLabelColor

        )
        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier
                .heightIn(max = 200.dp)
                .background(color = Color.White)

        ) {
            filteredOptions.value.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option)},
                    onClick = {
                        typedValue.value = option
                        expanded.value = false
                        onValueChanged(option)
                    }
                )
            }
        }

    }
    if (!errorMessage.isNullOrEmpty())
        ErrorMessageContainer(errorMessage)
}