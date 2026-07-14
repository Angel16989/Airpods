package com.angel16989.appleicon.ui.airpods

import android.animation.ValueAnimator
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.angel16989.appleicon.ui.components.AppleIconButton
import com.angel16989.appleicon.ui.components.AppleIconEmptyState
import com.angel16989.appleicon.ui.components.AppleIconErrorState
import com.angel16989.appleicon.ui.components.AppleIconLoadingIndicator
import com.angel16989.appleicon.ui.theme.AppleIconShape
import com.angel16989.appleicon.ui.theme.AppleIconSize
import com.angel16989.appleicon.ui.theme.AppleIconSpacing
import com.angel16989.appleicon.ui.theme.AppleIconTheme

@Composable
fun AirPodsDashboardScreen(
    state: AirPodsDashboardUiState,
    actions: AirPodsDashboardActions,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .testTag("airpods_dashboard"),
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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
                        DashboardHeader(modifier = Modifier.widthIn(max = 960.dp))
                        if (isWide) {
                            Row(
                                modifier = Modifier.widthIn(max = 960.dp),
                                horizontalArrangement = Arrangement.spacedBy(AppleIconSpacing.Large),
                            ) {
                                DashboardPanel(
                                    title = "Status",
                                    modifier = Modifier.weight(1f),
                                ) {
                                    StatusPanel(state = state, actions = actions)
                                }
                                DashboardPanel(
                                    title = "Latest Snapshot",
                                    modifier = Modifier.weight(1f),
                                ) {
                                    SnapshotPanel(state = state, actions = actions)
                                }
                            }
                        } else {
                            DashboardPanel(
                                title = "Status",
                                modifier = Modifier.widthIn(max = 560.dp),
                            ) {
                                StatusPanel(state = state, actions = actions)
                            }
                            DashboardPanel(
                                title = "Latest Snapshot",
                                modifier = Modifier.widthIn(max = 560.dp),
                            ) {
                                SnapshotPanel(state = state, actions = actions)
                            }
                        }
                        DashboardPanel(
                            title = "Settings",
                            modifier = Modifier.widthIn(max = 960.dp),
                        ) {
                            SettingsPanel(state = state, actions = actions)
                        }
                    }
                }
            }
        }

        state.popup?.let { popup ->
            AirPodsPopupOverlay(
                popup = popup,
                onDismiss = actions.onDismissPopup,
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .padding(AppleIconSpacing.Large),
            )
        }
    }
}

@Composable
private fun DashboardHeader(modifier: Modifier = Modifier) {
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
private fun StatusPanel(
    state: AirPodsDashboardUiState,
    actions: AirPodsDashboardActions,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppleIconSpacing.Medium)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppleIconSpacing.ExtraSmall),
            ) {
                Text(
                    text = state.statusTitle,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = state.statusDetail,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            StatusChip(
                text =
                    state.mode.name
                        .lowercase()
                        .replaceFirstChar(Char::uppercaseChar),
            )
        }
        if (state.isScanning) {
            AppleIconLoadingIndicator(label = "Scanning")
        }
        HorizontalDivider()
        PermissionChecklist(
            rows = state.permissionRows,
            actions = actions,
        )
        if (state.fallbackIssues.isNotEmpty()) {
            FallbackIssues(issues = state.fallbackIssues, actions = actions)
        }
    }
}

@Composable
private fun PermissionChecklist(
    rows: List<AirPodsPermissionRowUi>,
    actions: AirPodsDashboardActions,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppleIconSpacing.Medium)) {
        rows.forEach { row ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AppleIconSpacing.ExtraSmall),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = row.label,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )
                    StatusChip(text = row.status)
                }
                Text(
                    text = row.detail,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
                if (row.action != null && row.actionLabel != null) {
                    AppleIconButton(
                        label = row.actionLabel,
                        onClick = { actions.perform(row.action) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SnapshotPanel(
    state: AirPodsDashboardUiState,
    actions: AirPodsDashboardActions,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppleIconSpacing.Large)) {
        when (state.mode) {
            AirPodsDashboardMode.LOADING -> {
                AppleIconLoadingIndicator(label = "Scanning")
                Text(
                    text = "The latest AirPods battery status will appear here.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            AirPodsDashboardMode.EMPTY ->
                AppleIconEmptyState(
                    title = "No AirPods detected yet",
                    message = "Grant permissions, then open the case near this phone.",
                )

            AirPodsDashboardMode.ERROR ->
                state.primaryIssue?.let { issue ->
                    AppleIconErrorState(
                        title = issue.title,
                        message = issue.message,
                        actionLabel = issue.actionLabel,
                        onAction = issue.action?.let { action -> { actions.perform(action) } },
                    )
                }

            AirPodsDashboardMode.SUCCESS ->
                state.snapshot?.let { snapshot ->
                    SnapshotContent(snapshot = snapshot)
                }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(AppleIconSpacing.Medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppleIconButton(
                label = "Test Popup",
                onClick = actions.onTestPopup,
                enabled = state.settings.monitoringEnabled,
            )
            AppleIconButton(
                label = "Retry Scan",
                onClick = actions.onRetryScan,
                enabled = state.settings.monitoringEnabled,
            )
        }
    }
}

@Composable
private fun SnapshotContent(snapshot: AirPodsSnapshotUi) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AppleIconSpacing.Large),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppleIconSpacing.Large),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AirPodsVisual(
                modifier =
                    Modifier
                        .size(width = 112.dp, height = 72.dp)
                        .semantics {
                            contentDescription = "Original AirPods-inspired battery visual"
                        },
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppleIconSpacing.ExtraSmall),
            ) {
                Text(
                    text = snapshot.deviceName,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "${snapshot.modelLabel} • ${snapshot.connectionLabel}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "${snapshot.sourceLabel} • ${snapshot.lastSeenLabel}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        snapshot.staleLabel?.let { staleLabel ->
            Text(
                text = staleLabel,
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(AppleIconSpacing.Medium)) {
            snapshot.batteries.forEach { battery ->
                BatteryLevelRow(battery = battery)
            }
        }
    }
}

@Composable
private fun BatteryLevelRow(battery: AirPodsBatteryUi) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = battery.contentDescription
                }.testTag("battery_${battery.label.lowercase().replace(" ", "_")}"),
        verticalArrangement = Arrangement.spacedBy(AppleIconSpacing.ExtraSmall),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = battery.label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = battery.value,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.End,
            )
        }
        BatteryBar(progress = battery.progress)
        Text(
            text = battery.chargingLabel,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun BatteryBar(progress: Float?) {
    val clampedProgress = progress?.coerceIn(0f, 1f)
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(AppleIconShape.ControlRadius))
                .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        if (clampedProgress != null) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(clampedProgress)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.primary),
            )
        }
    }
}

@Composable
private fun FallbackIssues(
    issues: List<AirPodsIssueUi>,
    actions: AirPodsDashboardActions,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppleIconSpacing.Small)) {
        issues.forEach { issue ->
            Column(verticalArrangement = Arrangement.spacedBy(AppleIconSpacing.ExtraSmall)) {
                Text(
                    text = issue.title,
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = issue.message,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
                if (issue.action != null && issue.actionLabel != null) {
                    AppleIconButton(
                        label = issue.actionLabel,
                        onClick = { actions.perform(issue.action) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsPanel(
    state: AirPodsDashboardUiState,
    actions: AirPodsDashboardActions,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppleIconSpacing.Medium)) {
        SettingToggleRow(
            label = "Monitoring",
            checked = state.settings.monitoringEnabled,
            onCheckedChange = actions.onMonitoringChanged,
        )
        SettingToggleRow(
            label = "Automatic popup",
            checked = state.settings.popupEnabled,
            onCheckedChange = actions.onPopupChanged,
        )
        SettingToggleRow(
            label = "Fallback notification",
            checked = state.settings.notificationEnabled,
            onCheckedChange = actions.onNotificationChanged,
        )
    }
}

@Composable
private fun SettingToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AppleIconSpacing.ExtraSmall)) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = if (checked) "On" else "Off",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
private fun StatusChip(text: String) {
    val containerColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.secondaryContainer,
        label = "status_chip_color",
    )
    Surface(
        shape = RoundedCornerShape(AppleIconShape.ControlRadius),
        color = containerColor,
    ) {
        Text(
            text = text,
            modifier =
                Modifier.padding(
                    horizontal = AppleIconSpacing.Small,
                    vertical = AppleIconSpacing.ExtraSmall,
                ),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun AirPodsPopupOverlay(
    popup: AirPodsPopupUi,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier =
            modifier
                .widthIn(max = 420.dp)
                .testTag("airpods_popup"),
        shape = RoundedCornerShape(AppleIconShape.PanelRadius),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp,
    ) {
        Column(
            modifier = Modifier.padding(AppleIconSpacing.Large),
            verticalArrangement = Arrangement.spacedBy(AppleIconSpacing.Large),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AirPodsVisual(
                modifier =
                    Modifier
                        .size(width = 180.dp, height = 96.dp)
                        .semantics {
                            contentDescription = "Animated AirPods-inspired popup visual"
                        },
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(AppleIconSpacing.ExtraSmall),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = popup.snapshot.deviceName,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = popup.snapshot.lastSeenLabel,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(AppleIconSpacing.Small),
                modifier = Modifier.fillMaxWidth(),
            ) {
                popup.snapshot.batteries.forEach { battery ->
                    PopupBatteryRow(battery = battery)
                }
            }
            popup.fallbackMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                )
            }
            AppleIconButton(label = "Close", onClick = onDismiss)
        }
    }
}

@Composable
private fun PopupBatteryRow(battery: AirPodsBatteryUi) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = battery.contentDescription
                },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = battery.label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = battery.value,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun AirPodsVisual(modifier: Modifier = Modifier) {
    val motionEnabled = ValueAnimator.areAnimatorsEnabled()
    val pulse =
        if (motionEnabled) {
            val transition = rememberInfiniteTransition(label = "airpods_visual_pulse")
            val pulseValue by transition.animateFloat(
                initialValue = 0.18f,
                targetValue = 0.34f,
                animationSpec =
                    infiniteRepeatable(
                        animation = tween(durationMillis = 1100),
                        repeatMode = RepeatMode.Reverse,
                    ),
                label = "airpods_visual_pulse_alpha",
            )
            pulseValue
        } else {
            0.22f
        }
    val primary = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    Canvas(modifier = modifier.heightIn(min = AppleIconSize.TouchTarget)) {
        val width = size.width
        val height = size.height
        val budRadius = height * 0.24f
        val stemWidth = width * 0.08f
        val stemHeight = height * 0.5f
        val leftBud = Offset(width * 0.28f, height * 0.28f)
        val rightBud = Offset(width * 0.56f, height * 0.28f)

        drawRoundRect(
            color = primary.copy(alpha = pulse),
            topLeft = Offset(width * 0.12f, height * 0.08f),
            size = Size(width * 0.76f, height * 0.72f),
            cornerRadius = CornerRadius(height * 0.18f, height * 0.18f),
        )
        drawRoundRect(
            color = surfaceVariant,
            topLeft = Offset(width * 0.18f, height * 0.14f),
            size = Size(width * 0.64f, height * 0.58f),
            cornerRadius = CornerRadius(height * 0.16f, height * 0.16f),
        )
        drawCircle(
            color = Color.White,
            radius = budRadius,
            center = leftBud,
        )
        drawCircle(
            color = Color.White,
            radius = budRadius,
            center = rightBud,
        )
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(leftBud.x - stemWidth * 0.42f, leftBud.y + budRadius * 0.55f),
            size = Size(stemWidth, stemHeight),
            cornerRadius = CornerRadius(stemWidth, stemWidth),
        )
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(rightBud.x - stemWidth * 0.42f, rightBud.y + budRadius * 0.55f),
            size = Size(stemWidth, stemHeight),
            cornerRadius = CornerRadius(stemWidth, stemWidth),
        )
        drawCircle(
            color = onSurface.copy(alpha = 0.2f),
            radius = budRadius * 0.22f,
            center = Offset(leftBud.x + budRadius * 0.3f, leftBud.y - budRadius * 0.12f),
        )
        drawCircle(
            color = onSurface.copy(alpha = 0.2f),
            radius = budRadius * 0.22f,
            center = Offset(rightBud.x - budRadius * 0.3f, rightBud.y - budRadius * 0.12f),
        )
    }
}

private fun AirPodsDashboardActions.perform(action: AirPodsPermissionAction) {
    when (action) {
        AirPodsPermissionAction.REQUEST_BLUETOOTH_PERMISSION -> onRequestBluetoothPermission()
        AirPodsPermissionAction.OPEN_BLUETOOTH_SETTINGS -> onOpenBluetoothSettings()
        AirPodsPermissionAction.OPEN_OVERLAY_SETTINGS -> onOpenOverlaySettings()
        AirPodsPermissionAction.REQUEST_NOTIFICATION_PERMISSION -> onRequestNotificationPermission()
        AirPodsPermissionAction.RETRY_SCAN -> onRetryScan()
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun AirPodsDashboardMobilePreview() {
    AppleIconTheme {
        AirPodsDashboardScreen(
            state = previewState(),
            actions = AirPodsDashboardActions(),
        )
    }
}

@Preview(showBackground = true, widthDp = 900, heightDp = 700)
@Composable
private fun AirPodsDashboardWidePreview() {
    AppleIconTheme {
        AirPodsDashboardScreen(
            state = previewState(),
            actions = AirPodsDashboardActions(),
        )
    }
}

private fun previewState(): AirPodsDashboardUiState =
    AirPodsDashboardUiState(
        mode = AirPodsDashboardMode.SUCCESS,
        statusTitle = "Monitoring active",
        statusDetail = "Latest AirPods battery snapshot is available.",
        isScanning = false,
        settings =
            AirPodsSettingsUi(
                monitoringEnabled = true,
                popupEnabled = true,
                notificationEnabled = true,
            ),
        permissionRows =
            listOf(
                AirPodsPermissionRowUi(
                    label = "Bluetooth permission",
                    status = "Granted",
                    detail = "AirPods scan access is available.",
                ),
                AirPodsPermissionRowUi(
                    label = "Bluetooth",
                    status = "On",
                    detail = "Device Bluetooth is available.",
                ),
                AirPodsPermissionRowUi(
                    label = "Popup",
                    status = "Ready",
                    detail = "Popup permission is available.",
                ),
                AirPodsPermissionRowUi(
                    label = "Notifications",
                    status = "Ready",
                    detail = "Notification fallback can be used.",
                ),
            ),
        snapshot =
            AirPodsSnapshotUi(
                deviceName = "AirPods Pro",
                modelLabel = "AirPods Pro",
                connectionLabel = "Connected",
                sourceLabel = "Manual test",
                lastSeenLabel = "Last seen 2026-07-14 09:00 +10:00",
                staleLabel = null,
                batteries =
                    listOf(
                        AirPodsBatteryUi(
                            label = "Left earbud",
                            value = "82%",
                            progress = 0.82f,
                            chargingLabel = "Not charging",
                            contentDescription = "Left earbud battery 82 percent, Not charging",
                        ),
                        AirPodsBatteryUi(
                            label = "Right earbud",
                            value = "79%",
                            progress = 0.79f,
                            chargingLabel = "Not charging",
                            contentDescription = "Right earbud battery 79 percent, Not charging",
                        ),
                        AirPodsBatteryUi(
                            label = "Case",
                            value = "64%",
                            progress = 0.64f,
                            chargingLabel = "Charging",
                            contentDescription = "Case battery 64 percent, Charging",
                        ),
                    ),
            ),
        primaryIssue = null,
        fallbackIssues = emptyList(),
        popup = null,
    )
