package com.angel16989.appleicon.domain.airpods

import com.angel16989.appleicon.data.model.AirPodsBatterySnapshot
import java.time.OffsetDateTime

enum class AirPodsDebugEventName(
    val serializedName: String,
) {
    AIRPODS_DETECTED("airpods_detected"),
    BATTERY_POPUP_SHOWN("battery_popup_shown"),
    POPUP_FALLBACK_USED("popup_fallback_used"),
    PERMISSION_BLOCKED("permission_blocked"),
}

data class AirPodsDebugEvent(
    val name: AirPodsDebugEventName,
    val properties: Map<String, String>,
    val occurredAt: String,
) {
    init {
        require(runCatching { OffsetDateTime.parse(occurredAt) }.isSuccess) {
            "occurredAt must be an ISO-8601 timestamp with an explicit offset."
        }
    }

    companion object {
        fun airPodsDetected(
            snapshot: AirPodsBatterySnapshot,
            occurredAt: String,
        ): AirPodsDebugEvent =
            AirPodsDebugEvent(
                name = AirPodsDebugEventName.AIRPODS_DETECTED,
                properties =
                    mapOf(
                        "model_hint" to (snapshot.modelHint?.serializedName ?: "unknown"),
                        "source" to snapshot.source.serializedName,
                        "has_left" to (snapshot.leftBatteryPercent != null).toString(),
                        "has_right" to (snapshot.rightBatteryPercent != null).toString(),
                        "has_case" to (snapshot.caseBatteryPercent != null).toString(),
                    ),
                occurredAt = occurredAt,
            )

        fun batteryPopupShown(
            snapshot: AirPodsBatterySnapshot,
            overlayEnabled: Boolean,
            occurredAt: String,
        ): AirPodsDebugEvent =
            AirPodsDebugEvent(
                name = AirPodsDebugEventName.BATTERY_POPUP_SHOWN,
                properties =
                    mapOf(
                        "model_hint" to (snapshot.modelHint?.serializedName ?: "unknown"),
                        "source" to snapshot.source.serializedName,
                        "is_stale" to snapshot.isStale.toString(),
                        "overlay_enabled" to overlayEnabled.toString(),
                    ),
                occurredAt = occurredAt,
            )

        fun popupFallbackUsed(
            reason: String,
            notificationAvailable: Boolean,
            occurredAt: String,
        ): AirPodsDebugEvent =
            AirPodsDebugEvent(
                name = AirPodsDebugEventName.POPUP_FALLBACK_USED,
                properties =
                    mapOf(
                        "reason" to reason,
                        "notification_available" to notificationAvailable.toString(),
                    ),
                occurredAt = occurredAt,
            )

        fun permissionBlocked(
            permissionType: String,
            occurredAt: String,
        ): AirPodsDebugEvent =
            AirPodsDebugEvent(
                name = AirPodsDebugEventName.PERMISSION_BLOCKED,
                properties = mapOf("permission_type" to permissionType),
                occurredAt = occurredAt,
            )
    }
}

fun interface AirPodsDebugEventLogger {
    fun log(event: AirPodsDebugEvent)
}

object NoOpAirPodsDebugEventLogger : AirPodsDebugEventLogger {
    override fun log(event: AirPodsDebugEvent) = Unit
}
