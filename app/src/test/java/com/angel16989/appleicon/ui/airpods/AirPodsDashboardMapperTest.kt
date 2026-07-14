package com.angel16989.appleicon.ui.airpods

import com.angel16989.appleicon.data.model.AirPodsBatterySnapshot
import com.angel16989.appleicon.data.model.AirPodsConnectionState
import com.angel16989.appleicon.data.model.AirPodsErrorEnvelope
import com.angel16989.appleicon.data.model.AirPodsLocalState
import com.angel16989.appleicon.data.model.AirPodsModelHint
import com.angel16989.appleicon.data.model.AirPodsMonitorError
import com.angel16989.appleicon.data.model.AirPodsMonitorErrorCode
import com.angel16989.appleicon.data.model.AirPodsMonitorResult
import com.angel16989.appleicon.data.model.AirPodsNotificationState
import com.angel16989.appleicon.data.model.AirPodsSettings
import com.angel16989.appleicon.data.model.AirPodsSnapshotSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class AirPodsDashboardMapperTest {
    @Test
    fun mapsScanningWithoutSnapshotToLoadingState() {
        val state =
            mapAirPodsDashboardUiState(
                localState = localState(),
                monitorResult = null,
                runtimePermissions = grantedRuntimePermissions(),
                isScanning = true,
                popupResult = null,
            )

        assertEquals(AirPodsDashboardMode.LOADING, state.mode)
        assertEquals("Scanning", state.statusTitle)
        assertEquals(true, state.isScanning)
        assertNull(state.snapshot)
    }

    @Test
    fun mapsPermissionFailureToErrorStateAndBluetoothAction() {
        val state =
            mapAirPodsDashboardUiState(
                localState = localState(),
                monitorResult =
                    AirPodsMonitorResult.Failure(
                        error =
                            errorEnvelope(
                                code = AirPodsMonitorErrorCode.BLUETOOTH_PERMISSION_DENIED,
                                message = "Apple Icon needs Bluetooth permission to detect AirPods.",
                            ),
                    ),
                runtimePermissions = grantedRuntimePermissions().copy(bluetoothPermissionGranted = false),
                isScanning = false,
                popupResult = null,
            )

        assertEquals(AirPodsDashboardMode.ERROR, state.mode)
        assertEquals("Bluetooth permission needed", state.primaryIssue?.title)
        assertEquals(
            AirPodsPermissionAction.REQUEST_BLUETOOTH_PERMISSION,
            state.primaryIssue?.action,
        )
        assertEquals("Missing", state.permissionRows.first().status)
    }

    @Test
    fun mapsEverySpecErrorCaseToUserMessageAndRecoveryAction() {
        val cases =
            listOf(
                ErrorUiCase(
                    code = AirPodsMonitorErrorCode.BLUETOOTH_OFF,
                    message = "Turn on Bluetooth to detect your AirPods.",
                    title = "Bluetooth is off",
                    actionLabel = "Open Bluetooth",
                    action = AirPodsPermissionAction.OPEN_BLUETOOTH_SETTINGS,
                    fallback = false,
                ),
                ErrorUiCase(
                    code = AirPodsMonitorErrorCode.BLUETOOTH_PERMISSION_DENIED,
                    message = "Apple Icon needs Bluetooth permission to detect AirPods.",
                    title = "Bluetooth permission needed",
                    actionLabel = "Grant Bluetooth",
                    action = AirPodsPermissionAction.REQUEST_BLUETOOTH_PERMISSION,
                    fallback = false,
                ),
                ErrorUiCase(
                    code = AirPodsMonitorErrorCode.OVERLAY_UNAVAILABLE,
                    message = "Popup permission is off, so battery status will appear inside the app.",
                    title = "Popup fallback active",
                    actionLabel = "Open Popup Settings",
                    action = AirPodsPermissionAction.OPEN_OVERLAY_SETTINGS,
                    fallback = true,
                ),
                ErrorUiCase(
                    code = AirPodsMonitorErrorCode.BATTERY_UNAVAILABLE,
                    message = "AirPods detected, but battery level is unavailable right now.",
                    title = "Battery unavailable",
                    actionLabel = "Retry scan",
                    action = AirPodsPermissionAction.RETRY_SCAN,
                    fallback = true,
                ),
                ErrorUiCase(
                    code = AirPodsMonitorErrorCode.STALE_DATA,
                    message = "Showing last known battery from earlier.",
                    title = "Last known battery",
                    actionLabel = "Retry scan",
                    action = AirPodsPermissionAction.RETRY_SCAN,
                    fallback = true,
                ),
                ErrorUiCase(
                    code = AirPodsMonitorErrorCode.SCAN_THROTTLED,
                    message = "Waiting for Android to allow the next scan.",
                    title = "Scan waiting",
                    actionLabel = "Retry",
                    action = AirPodsPermissionAction.RETRY_SCAN,
                    fallback = false,
                ),
            )

        cases.forEach { errorCase ->
            val monitorResult: AirPodsMonitorResult =
                if (errorCase.fallback) {
                    AirPodsMonitorResult.Snapshot(
                        snapshot = snapshot(rightBatteryPercent = null),
                        popupShouldShow = false,
                        fallbackErrors =
                            listOf(
                                errorEnvelope(
                                    code = errorCase.code,
                                    message = errorCase.message,
                                ),
                            ),
                    )
                } else {
                    AirPodsMonitorResult.Failure(
                        error =
                            errorEnvelope(
                                code = errorCase.code,
                                message = errorCase.message,
                            ),
                    )
                }

            val state =
                mapAirPodsDashboardUiState(
                    localState = localState(snapshot = snapshot(rightBatteryPercent = null)),
                    monitorResult = monitorResult,
                    runtimePermissions = grantedRuntimePermissions(),
                    isScanning = false,
                    popupResult = null,
                )
            val issue =
                if (errorCase.fallback) {
                    state.fallbackIssues.single()
                } else {
                    requireNotNull(state.primaryIssue)
                }

            assertEquals(errorCase.title, issue.title)
            assertEquals(errorCase.message, issue.message)
            assertEquals(errorCase.actionLabel, issue.actionLabel)
            assertEquals(errorCase.action, issue.action)
            assertEquals(
                if (errorCase.fallback) AirPodsDashboardMode.SUCCESS else AirPodsDashboardMode.ERROR,
                state.mode,
            )
        }
    }

    @Test
    fun mapsSnapshotUnknownBatteryFallbackAndPopupState() {
        val snapshotResult =
            AirPodsMonitorResult.Snapshot(
                snapshot = snapshot(rightBatteryPercent = null),
                popupShouldShow = true,
                fallbackErrors =
                    listOf(
                        errorEnvelope(
                            code = AirPodsMonitorErrorCode.OVERLAY_UNAVAILABLE,
                            message = "Popup permission is off, so battery status will appear inside the app.",
                        ),
                    ),
            )

        val state =
            mapAirPodsDashboardUiState(
                localState = localState(snapshot = snapshotResult.snapshot),
                monitorResult = snapshotResult,
                runtimePermissions = grantedRuntimePermissions().copy(overlayPermissionGranted = false),
                isScanning = false,
                popupResult = snapshotResult,
            )

        assertEquals(AirPodsDashboardMode.SUCCESS, state.mode)
        assertEquals("AirPods Pro", state.snapshot?.deviceName)
        assertEquals(
            "Unknown",
            state.snapshot
                ?.batteries
                ?.get(1)
                ?.value,
        )
        assertEquals("Popup fallback active", state.fallbackIssues.first().title)
        assertNotNull(state.popup)
    }

    private fun localState(snapshot: AirPodsBatterySnapshot? = null): AirPodsLocalState =
        AirPodsLocalState(
            settings = AirPodsSettings(),
            snapshot = snapshot,
            notificationState = AirPodsNotificationState(),
        )

    private fun grantedRuntimePermissions(): AirPodsRuntimePermissionState =
        AirPodsRuntimePermissionState(
            bluetoothPermissionGranted = true,
            bluetoothAvailable = true,
            overlayPermissionGranted = true,
            notificationPermissionGranted = true,
            notificationPermissionRequired = true,
        )

    private fun snapshot(rightBatteryPercent: Int?): AirPodsBatterySnapshot =
        AirPodsBatterySnapshot(
            deviceId = "airpods_1234abcd5678",
            displayName = "AirPods Pro",
            modelHint = AirPodsModelHint.AIRPODS_PRO,
            connectionState = AirPodsConnectionState.CONNECTED,
            leftBatteryPercent = 82,
            rightBatteryPercent = rightBatteryPercent,
            caseBatteryPercent = 64,
            leftCharging = false,
            rightCharging = null,
            caseCharging = true,
            source = AirPodsSnapshotSource.MANUAL_TEST,
            lastSeenAt = "2026-07-14T09:00:00+10:00",
            isStale = false,
        )

    private fun errorEnvelope(
        code: AirPodsMonitorErrorCode,
        message: String,
    ): AirPodsErrorEnvelope =
        AirPodsErrorEnvelope(
            error =
                AirPodsMonitorError(
                    code = code,
                    message = message,
                    recoverable = true,
                    userAction = "test_action",
                ),
            occurredAt = "2026-07-14T09:00:00+10:00",
        )

    private data class ErrorUiCase(
        val code: AirPodsMonitorErrorCode,
        val message: String,
        val title: String,
        val actionLabel: String,
        val action: AirPodsPermissionAction,
        val fallback: Boolean,
    )
}
