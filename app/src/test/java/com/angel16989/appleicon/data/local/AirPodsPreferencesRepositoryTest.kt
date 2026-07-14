package com.angel16989.appleicon.data.local

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.angel16989.appleicon.data.model.AirPodsBatterySnapshot
import com.angel16989.appleicon.data.model.AirPodsConnectionState
import com.angel16989.appleicon.data.model.AirPodsModelHint
import com.angel16989.appleicon.data.model.AirPodsSettings
import com.angel16989.appleicon.data.model.AirPodsSnapshotSource
import com.angel16989.appleicon.data.model.stableAirPodsNotificationId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class AirPodsPreferencesRepositoryTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun storesAndRestoresSettingsAndSnapshot() =
        withRepository { repository ->
            val settings =
                AirPodsSettings(
                    monitoringEnabled = false,
                    overlayEnabled = false,
                    notificationEnabled = true,
                    notificationPermissionGranted = true,
                )
            val snapshot =
                AirPodsBatterySnapshot(
                    deviceId = "airpods_1234abcd5678",
                    displayName = "AirPods",
                    modelHint = AirPodsModelHint.AIRPODS,
                    connectionState = AirPodsConnectionState.DETECTED,
                    leftBatteryPercent = 40,
                    rightBatteryPercent = null,
                    caseBatteryPercent = 88,
                    leftCharging = false,
                    rightCharging = null,
                    caseCharging = true,
                    source = AirPodsSnapshotSource.MANUAL_TEST,
                    lastSeenAt = "2026-07-14T09:30:00+10:00",
                    isStale = false,
                )

            repository.saveSettings(settings)
            repository.saveSnapshot(snapshot)

            assertEquals(settings, repository.settings.first())
            assertEquals(snapshot, repository.snapshot.first())
        }

    @Test
    fun seedLocalDevelopmentDataPopulatesSnapshotAndNotificationState() =
        withRepository { repository ->
            repository.seedLocalDevelopmentData()

            val state = repository.localState.first()

            assertEquals(true, state.settings.monitoringEnabled)
            assertEquals("AirPods Pro", state.snapshot?.displayName)
            assertEquals(82, state.snapshot?.leftBatteryPercent)
            assertEquals(true, state.notificationState.notificationActive)
            assertEquals(
                stableAirPodsNotificationId("airpods_7f4a9c2d1b66"),
                state.notificationState.notificationId,
            )
            assertNotNull(state.notificationState.lastNotificationUpdateAt)
        }

    @Test
    fun clearAllResetsLocalStateButKeepsDefaults() =
        withRepository { repository ->
            repository.seedLocalDevelopmentData()
            repository.clearAll()

            val state = repository.localState.first()

            assertEquals(AirPodsSettings(), state.settings)
            assertNull(state.snapshot)
            assertFalse(state.notificationState.notificationActive)
        }

    @Test
    fun rejectsInvalidBatteryPercentage() {
        assertThrows(IllegalArgumentException::class.java) {
            AirPodsBatterySnapshot(
                deviceId = "airpods_1234abcd5678",
                displayName = "AirPods",
                modelHint = AirPodsModelHint.UNKNOWN,
                connectionState = AirPodsConnectionState.CONNECTED,
                leftBatteryPercent = 101,
                rightBatteryPercent = null,
                caseBatteryPercent = null,
                leftCharging = null,
                rightCharging = null,
                caseCharging = null,
                source = AirPodsSnapshotSource.MANUAL_TEST,
                lastSeenAt = "2026-07-14T09:30:00+10:00",
                isStale = false,
            )
        }
    }

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
}
