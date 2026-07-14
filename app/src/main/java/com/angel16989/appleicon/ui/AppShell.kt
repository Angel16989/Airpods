package com.angel16989.appleicon.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.angel16989.appleicon.ui.components.AppleIconButton
import com.angel16989.appleicon.ui.components.AppleIconEmptyState
import com.angel16989.appleicon.ui.components.AppleIconLoadingIndicator
import com.angel16989.appleicon.ui.theme.AppleIconShape
import com.angel16989.appleicon.ui.theme.AppleIconSpacing
import com.angel16989.appleicon.ui.theme.AppleIconTheme

@Composable
fun AppleIconApp(modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Surface(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            color = MaterialTheme.colorScheme.background,
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val isWide = maxWidth >= 720.dp
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = AppleIconSpacing.Screen)
                            .padding(vertical = AppleIconSpacing.ExtraLarge),
                    verticalArrangement = Arrangement.spacedBy(AppleIconSpacing.ExtraLarge),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Header(modifier = Modifier.widthIn(max = 960.dp))
                    if (isWide) {
                        Row(
                            modifier = Modifier.widthIn(max = 960.dp),
                            horizontalArrangement = Arrangement.spacedBy(AppleIconSpacing.Large),
                        ) {
                            DashboardPanel(
                                title = "Status",
                                modifier = Modifier.weight(1f),
                            ) {
                                MonitoringSummary()
                            }
                            DashboardPanel(
                                title = "Latest Snapshot",
                                modifier = Modifier.weight(1f),
                            ) {
                                LastSnapshotPlaceholder()
                            }
                        }
                    } else {
                        DashboardPanel(
                            title = "Status",
                            modifier = Modifier.widthIn(max = 560.dp),
                        ) {
                            MonitoringSummary()
                        }
                        DashboardPanel(
                            title = "Latest Snapshot",
                            modifier = Modifier.widthIn(max = 560.dp),
                        ) {
                            LastSnapshotPlaceholder()
                        }
                    }
                    DashboardPanel(
                        title = "Settings",
                        modifier = Modifier.widthIn(max = 960.dp),
                    ) {
                        SettingsPlaceholder()
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppleIconSpacing.Small),
    ) {
        Text(
            text = "Apple Icon",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "AirPods status",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun DashboardPanel(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppleIconShape.PanelRadius),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(AppleIconSpacing.Large),
            verticalArrangement = Arrangement.spacedBy(AppleIconSpacing.Large),
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            content()
        }
    }
}

@Composable
private fun MonitoringSummary() {
    Column(verticalArrangement = Arrangement.spacedBy(AppleIconSpacing.Medium)) {
        StatusRow(label = "Monitoring", value = "Ready")
        StatusRow(label = "Bluetooth", value = "Pending")
        StatusRow(label = "Overlay", value = "Pending")
        StatusRow(label = "Notifications", value = "Pending")
        HorizontalDivider()
        AppleIconLoadingIndicator(label = "Idle")
    }
}

@Composable
private fun LastSnapshotPlaceholder() {
    Column(verticalArrangement = Arrangement.spacedBy(AppleIconSpacing.Large)) {
        AppleIconEmptyState(
            title = "No AirPods detected yet",
            message = "Last battery status will appear here.",
        )
        AppleIconButton(
            label = "Test Popup",
            onClick = {},
            enabled = false,
        )
    }
}

@Composable
private fun SettingsPlaceholder() {
    Column(verticalArrangement = Arrangement.spacedBy(AppleIconSpacing.Medium)) {
        ToggleRow(label = "Monitoring", checked = false)
        ToggleRow(label = "Popup", checked = false)
        ToggleRow(label = "Persistent notification", checked = false)
    }
}

@Composable
private fun StatusRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = value,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Switch(
            checked = checked,
            onCheckedChange = null,
            enabled = false,
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun AppleIconAppMobilePreview() {
    AppleIconTheme {
        AppleIconApp()
    }
}

@Preview(showBackground = true, widthDp = 900, heightDp = 700)
@Composable
private fun AppleIconAppWidePreview() {
    AppleIconTheme {
        AppleIconApp()
    }
}
