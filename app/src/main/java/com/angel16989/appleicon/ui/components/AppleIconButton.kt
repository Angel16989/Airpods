package com.angel16989.appleicon.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.angel16989.appleicon.ui.theme.AppleIconShape
import com.angel16989.appleicon.ui.theme.AppleIconSize
import com.angel16989.appleicon.ui.theme.AppleIconSpacing

@Composable
fun AppleIconButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier.heightIn(min = AppleIconSize.TouchTarget),
        shape = RoundedCornerShape(AppleIconShape.ControlRadius),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier =
                    Modifier
                        .size(AppleIconSize.LoadingIndicator)
                        .testTag("apple_icon_button_loading"),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp,
            )
            Spacer(modifier = Modifier.width(AppleIconSpacing.Small))
        }
        Text(text = label)
    }
}
