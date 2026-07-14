package com.angel16989.appleicon.domain.airpods

import com.angel16989.appleicon.data.model.AirPodsBluetoothPayload
import com.angel16989.appleicon.data.model.AirPodsConnectionState
import com.angel16989.appleicon.data.model.AirPodsModelHint
import com.angel16989.appleicon.data.model.AirPodsMonitorErrorCode
import com.angel16989.appleicon.data.model.AirPodsSnapshotSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AirPodsPayloadParserTest {
    private val parser = AirPodsPayloadParser(deviceIdSalt = "test-salt")

    @Test
    fun parsesCompletePayloadIntoValidatedSnapshot() {
        val result =
            parser.parse(
                AirPodsBluetoothPayload(
                    bluetoothIdentity = "AA:BB:CC:DD:EE:FF",
                    displayName = "AirPods Pro",
                    connectionState = AirPodsConnectionState.CONNECTED,
                    leftBatteryPercent = 82,
                    rightBatteryPercent = 79,
                    caseBatteryPercent = 64,
                    leftCharging = false,
                    rightCharging = false,
                    caseCharging = true,
                    source = AirPodsSnapshotSource.BLE_ADVERTISEMENT,
                    observedAt = "2026-07-14T09:00:00+10:00",
                ),
            ) as AirPodsPayloadParseResult.Success

        assertTrue(result.snapshot.deviceId.matches(Regex("^airpods_[0-9a-f]{12}$")))
        assertFalse(result.snapshot.deviceId.contains("AA"))
        assertEquals("AirPods Pro", result.snapshot.displayName)
        assertEquals(AirPodsModelHint.AIRPODS_PRO, result.snapshot.modelHint)
        assertEquals(82, result.snapshot.leftBatteryPercent)
        assertEquals(false, result.batteryUnavailable)
    }

    @Test
    fun preservesUnknownBatteryValuesAsNull() {
        val result =
            parser.parse(
                AirPodsBluetoothPayload(
                    bluetoothIdentity = "case-open-signal",
                    displayName = null,
                    observedAt = "2026-07-14T09:00:00+10:00",
                ),
            ) as AirPodsPayloadParseResult.Success

        assertEquals("AirPods", result.snapshot.displayName)
        assertEquals(AirPodsModelHint.AIRPODS, result.snapshot.modelHint)
        assertNull(result.snapshot.leftBatteryPercent)
        assertNull(result.snapshot.rightBatteryPercent)
        assertNull(result.snapshot.caseBatteryPercent)
        assertTrue(result.batteryUnavailable)
    }

    @Test
    fun malformedBatteryPercentReturnsConventionalError() {
        val result =
            parser.parse(
                AirPodsBluetoothPayload(
                    bluetoothIdentity = "case-open-signal",
                    leftBatteryPercent = 101,
                    observedAt = "2026-07-14T09:00:00+10:00",
                ),
            ) as AirPodsPayloadParseResult.Failure

        assertFalse(result.error.ok)
        assertEquals(AirPodsMonitorErrorCode.BATTERY_UNAVAILABLE, result.error.error.code)
        assertEquals("retry_bluetooth_scan", result.error.error.userAction)
        assertTrue(
            result.error.error.details
                .getValue("reason")
                .contains("left_battery_percent"),
        )
    }

    @Test
    fun missingBluetoothIdentityReturnsConventionalError() {
        val result =
            parser.parse(
                AirPodsBluetoothPayload(
                    bluetoothIdentity = " ",
                    observedAt = "2026-07-14T09:00:00+10:00",
                ),
            ) as AirPodsPayloadParseResult.Failure

        assertEquals(AirPodsMonitorErrorCode.BATTERY_UNAVAILABLE, result.error.error.code)
        assertEquals(
            "missing_bluetooth_identity",
            result.error.error.details
                .getValue("reason"),
        )
    }
}
