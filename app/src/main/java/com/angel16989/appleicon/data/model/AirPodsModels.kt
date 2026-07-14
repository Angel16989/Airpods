package com.angel16989.appleicon.data.model

import java.time.OffsetDateTime

private val DeviceIdPattern = Regex("^airpods_[0-9a-f]{12}$")

enum class AirPodsModelHint(
    val serializedName: String,
) {
    AIRPODS("airpods"),
    AIRPODS_PRO("airpods_pro"),
    AIRPODS_MAX("airpods_max"),
    UNKNOWN("unknown"),
    ;

    companion object {
        fun fromSerializedName(value: String): AirPodsModelHint? = entries.firstOrNull { it.serializedName == value }
    }
}

enum class AirPodsConnectionState(
    val serializedName: String,
) {
    SCANNING("scanning"),
    DETECTED("detected"),
    CONNECTED("connected"),
    DISCONNECTED("disconnected"),
    PERMISSION_BLOCKED("permission_blocked"),
    ;

    companion object {
        fun fromSerializedName(value: String): AirPodsConnectionState? = entries.firstOrNull { it.serializedName == value }
    }
}

enum class AirPodsSnapshotSource(
    val serializedName: String,
) {
    BLE_ADVERTISEMENT("ble_advertisement"),
    BLUETOOTH_CONNECTION("bluetooth_connection"),
    CACHED("cached"),
    MANUAL_TEST("manual_test"),
    ;

    companion object {
        fun fromSerializedName(value: String): AirPodsSnapshotSource? = entries.firstOrNull { it.serializedName == value }
    }
}

data class AirPodsBatterySnapshot(
    val deviceId: String,
    val displayName: String,
    val modelHint: AirPodsModelHint?,
    val connectionState: AirPodsConnectionState,
    val leftBatteryPercent: Int?,
    val rightBatteryPercent: Int?,
    val caseBatteryPercent: Int?,
    val leftCharging: Boolean?,
    val rightCharging: Boolean?,
    val caseCharging: Boolean?,
    val source: AirPodsSnapshotSource,
    val lastSeenAt: String,
    val isStale: Boolean,
    val popupLastShownAt: String? = null,
) {
    init {
        require(DeviceIdPattern.matches(deviceId)) {
            "deviceId must use airpods_<12 lowercase hex chars> format."
        }
        require(displayName.isNotBlank()) {
            "displayName is required."
        }
        requireBatteryPercent(leftBatteryPercent, "leftBatteryPercent")
        requireBatteryPercent(rightBatteryPercent, "rightBatteryPercent")
        requireBatteryPercent(caseBatteryPercent, "caseBatteryPercent")
        requireOffsetTimestamp(lastSeenAt, "lastSeenAt")
        popupLastShownAt?.let { requireOffsetTimestamp(it, "popupLastShownAt") }
    }
}

data class AirPodsSettings(
    val monitoringEnabled: Boolean = true,
    val overlayEnabled: Boolean = true,
    val notificationEnabled: Boolean = true,
    val notificationPermissionGranted: Boolean = false,
)

data class AirPodsNotificationState(
    val notificationActive: Boolean = false,
    val notificationId: Int? = null,
    val lastNotificationUpdateAt: String? = null,
) {
    init {
        require(notificationId == null || notificationId >= 0) {
            "notificationId must be a stable non-negative integer."
        }
        require(!notificationActive || notificationId != null) {
            "notificationId is required while a notification is active."
        }
        lastNotificationUpdateAt?.let { requireOffsetTimestamp(it, "lastNotificationUpdateAt") }
    }
}

data class AirPodsLocalState(
    val settings: AirPodsSettings,
    val snapshot: AirPodsBatterySnapshot?,
    val notificationState: AirPodsNotificationState,
)

fun stableAirPodsNotificationId(deviceId: String): Int {
    require(DeviceIdPattern.matches(deviceId)) {
        "deviceId must use airpods_<12 lowercase hex chars> format."
    }
    return 1_000 + (deviceId.hashCode() and Int.MAX_VALUE) % 900_000
}

private fun requireBatteryPercent(
    value: Int?,
    fieldName: String,
) {
    require(value == null || value in 0..100) {
        "$fieldName must be null or an integer from 0 through 100."
    }
}

private fun requireOffsetTimestamp(
    value: String,
    fieldName: String,
) {
    require(runCatching { OffsetDateTime.parse(value) }.isSuccess) {
        "$fieldName must be an ISO-8601 timestamp with an explicit offset."
    }
}
