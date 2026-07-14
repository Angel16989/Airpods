package com.angel16989.appleicon.domain.airpods

import com.angel16989.appleicon.data.model.AirPodsErrorEnvelope
import com.angel16989.appleicon.data.model.AirPodsMonitorError
import com.angel16989.appleicon.data.model.AirPodsMonitorErrorCode

internal object AirPodsMonitorErrors {
    fun bluetoothOff(occurredAt: String): AirPodsErrorEnvelope =
        envelope(
            code = AirPodsMonitorErrorCode.BLUETOOTH_OFF,
            message = "Turn on Bluetooth to detect your AirPods.",
            recoverable = true,
            userAction = "open_bluetooth_settings",
            occurredAt = occurredAt,
        )

    fun bluetoothPermissionDenied(occurredAt: String): AirPodsErrorEnvelope =
        envelope(
            code = AirPodsMonitorErrorCode.BLUETOOTH_PERMISSION_DENIED,
            message = "Apple Icon needs Bluetooth permission to detect AirPods.",
            recoverable = true,
            userAction = "open_bluetooth_permission_settings",
            details = mapOf("permission" to "BLUETOOTH_SCAN"),
            occurredAt = occurredAt,
        )

    fun overlayUnavailable(
        occurredAt: String,
        notificationAvailable: Boolean,
    ): AirPodsErrorEnvelope =
        envelope(
            code = AirPodsMonitorErrorCode.OVERLAY_UNAVAILABLE,
            message = "Popup permission is off, so battery status will appear inside the app.",
            recoverable = true,
            userAction = "open_overlay_permission_settings",
            details = mapOf("notification_available" to notificationAvailable.toString()),
            occurredAt = occurredAt,
        )

    fun batteryUnavailable(
        occurredAt: String,
        reason: String,
    ): AirPodsErrorEnvelope =
        envelope(
            code = AirPodsMonitorErrorCode.BATTERY_UNAVAILABLE,
            message = "AirPods detected, but battery level is unavailable right now.",
            recoverable = true,
            userAction = "retry_bluetooth_scan",
            details = mapOf("reason" to reason),
            occurredAt = occurredAt,
        )

    fun staleData(occurredAt: String): AirPodsErrorEnvelope =
        envelope(
            code = AirPodsMonitorErrorCode.STALE_DATA,
            message = "Showing last known battery from earlier.",
            recoverable = true,
            userAction = "continue_scanning",
            occurredAt = occurredAt,
        )

    fun scanThrottled(occurredAt: String): AirPodsErrorEnvelope =
        envelope(
            code = AirPodsMonitorErrorCode.SCAN_THROTTLED,
            message = "Waiting for Android to allow the next scan.",
            recoverable = true,
            userAction = "wait_for_scan_window",
            occurredAt = occurredAt,
        )

    private fun envelope(
        code: AirPodsMonitorErrorCode,
        message: String,
        recoverable: Boolean,
        userAction: String,
        occurredAt: String,
        details: Map<String, String> = emptyMap(),
    ): AirPodsErrorEnvelope =
        AirPodsErrorEnvelope(
            error =
                AirPodsMonitorError(
                    code = code,
                    message = message,
                    recoverable = recoverable,
                    userAction = userAction,
                    details = details,
                ),
            occurredAt = occurredAt,
        )
}
