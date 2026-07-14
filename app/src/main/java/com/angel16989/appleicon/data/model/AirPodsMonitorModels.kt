package com.angel16989.appleicon.data.model

import java.time.OffsetDateTime

enum class AirPodsScanReason(
    val serializedName: String,
) {
    APP_START_OR_BLUETOOTH_EVENT("app_start_or_bluetooth_event"),
    APP_FOREGROUND("app_foreground"),
    BLUETOOTH_EVENT("bluetooth_event"),
    MANUAL_TEST("manual_test"),
    ;

    companion object {
        fun fromSerializedName(value: String): AirPodsScanReason? = entries.firstOrNull { it.serializedName == value }
    }
}

data class AirPodsMonitorRequest(
    val monitoringEnabled: Boolean = true,
    val overlayEnabled: Boolean = true,
    val scanReason: AirPodsScanReason = AirPodsScanReason.APP_START_OR_BLUETOOTH_EVENT,
    val cooldownSeconds: Long = 30,
    val freshnessWindowSeconds: Long = 120,
) {
    init {
        require(cooldownSeconds >= 0) {
            "cooldownSeconds must be zero or greater."
        }
        require(freshnessWindowSeconds >= 0) {
            "freshnessWindowSeconds must be zero or greater."
        }
    }
}

data class AirPodsMonitorPermissions(
    val bluetoothPermissionGranted: Boolean,
    val bluetoothAvailable: Boolean,
    val overlayPermissionGranted: Boolean = true,
    val notificationPermissionGranted: Boolean = false,
    val scanThrottled: Boolean = false,
)

enum class AirPodsMonitorErrorCode(
    val serializedName: String,
) {
    BLUETOOTH_OFF("BLUETOOTH_OFF"),
    BLUETOOTH_PERMISSION_DENIED("BLUETOOTH_PERMISSION_DENIED"),
    OVERLAY_UNAVAILABLE("OVERLAY_UNAVAILABLE"),
    BATTERY_UNAVAILABLE("BATTERY_UNAVAILABLE"),
    STALE_DATA("STALE_DATA"),
    SCAN_THROTTLED("SCAN_THROTTLED"),
}

data class AirPodsMonitorError(
    val code: AirPodsMonitorErrorCode,
    val message: String,
    val recoverable: Boolean,
    val userAction: String,
    val details: Map<String, String> = emptyMap(),
)

data class AirPodsErrorEnvelope(
    val ok: Boolean = false,
    val error: AirPodsMonitorError,
    val occurredAt: String,
) {
    init {
        require(!ok) {
            "AirPodsErrorEnvelope is only used for failed or degraded local monitor states."
        }
        requireOffsetTimestamp(occurredAt, "occurredAt")
    }
}

sealed interface AirPodsMonitorResult {
    data class Snapshot(
        val snapshot: AirPodsBatterySnapshot,
        val popupShouldShow: Boolean,
        val fallbackErrors: List<AirPodsErrorEnvelope> = emptyList(),
    ) : AirPodsMonitorResult

    data class Failure(
        val error: AirPodsErrorEnvelope,
    ) : AirPodsMonitorResult
}

data class AirPodsBluetoothPayload(
    val bluetoothIdentity: String,
    val displayName: String? = null,
    val modelHint: AirPodsModelHint? = null,
    val connectionState: AirPodsConnectionState = AirPodsConnectionState.DETECTED,
    val leftBatteryPercent: Int? = null,
    val rightBatteryPercent: Int? = null,
    val caseBatteryPercent: Int? = null,
    val leftCharging: Boolean? = null,
    val rightCharging: Boolean? = null,
    val caseCharging: Boolean? = null,
    val source: AirPodsSnapshotSource = AirPodsSnapshotSource.BLE_ADVERTISEMENT,
    val observedAt: String,
)

private fun requireOffsetTimestamp(
    value: String,
    fieldName: String,
) {
    require(runCatching { OffsetDateTime.parse(value) }.isSuccess) {
        "$fieldName must be an ISO-8601 timestamp with an explicit offset."
    }
}
