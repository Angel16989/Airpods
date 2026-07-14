package com.angel16989.appleicon.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.angel16989.appleicon.ui.theme.AppleIconSize
import com.angel16989.appleicon.ui.theme.AppleIconSpacing

@Composable
fun AppleIconLoadingIndicator(
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.testTag("apple_icon_loading_indicator"),
        horizontalArrangement = Arrangement.spacedBy(AppleIconSpacing.Medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(AppleIconSize.LoadingIndicator),
            strokeWidth = AppleIconSpacing.ExtraSmall,
        )
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun AppleIconEmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    StateCopy(
        title = title,
        message = message,
        modifier = modifier.testTag("apple_icon_empty_state"),
        actionLabel = actionLabel,
        onAction = onAction,
    )
}

@Composable
fun AppleIconErrorState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    StateCopy(
        title = title,
        message = message,
        modifier = modifier.testTag("apple_icon_error_state"),
        actionLabel = actionLabel,
        onAction = onAction,
        titleColor = MaterialTheme.colorScheme.error,
    )
}

@Composable
private fun StateCopy(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    titleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppleIconSpacing.Small),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = title,
            color = titleColor,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(AppleIconSpacing.ExtraSmall))
            AppleIconButton(label = actionLabel, onClick = onAction)
        }
    }
}
