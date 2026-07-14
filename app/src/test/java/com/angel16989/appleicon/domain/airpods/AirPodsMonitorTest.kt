package com.angel16989.appleicon.domain.airpods

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.angel16989.appleicon.data.local.AirPodsPreferencesRepository
import com.angel16989.appleicon.data.model.AirPodsBluetoothPayload
import com.angel16989.appleicon.data.model.AirPodsConnectionState
import com.angel16989.appleicon.data.model.AirPodsMonitorErrorCode
import com.angel16989.appleicon.data.model.AirPodsMonitorPermissions
import com.angel16989.appleicon.data.model.AirPodsMonitorRequest
import com.angel16989.appleicon.data.model.AirPodsMonitorResult
import com.angel16989.appleicon.data.model.AirPodsSnapshotSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.time.OffsetDateTime

class AirPodsMonitorTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun observeSnapshotsEmitsSeededDataStoreSnapshot() =
        withRepository { repository ->
            repository.seedLocalDevelopmentData(timestamp = "2026-07-14T09:00:00+10:00")
            val result =
                monitor(repository, now = "2026-07-14T09:00:05+10:00")
                    .observeSnapshots(AirPodsMonitorRequest(), grantedPermissions)
                    .first() as AirPodsMonitorResult.Snapshot

            assertEquals("AirPods Pro", result.snapshot.displayName)
            assertEquals(82, result.snapshot.leftBatteryPercent)
            assertTrue(result.popupShouldShow)
            assertTrue(result.fallbackErrors.isEmpty())
        }

    @Test
    fun observeSnapshotsSavesSignalPayloadAndEmitsSnapshot() =
        withRepository { repository ->
            val debugEventLogger = RecordingAirPodsDebugEventLogger()
            val payload =
                AirPodsBluetoothPayload(
                    bluetoothIdentity = "connected-case-one",
                    displayName = "AirPods Pro",
                    connectionState = AirPodsConnectionState.CONNECTED,
                    leftBatteryPercent = 72,
                    rightBatteryPercent = 70,
                    caseBatteryPercent = 91,
                    source = AirPodsSnapshotSource.BLE_ADVERTISEMENT,
                    observedAt = "2026-07-14T09:00:00+10:00",
                )

            val result =
                monitor(
                    repository = repository,
                    signals = AirPodsSignalSource { flowOf(payload) },
                    debugEventLogger = debugEventLogger,
                    now = "2026-07-14T09:00:05+10:00",
                ).observeSnapshots(AirPodsMonitorRequest(), grantedPermissions)
                    .first() as AirPodsMonitorResult.Snapshot

            val storedSnapshot = repository.snapshot.first()
            assertEquals("AirPods Pro", result.snapshot.displayName)
            assertEquals(72, storedSnapshot?.leftBatteryPercent)
            assertEquals(91, storedSnapshot?.caseBatteryPercent)
            assertTrue(result.popupShouldShow)
            assertEquals(
                listOf(
                    AirPodsDebugEventName.AIRPODS_DETECTED,
                    AirPodsDebugEventName.BATTERY_POPUP_SHOWN,
                ),
                debugEventLogger.events.map(AirPodsDebugEvent::name),
            )
            assertEquals("airpods_pro", debugEventLogger.events.first().properties["model_hint"])
            assertEquals("ble_advertisement", debugEventLogger.events.first().properties["source"])
            assertEquals("true", debugEventLogger.events.first().properties["has_left"])
        }

    @Test
    fun bluetoothPermissionDeniedReturnsErrorEnvelope() =
        withRepository { repository ->
            val debugEventLogger = RecordingAirPodsDebugEventLogger()
            val result =
                monitor(repository, debugEventLogger = debugEventLogger)
                    .observeSnapshots(
                        request = AirPodsMonitorRequest(),
                        permissions =
                            grantedPermissions.copy(
                                bluetoothPermissionGranted = false,
                            ),
                    ).first() as AirPodsMonitorResult.Failure

            assertFalse(result.error.ok)
            assertEquals(AirPodsMonitorErrorCode.BLUETOOTH_PERMISSION_DENIED, result.error.error.code)
            assertEquals("open_bluetooth_permission_settings", result.error.error.userAction)
            assertEquals(AirPodsDebugEventName.PERMISSION_BLOCKED, debugEventLogger.events.single().name)
            assertEquals("bluetooth", debugEventLogger.events.single().properties["permission_type"])
        }

    @Test
    fun bluetoothOffReturnsErrorEnvelope() =
        withRepository { repository ->
            val result =
                monitor(repository)
                    .observeSnapshots(
                        request = AirPodsMonitorRequest(),
                        permissions =
                            grantedPermissions.copy(
                                bluetoothAvailable = false,
                            ),
                    ).first() as AirPodsMonitorResult.Failure

            assertEquals(AirPodsMonitorErrorCode.BLUETOOTH_OFF, result.error.error.code)
            assertEquals("open_bluetooth_settings", result.error.error.userAction)
        }

    @Test
    fun scanThrottledReturnsErrorEnvelope() =
        withRepository { repository ->
            val result =
                monitor(repository)
                    .observeSnapshots(
                        request = AirPodsMonitorRequest(),
                        permissions =
                            grantedPermissions.copy(
                                scanThrottled = true,
                            ),
                    ).first() as AirPodsMonitorResult.Failure

            assertEquals(AirPodsMonitorErrorCode.SCAN_THROTTLED, result.error.error.code)
            assertEquals("wait_for_scan_window", result.error.error.userAction)
        }

    @Test
    fun overlayUnavailableReturnsSnapshotFallbackAndSuppressesPopup() =
        withRepository { repository ->
            val debugEventLogger = RecordingAirPodsDebugEventLogger()
            repository.seedLocalDevelopmentData(timestamp = "2026-07-14T09:00:00+10:00")

            val result =
                monitor(
                    repository = repository,
                    debugEventLogger = debugEventLogger,
                    now = "2026-07-14T09:00:05+10:00",
                ).observeSnapshots(
                    request = AirPodsMonitorRequest(),
                    permissions =
                        grantedPermissions.copy(
                            overlayPermissionGranted = false,
                            notificationPermissionGranted = true,
                        ),
                ).first() as AirPodsMonitorResult.Snapshot

            assertFalse(result.popupShouldShow)
            assertEquals(
                AirPodsMonitorErrorCode.OVERLAY_UNAVAILABLE,
                result.fallbackErrors
                    .single()
                    .error.code,
            )
            assertEquals(
                "true",
                result.fallbackErrors
                    .single()
                    .error.details
                    .getValue("notification_available"),
            )
            assertEquals(AirPodsDebugEventName.POPUP_FALLBACK_USED, debugEventLogger.events.single().name)
            assertEquals("overlay_unavailable", debugEventLogger.events.single().properties["reason"])
            assertEquals("true", debugEventLogger.events.single().properties["notification_available"])
        }

    @Test
    fun missingBatteryValuesReturnSnapshotFallback() =
        withRepository { repository ->
            val payload =
                AirPodsBluetoothPayload(
                    bluetoothIdentity = "battery-missing-signal",
                    displayName = "AirPods",
                    observedAt = "2026-07-14T09:00:00+10:00",
                )

            val result =
                monitor(
                    repository = repository,
                    signals = AirPodsSignalSource { flowOf(payload) },
                    now = "2026-07-14T09:00:05+10:00",
                ).observeSnapshots(AirPodsMonitorRequest(), grantedPermissions)
                    .first() as AirPodsMonitorResult.Snapshot

            assertTrue(result.popupShouldShow)
            assertEquals(
                AirPodsMonitorErrorCode.BATTERY_UNAVAILABLE,
                result.fallbackErrors
                    .single()
                    .error.code,
            )
        }

    @Test
    fun malformedBatteryValuesReturnFailureEnvelope() =
        withRepository { repository ->
            val payload =
                AirPodsBluetoothPayload(
                    bluetoothIdentity = "bad-battery-signal",
                    leftBatteryPercent = -1,
                    observedAt = "2026-07-14T09:00:00+10:00",
                )

            val result =
                monitor(
                    repository = repository,
                    signals = AirPodsSignalSource { flowOf(payload) },
                    now = "2026-07-14T09:00:05+10:00",
                ).observeSnapshots(AirPodsMonitorRequest(), grantedPermissions)
                    .first() as AirPodsMonitorResult.Failure

            assertEquals(AirPodsMonitorErrorCode.BATTERY_UNAVAILABLE, result.error.error.code)
            assertTrue(
                result.error.error.details
                    .getValue("reason")
                    .contains("left_battery_percent"),
            )
        }

    @Test
    fun staleCachedSnapshotIsMarkedAndWarned() =
        withRepository { repository ->
            repository.seedLocalDevelopmentData(timestamp = "2026-07-14T09:00:00+10:00")

            val result =
                monitor(repository, now = "2026-07-14T09:05:00+10:00")
                    .observeSnapshots(
                        request = AirPodsMonitorRequest(freshnessWindowSeconds = 30),
                        permissions = grantedPermissions,
                    ).first() as AirPodsMonitorResult.Snapshot

            assertTrue(result.snapshot.isStale)
            assertFalse(result.popupShouldShow)
            assertEquals(
                AirPodsMonitorErrorCode.STALE_DATA,
                result.fallbackErrors
                    .single()
                    .error.code,
            )
        }

    @Test
    fun cooldownSuppressesUnchangedPopupState() =
        withRepository { repository ->
            repository.seedLocalDevelopmentData(timestamp = "2026-07-14T09:00:00+10:00")
            repository.markPopupShown(
                deviceId = "airpods_7f4a9c2d1b66",
                shownAt = "2026-07-14T09:00:00+10:00",
            )

            val result =
                monitor(repository, now = "2026-07-14T09:00:10+10:00")
                    .observeSnapshots(
                        request = AirPodsMonitorRequest(cooldownSeconds = 30),
                        permissions = grantedPermissions,
                    ).first() as AirPodsMonitorResult.Snapshot

            assertFalse(result.popupShouldShow)
        }

    @Test
    fun requestRejectsNegativeCooldown() {
        assertThrows(IllegalArgumentException::class.java) {
            AirPodsMonitorRequest(cooldownSeconds = -1)
        }
    }

    private fun monitor(
        repository: AirPodsPreferencesRepository,
        signals: AirPodsSignalSource = AirPodsSignalSource { emptyFlow() },
        debugEventLogger: AirPodsDebugEventLogger = NoOpAirPodsDebugEventLogger,
        now: String = "2026-07-14T09:00:00+10:00",
    ): AirPodsMonitor =
        AirPodsMonitor(
            repository = repository,
            signalSource = signals,
            parser = AirPodsPayloadParser(deviceIdSalt = "test-salt"),
            debugEventLogger = debugEventLogger,
            now = { OffsetDateTime.parse(now) },
        )

    private fun withRepository(block: suspend (AirPodsPreferencesRepository) -> Unit) {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val dataFile = File(temporaryFolder.root, "apple_icon.preferences_pb")
        val dataStore =
            PreferenceDataStoreFactory.create(
                scope = scope,
                produceFile = { dataFile },
            )
        val repository = AirPodsPreferencesRepository(dataStore)

        try {
            runBlocking {
                block(repository)
            }
        } finally {
            scope.cancel()
        }
    }

    private companion object {
        val grantedPermissions =
            AirPodsMonitorPermissions(
                bluetoothPermissionGranted = true,
                bluetoothAvailable = true,
                overlayPermissionGranted = true,
                notificationPermissionGranted = true,
            )
    }

    private class RecordingAirPodsDebugEventLogger : AirPodsDebugEventLogger {
        val events = mutableListOf<AirPodsDebugEvent>()

        override fun log(event: AirPodsDebugEvent) {
            events += event
        }
    }
}
