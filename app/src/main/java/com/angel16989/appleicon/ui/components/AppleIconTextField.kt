package com.angel16989.appleicon.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.angel16989.appleicon.ui.theme.AppleIconShape

@Composable
fun AppleIconTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        isError = isError,
        label = { Text(text = label) },
        supportingText =
            supportingText?.let { text ->
                { Text(text = text, style = MaterialTheme.typography.bodySmall) }
            },
        shape = RoundedCornerShape(AppleIconShape.ControlRadius),
        singleLine = true,
    )
}
