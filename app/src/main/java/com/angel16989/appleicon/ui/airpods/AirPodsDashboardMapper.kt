package com.angel16989.appleicon.ui.airpods

import com.angel16989.appleicon.data.model.AirPodsBatterySnapshot
import com.angel16989.appleicon.data.model.AirPodsConnectionState
import com.angel16989.appleicon.data.model.AirPodsErrorEnvelope
import com.angel16989.appleicon.data.model.AirPodsLocalState
import com.angel16989.appleicon.data.model.AirPodsModelHint
import com.angel16989.appleicon.data.model.AirPodsMonitorErrorCode
import com.angel16989.appleicon.data.model.AirPodsMonitorPermissions
import com.angel16989.appleicon.data.model.AirPodsMonitorResult
import com.angel16989.appleicon.data.model.AirPodsSnapshotSource
import java.time.OffsetDateTime

data class AirPodsRuntimePermissionState(
    val bluetoothPermissionGranted: Boolean,
    val bluetoothAvailable: Boolean,
    val overlayPermissionGranted: Boolean,
    val notificationPermissionGranted: Boolean,
    val notificationPermissionRequired: Boolean,
    val scanThrottled: Boolean = false,
) {
    fun toMonitorPermissions(): AirPodsMonitorPermissions =
        AirPodsMonitorPermissions(
            bluetoothPermissionGranted = bluetoothPermissionGranted,
            bluetoothAvailable = bluetoothAvailable,
            overlayPermissionGranted = overlayPermissionGranted,
            notificationPermissionGranted = notificationPermissionGranted,
            scanThrottled = scanThrottled,
        )
}

fun mapAirPodsDashboardUiState(
    localState: AirPodsLocalState,
    monitorResult: AirPodsMonitorResult?,
    runtimePermissions: AirPodsRuntimePermissionState,
    isScanning: Boolean,
    popupResult: AirPodsMonitorResult.Snapshot?,
): AirPodsDashboardUiState {
    val snapshotResult = monitorResult as? AirPodsMonitorResult.Snapshot
    val fatalError = (monitorResult as? AirPodsMonitorResult.Failure)?.error
    val snapshot = snapshotResult?.snapshot ?: localState.snapshot
    val fallbackIssues = snapshotResult?.fallbackErrors.orEmpty().map(AirPodsErrorEnvelope::toIssueUi)

    val mode =
        when {
            fatalError != null -> AirPodsDashboardMode.ERROR
            snapshot != null -> AirPodsDashboardMode.SUCCESS
            isScanning && localState.settings.monitoringEnabled -> AirPodsDashboardMode.LOADING
            else -> AirPodsDashboardMode.EMPTY
        }

    return AirPodsDashboardUiState(
        mode = mode,
        statusTitle =
            statusTitle(
                mode = mode,
                monitoringEnabled = localState.settings.monitoringEnabled,
                snapshot = snapshot,
            ),
        statusDetail =
            statusDetail(
                mode = mode,
                monitoringEnabled = localState.settings.monitoringEnabled,
                snapshot = snapshot,
            ),
        isScanning = isScanning && localState.settings.monitoringEnabled,
        settings =
            AirPodsSettingsUi(
                monitoringEnabled = localState.settings.monitoringEnabled,
                popupEnabled = localState.settings.overlayEnabled,
                notificationEnabled = localState.settings.notificationEnabled,
            ),
        permissionRows =
            permissionRows(
                runtimePermissions = runtimePermissions,
                popupEnabled = localState.settings.overlayEnabled,
                notificationsEnabled = localState.settings.notificationEnabled,
            ),
        snapshot = snapshot?.toSnapshotUi(),
        primaryIssue = fatalError?.toIssueUi(),
        fallbackIssues = fallbackIssues,
        popup =
            popupResult?.let { result ->
                AirPodsPopupUi(
                    snapshot = result.snapshot.toSnapshotUi(),
                    fallbackMessage =
                        result.fallbackErrors
                            .firstOrNull()
                            ?.error
                            ?.message,
                )
            },
    )
}

private fun statusTitle(
    mode: AirPodsDashboardMode,
    monitoringEnabled: Boolean,
    snapshot: AirPodsBatterySnapshot?,
): String =
    when {
        !monitoringEnabled -> "Monitoring paused"
        mode == AirPodsDashboardMode.ERROR -> "Needs attention"
        mode == AirPodsDashboardMode.LOADING -> "Scanning"
        snapshot?.isStale == true -> "Last known status"
        mode == AirPodsDashboardMode.SUCCESS -> "Monitoring active"
        else -> "Ready to scan"
    }

private fun statusDetail(
    mode: AirPodsDashboardMode,
    monitoringEnabled: Boolean,
    snapshot: AirPodsBatterySnapshot?,
): String =
    when {
        !monitoringEnabled -> "Enable monitoring to check for AirPods again."
        mode == AirPodsDashboardMode.ERROR -> "Resolve the item below before scanning can continue."
        mode == AirPodsDashboardMode.LOADING -> "Looking for nearby or connected AirPods."
        snapshot?.isStale == true -> "Showing cached battery data while scanning continues."
        mode == AirPodsDashboardMode.SUCCESS -> "Latest AirPods battery snapshot is available."
        else -> "Grant permissions, then open the case or use the test popup."
    }

private fun permissionRows(
    runtimePermissions: AirPodsRuntimePermissionState,
    popupEnabled: Boolean,
    notificationsEnabled: Boolean,
): List<AirPodsPermissionRowUi> =
    listOf(
        if (runtimePermissions.bluetoothPermissionGranted) {
            AirPodsPermissionRowUi(
                label = "Bluetooth permission",
                status = "Granted",
                detail = "AirPods scan access is available.",
            )
        } else {
            AirPodsPermissionRowUi(
                label = "Bluetooth permission",
                status = "Missing",
                detail = "Required before monitoring can start.",
                actionLabel = "Grant",
                action = AirPodsPermissionAction.REQUEST_BLUETOOTH_PERMISSION,
            )
        },
        if (runtimePermissions.bluetoothAvailable) {
            AirPodsPermissionRowUi(
                label = "Bluetooth",
                status = "On",
                detail = "Device Bluetooth is available.",
            )
        } else {
            AirPodsPermissionRowUi(
                label = "Bluetooth",
                status = "Off",
                detail = "Turn on Bluetooth to detect AirPods.",
                actionLabel = "Open",
                action = AirPodsPermissionAction.OPEN_BLUETOOTH_SETTINGS,
            )
        },
        when {
            !popupEnabled ->
                AirPodsPermissionRowUi(
                    label = "Popup",
                    status = "Off",
                    detail = "Automatic popups are disabled.",
                )

            runtimePermissions.overlayPermissionGranted ->
                AirPodsPermissionRowUi(
                    label = "Popup",
                    status = "Ready",
                    detail = "Popup permission is available.",
                )

            else ->
                AirPodsPermissionRowUi(
                    label = "Popup",
                    status = "Fallback",
                    detail = "Battery status will stay inside the app.",
                    actionLabel = "Open",
                    action = AirPodsPermissionAction.OPEN_OVERLAY_SETTINGS,
                )
        },
        when {
            !notificationsEnabled ->
                AirPodsPermissionRowUi(
                    label = "Notifications",
                    status = "Off",
                    detail = "Fallback notifications are disabled.",
                )

            !runtimePermissions.notificationPermissionRequired ||
                runtimePermissions.notificationPermissionGranted ->
                AirPodsPermissionRowUi(
                    label = "Notifications",
                    status = "Ready",
                    detail = "Notification fallback can be used.",
                )

            else ->
                AirPodsPermissionRowUi(
                    label = "Notifications",
                    status = "Missing",
                    detail = "Grant permission for fallback alerts.",
                    actionLabel = "Grant",
                    action = AirPodsPermissionAction.REQUEST_NOTIFICATION_PERMISSION,
                )
        },
    )

private fun AirPodsErrorEnvelope.toIssueUi(): AirPodsIssueUi =
    when (error.code) {
        AirPodsMonitorErrorCode.BLUETOOTH_PERMISSION_DENIED ->
            AirPodsIssueUi(
                title = "Bluetooth permission needed",
                message = error.message,
                actionLabel = "Grant Bluetooth",
                action = AirPodsPermissionAction.REQUEST_BLUETOOTH_PERMISSION,
            )

        AirPodsMonitorErrorCode.BLUETOOTH_OFF ->
            AirPodsIssueUi(
                title = "Bluetooth is off",
                message = error.message,
                actionLabel = "Open Bluetooth",
                action = AirPodsPermissionAction.OPEN_BLUETOOTH_SETTINGS,
            )

        AirPodsMonitorErrorCode.OVERLAY_UNAVAILABLE ->
            AirPodsIssueUi(
                title = "Popup fallback active",
                message = error.message,
                actionLabel = "Open Popup Settings",
                action = AirPodsPermissionAction.OPEN_OVERLAY_SETTINGS,
            )

        AirPodsMonitorErrorCode.BATTERY_UNAVAILABLE ->
            AirPodsIssueUi(
                title = "Battery unavailable",
                message = error.message,
                actionLabel = "Retry scan",
                action = AirPodsPermissionAction.RETRY_SCAN,
            )

        AirPodsMonitorErrorCode.STALE_DATA ->
            AirPodsIssueUi(
                title = "Last known battery",
                message = error.message,
                actionLabel = "Retry scan",
                action = AirPodsPermissionAction.RETRY_SCAN,
            )

        AirPodsMonitorErrorCode.SCAN_THROTTLED ->
            AirPodsIssueUi(
                title = "Scan waiting",
                message = error.message,
                actionLabel = "Retry",
                action = AirPodsPermissionAction.RETRY_SCAN,
            )
    }

private fun AirPodsBatterySnapshot.toSnapshotUi(): AirPodsSnapshotUi =
    AirPodsSnapshotUi(
        deviceName = displayName,
        modelLabel = modelHint.toDisplayLabel(),
        connectionLabel = connectionState.toDisplayLabel(),
        sourceLabel = source.toDisplayLabel(),
        lastSeenLabel = "Last seen ${lastSeenAt.toDisplayTimestamp()}",
        staleLabel = if (isStale) "Showing last known battery from earlier." else null,
        batteries =
            listOf(
                batteryUi("Left earbud", leftBatteryPercent, leftCharging),
                batteryUi("Right earbud", rightBatteryPercent, rightCharging),
                batteryUi("Case", caseBatteryPercent, caseCharging),
            ),
    )

private fun batteryUi(
    label: String,
    percent: Int?,
    charging: Boolean?,
): AirPodsBatteryUi {
    val value = percent?.let { "$it%" } ?: "Unknown"
    val chargingLabel =
        when (charging) {
            true -> "Charging"
            false -> "Not charging"
            null -> "Charging unknown"
        }
    val batteryDescription =
        percent?.let { "$it percent" } ?: "unknown"
    return AirPodsBatteryUi(
        label = label,
        value = value,
        progress = percent?.let { it / 100f },
        chargingLabel = chargingLabel,
        contentDescription = "$label battery $batteryDescription, $chargingLabel",
    )
}

private fun AirPodsModelHint?.toDisplayLabel(): String =
    when (this) {
        AirPodsModelHint.AIRPODS -> "AirPods"
        AirPodsModelHint.AIRPODS_PRO -> "AirPods Pro"
        AirPodsModelHint.AIRPODS_MAX -> "AirPods Max"
        AirPodsModelHint.UNKNOWN, null -> "AirPods"
    }

private fun AirPodsConnectionState.toDisplayLabel(): String =
    when (this) {
        AirPodsConnectionState.SCANNING -> "Scanning"
        AirPodsConnectionState.DETECTED -> "Detected"
        AirPodsConnectionState.CONNECTED -> "Connected"
        AirPodsConnectionState.DISCONNECTED -> "Disconnected"
        AirPodsConnectionState.PERMISSION_BLOCKED -> "Permission blocked"
    }

private fun AirPodsSnapshotSource.toDisplayLabel(): String =
    when (this) {
        AirPodsSnapshotSource.BLE_ADVERTISEMENT -> "BLE signal"
        AirPodsSnapshotSource.BLUETOOTH_CONNECTION -> "Bluetooth connection"
        AirPodsSnapshotSource.CACHED -> "Cached"
        AirPodsSnapshotSource.MANUAL_TEST -> "Manual test"
    }

private fun String.toDisplayTimestamp(): String =
    runCatching {
        val timestamp = OffsetDateTime.parse(this)
        val date =
            listOf(
                timestamp.year.toString().padStart(4, '0'),
                timestamp.monthValue.toString().padStart(2, '0'),
                timestamp.dayOfMonth.toString().padStart(2, '0'),
            ).joinToString("-")
        val time =
            listOf(
                timestamp.hour.toString().padStart(2, '0'),
                timestamp.minute.toString().padStart(2, '0'),
            ).joinToString(":")
        "$date $time ${timestamp.offset}"
    }.getOrDefault(this)
