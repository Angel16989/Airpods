package com.angel16989.appleicon.ui.airpods

enum class AirPodsDashboardMode {
    LOADING,
    EMPTY,
    ERROR,
    SUCCESS,
}

enum class AirPodsPermissionAction {
    REQUEST_BLUETOOTH_PERMISSION,
    OPEN_BLUETOOTH_SETTINGS,
    OPEN_OVERLAY_SETTINGS,
    REQUEST_NOTIFICATION_PERMISSION,
    RETRY_SCAN,
}

data class AirPodsDashboardUiState(
    val mode: AirPodsDashboardMode,
    val statusTitle: String,
    val statusDetail: String,
    val isScanning: Boolean,
    val settings: AirPodsSettingsUi,
    val permissionRows: List<AirPodsPermissionRowUi>,
    val snapshot: AirPodsSnapshotUi?,
    val primaryIssue: AirPodsIssueUi?,
    val fallbackIssues: List<AirPodsIssueUi>,
    val popup: AirPodsPopupUi?,
)

data class AirPodsSettingsUi(
    val monitoringEnabled: Boolean,
    val popupEnabled: Boolean,
    val notificationEnabled: Boolean,
)

data class AirPodsPermissionRowUi(
    val label: String,
    val status: String,
    val detail: String,
    val actionLabel: String? = null,
    val action: AirPodsPermissionAction? = null,
)

data class AirPodsIssueUi(
    val title: String,
    val message: String,
    val actionLabel: String? = null,
    val action: AirPodsPermissionAction? = null,
)

data class AirPodsSnapshotUi(
    val deviceName: String,
    val modelLabel: String,
    val connectionLabel: String,
    val sourceLabel: String,
    val lastSeenLabel: String,
    val staleLabel: String?,
    val batteries: List<AirPodsBatteryUi>,
)

data class AirPodsBatteryUi(
    val label: String,
    val value: String,
    val progress: Float?,
    val chargingLabel: String,
    val contentDescription: String,
)

data class AirPodsPopupUi(
    val snapshot: AirPodsSnapshotUi,
    val fallbackMessage: String?,
)

data class AirPodsDashboardActions(
    val onMonitoringChanged: (Boolean) -> Unit = {},
    val onPopupChanged: (Boolean) -> Unit = {},
    val onNotificationChanged: (Boolean) -> Unit = {},
    val onRequestBluetoothPermission: () -> Unit = {},
    val onOpenBluetoothSettings: () -> Unit = {},
    val onOpenOverlaySettings: () -> Unit = {},
    val onRequestNotificationPermission: () -> Unit = {},
    val onRetryScan: () -> Unit = {},
    val onTestPopup: () -> Unit = {},
    val onDismissPopup: () -> Unit = {},
)
