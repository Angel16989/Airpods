package com.angel16989.appleicon.domain.airpods

import com.angel16989.appleicon.data.model.AirPodsBatterySnapshot
import com.angel16989.appleicon.data.model.AirPodsBluetoothPayload
import com.angel16989.appleicon.data.model.AirPodsErrorEnvelope
import com.angel16989.appleicon.data.model.AirPodsModelHint
import java.security.MessageDigest

class AirPodsPayloadParser(
    private val deviceIdSalt: String = DEFAULT_DEVICE_ID_SALT,
) {
    fun parse(
        payload: AirPodsBluetoothPayload,
        errorOccurredAt: String = payload.observedAt,
    ): AirPodsPayloadParseResult {
        val invalidBatteryFields = payload.invalidBatteryFields()
        if (invalidBatteryFields.isNotEmpty()) {
            return AirPodsPayloadParseResult.Failure(
                AirPodsMonitorErrors.batteryUnavailable(
                    occurredAt = errorOccurredAt,
                    reason = "invalid_battery_percent:${invalidBatteryFields.joinToString(",")}",
                ),
            )
        }

        if (payload.bluetoothIdentity.isBlank()) {
            return AirPodsPayloadParseResult.Failure(
                AirPodsMonitorErrors.batteryUnavailable(
                    occurredAt = errorOccurredAt,
                    reason = "missing_bluetooth_identity",
                ),
            )
        }

        val displayName = payload.displayName?.trim()?.takeIf(String::isNotBlank) ?: "AirPods"
        val snapshot =
            runCatching {
                AirPodsBatterySnapshot(
                    deviceId = stableDeviceId(payload.bluetoothIdentity),
                    displayName = displayName,
                    modelHint = payload.modelHint ?: inferModelHint(displayName),
                    connectionState = payload.connectionState,
                    leftBatteryPercent = payload.leftBatteryPercent,
                    rightBatteryPercent = payload.rightBatteryPercent,
                    caseBatteryPercent = payload.caseBatteryPercent,
                    leftCharging = payload.leftCharging,
                    rightCharging = payload.rightCharging,
                    caseCharging = payload.caseCharging,
                    source = payload.source,
                    lastSeenAt = payload.observedAt,
                    isStale = false,
                )
            }.getOrElse {
                return AirPodsPayloadParseResult.Failure(
                    AirPodsMonitorErrors.batteryUnavailable(
                        occurredAt = errorOccurredAt,
                        reason = "invalid_payload",
                    ),
                )
            }

        return AirPodsPayloadParseResult.Success(
            snapshot = snapshot,
            batteryUnavailable = !snapshot.hasAnyBatteryValue(),
        )
    }

    private fun stableDeviceId(bluetoothIdentity: String): String {
        val digest =
            MessageDigest
                .getInstance("SHA-256")
                .digest("$deviceIdSalt:$bluetoothIdentity".encodeToByteArray())
        val hex = digest.joinToString(separator = "") { byte -> "%02x".format(byte) }
        return "airpods_${hex.take(12)}"
    }

    private fun inferModelHint(displayName: String): AirPodsModelHint {
        val normalized = displayName.lowercase()
        return when {
            "max" in normalized -> AirPodsModelHint.AIRPODS_MAX
            "pro" in normalized -> AirPodsModelHint.AIRPODS_PRO
            "airpods" in normalized -> AirPodsModelHint.AIRPODS
            else -> AirPodsModelHint.UNKNOWN
        }
    }

    private companion object {
        const val DEFAULT_DEVICE_ID_SALT = "apple-icon-v0.1-airpods-device-id"
    }
}

sealed interface AirPodsPayloadParseResult {
    data class Success(
        val snapshot: AirPodsBatterySnapshot,
        val batteryUnavailable: Boolean,
    ) : AirPodsPayloadParseResult

    data class Failure(
        val error: AirPodsErrorEnvelope,
    ) : AirPodsPayloadParseResult
}

private fun AirPodsBluetoothPayload.invalidBatteryFields(): List<String> =
    buildList {
        if (leftBatteryPercent != null && leftBatteryPercent !in 0..100) {
            add("left_battery_percent")
        }
        if (rightBatteryPercent != null && rightBatteryPercent !in 0..100) {
            add("right_battery_percent")
        }
        if (caseBatteryPercent != null && caseBatteryPercent !in 0..100) {
            add("case_battery_percent")
        }
    }

private fun AirPodsBatterySnapshot.hasAnyBatteryValue(): Boolean =
    leftBatteryPercent != null || rightBatteryPercent != null || caseBatteryPercent != null
