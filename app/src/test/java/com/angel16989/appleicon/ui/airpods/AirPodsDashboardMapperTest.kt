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
}
