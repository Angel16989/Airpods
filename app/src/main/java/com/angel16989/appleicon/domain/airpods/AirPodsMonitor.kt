package com.angel16989.appleicon.domain.airpods

import com.angel16989.appleicon.data.local.AirPodsPreferencesRepository
import com.angel16989.appleicon.data.model.AirPodsBatterySnapshot
import com.angel16989.appleicon.data.model.AirPodsBluetoothPayload
import com.angel16989.appleicon.data.model.AirPodsConnectionState
import com.angel16989.appleicon.data.model.AirPodsErrorEnvelope
import com.angel16989.appleicon.data.model.AirPodsMonitorPermissions
import com.angel16989.appleicon.data.model.AirPodsMonitorRequest
import com.angel16989.appleicon.data.model.AirPodsMonitorResult
import com.angel16989.appleicon.data.model.AirPodsScanReason
import com.angel16989.appleicon.data.model.AirPodsSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.time.Duration
import java.time.OffsetDateTime

fun interface AirPodsSignalSource {
    fun observeSignals(scanReason: AirPodsScanReason): Flow<AirPodsBluetoothPayload>
}

class AirPodsMonitor(
    private val repository: AirPodsPreferencesRepository,
    private val signalSource: AirPodsSignalSource = AirPodsSignalSource { emptyFlow() },
    private val parser: AirPodsPayloadParser = AirPodsPayloadParser(),
    private val now: () -> OffsetDateTime = { OffsetDateTime.now() },
) {
    fun observeSnapshots(
        request: AirPodsMonitorRequest,
        permissions: AirPodsMonitorPermissions,
    ): Flow<AirPodsMonitorResult> =
        flow {
            repository.ensureInitialized()
            val settings = repository.settings.first()
            if (!request.monitoringEnabled || !settings.monitoringEnabled) {
                return@flow
            }

            val gateError = gateError(permissions, now().toString())
            if (gateError != null) {
                emit(AirPodsMonitorResult.Failure(gateError))
                return@flow
            }

            val cachedSnapshot = repository.snapshot.first()
            if (cachedSnapshot != null) {
                emit(
                    cachedSnapshot
                        .withStaleState(request)
                        .toMonitorResult(
                            request = request,
                            settings = settings,
                            permissions = permissions,
                            previousSnapshot = cachedSnapshot,
                            batteryUnavailable = cachedSnapshot.hasNoBatteryValues(),
                        ),
                )
            }

            signalSource.observeSignals(request.scanReason).collect { payload ->
                when (val parsed = parser.parse(payload, errorOccurredAt = now().toString())) {
                    is AirPodsPayloadParseResult.Failure ->
                        emit(AirPodsMonitorResult.Failure(parsed.error))

                    is AirPodsPayloadParseResult.Success -> {
                        val previousSnapshot = repository.snapshot.first()
                        val snapshot =
                            parsed.snapshot
                                .carryForwardPopupCooldown(previousSnapshot)
                                .withStaleState(request)
                        repository.saveSnapshot(snapshot)
                        emit(
                            snapshot.toMonitorResult(
                                request = request,
                                settings = settings,
                                permissions = permissions,
                                previousSnapshot = previousSnapshot,
                                batteryUnavailable = parsed.batteryUnavailable,
                            ),
                        )
                    }
                }
            }
        }

    private fun gateError(
        permissions: AirPodsMonitorPermissions,
        occurredAt: String,
    ): AirPodsErrorEnvelope? =
        when {
            !permissions.bluetoothPermissionGranted ->
                AirPodsMonitorErrors.bluetoothPermissionDenied(occurredAt)

            !permissions.bluetoothAvailable ->
                AirPodsMonitorErrors.bluetoothOff(occurredAt)

            permissions.scanThrottled ->
                AirPodsMonitorErrors.scanThrottled(occurredAt)

            else -> null
        }

    private fun AirPodsBatterySnapshot.withStaleState(request: AirPodsMonitorRequest): AirPodsBatterySnapshot {
        val age = Duration.between(OffsetDateTime.parse(lastSeenAt), now())
        val stale = age.seconds > request.freshnessWindowSeconds
        return copy(isStale = stale)
    }

    private fun AirPodsBatterySnapshot.toMonitorResult(
        request: AirPodsMonitorRequest,
        settings: AirPodsSettings,
        permissions: AirPodsMonitorPermissions,
        previousSnapshot: AirPodsBatterySnapshot?,
        batteryUnavailable: Boolean,
    ): AirPodsMonitorResult.Snapshot {
        val occurredAt = now().toString()
        val fallbackErrors =
            buildList {
                if (overlayUnavailable(request, settings, permissions)) {
                    add(
                        AirPodsMonitorErrors.overlayUnavailable(
                            occurredAt = occurredAt,
                            notificationAvailable =
                                settings.notificationEnabled &&
                                    permissions.notificationPermissionGranted,
                        ),
                    )
                }
                if (batteryUnavailable) {
                    add(
                        AirPodsMonitorErrors.batteryUnavailable(
                            occurredAt = occurredAt,
                            reason = "battery_values_missing",
                        ),
                    )
                }
                if (isStale) {
                    add(AirPodsMonitorErrors.staleData(occurredAt))
                }
            }

        return AirPodsMonitorResult.Snapshot(
            snapshot = this,
            popupShouldShow =
                shouldShowPopup(
                    request = request,
                    settings = settings,
                    permissions = permissions,
                    previousSnapshot = previousSnapshot,
                ),
            fallbackErrors = fallbackErrors,
        )
    }

    private fun AirPodsBatterySnapshot.shouldShowPopup(
        request: AirPodsMonitorRequest,
        settings: AirPodsSettings,
        permissions: AirPodsMonitorPermissions,
        previousSnapshot: AirPodsBatterySnapshot?,
    ): Boolean {
        if (!request.overlayEnabled || !settings.overlayEnabled || !permissions.overlayPermissionGranted) {
            return false
        }
        if (connectionState !in POPUP_ELIGIBLE_STATES || isStale) {
            return false
        }

        val shownAt = popupLastShownAt ?: return true
        val shownAge = Duration.between(OffsetDateTime.parse(shownAt), now())
        val unchanged =
            previousSnapshot != null &&
                hasSamePopupState(previousSnapshot)
        return !(unchanged && shownAge.seconds < request.cooldownSeconds)
    }

    private fun overlayUnavailable(
        request: AirPodsMonitorRequest,
        settings: AirPodsSettings,
        permissions: AirPodsMonitorPermissions,
    ): Boolean =
        request.overlayEnabled &&
            settings.overlayEnabled &&
            !permissions.overlayPermissionGranted

    private fun AirPodsBatterySnapshot.carryForwardPopupCooldown(previousSnapshot: AirPodsBatterySnapshot?): AirPodsBatterySnapshot =
        if (previousSnapshot != null && hasSamePopupState(previousSnapshot)) {
            copy(popupLastShownAt = previousSnapshot.popupLastShownAt)
        } else {
            this
        }

    private fun AirPodsBatterySnapshot.hasSamePopupState(other: AirPodsBatterySnapshot): Boolean =
        deviceId == other.deviceId &&
            connectionState == other.connectionState &&
            leftBatteryPercent == other.leftBatteryPercent &&
            rightBatteryPercent == other.rightBatteryPercent &&
            caseBatteryPercent == other.caseBatteryPercent &&
            leftCharging == other.leftCharging &&
            rightCharging == other.rightCharging &&
            caseCharging == other.caseCharging

    private fun AirPodsBatterySnapshot.hasNoBatteryValues(): Boolean =
        leftBatteryPercent == null &&
            rightBatteryPercent == null &&
            caseBatteryPercent == null

    private companion object {
        val POPUP_ELIGIBLE_STATES =
            setOf(
                AirPodsConnectionState.DETECTED,
                AirPodsConnectionState.CONNECTED,
            )
    }
}
