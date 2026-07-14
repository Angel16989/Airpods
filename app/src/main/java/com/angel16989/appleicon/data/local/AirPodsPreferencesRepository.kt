package com.angel16989.appleicon.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.angel16989.appleicon.data.model.AirPodsBatterySnapshot
import com.angel16989.appleicon.data.model.AirPodsConnectionState
import com.angel16989.appleicon.data.model.AirPodsLocalState
import com.angel16989.appleicon.data.model.AirPodsModelHint
import com.angel16989.appleicon.data.model.AirPodsNotificationState
import com.angel16989.appleicon.data.model.AirPodsSettings
import com.angel16989.appleicon.data.model.AirPodsSnapshotSource
import com.angel16989.appleicon.data.model.stableAirPodsNotificationId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AirPodsPreferencesRepository(
    private val dataStore: DataStore<Preferences>,
) {
    val settings: Flow<AirPodsSettings> =
        dataStore.data.map { preferences ->
            preferences.toSettings()
        }

    val snapshot: Flow<AirPodsBatterySnapshot?> =
        dataStore.data.map { preferences ->
            preferences.toSnapshot()
        }

    val notificationState: Flow<AirPodsNotificationState> =
        dataStore.data.map { preferences ->
            preferences.toNotificationState()
        }

    val localState: Flow<AirPodsLocalState> =
        combine(settings, snapshot, notificationState) { settings, snapshot, notificationState ->
            AirPodsLocalState(
                settings = settings,
                snapshot = snapshot,
                notificationState = notificationState,
            )
        }

    suspend fun ensureInitialized() {
        dataStore.edit { preferences ->
            if (!preferences.contains(AirPodsPreferenceKeys.SchemaVersion)) {
                preferences[AirPodsPreferenceKeys.SchemaVersion] = SCHEMA_VERSION
            }
        }
    }

    suspend fun saveSettings(settings: AirPodsSettings) {
        dataStore.edit { preferences ->
            preferences[AirPodsPreferenceKeys.SchemaVersion] = SCHEMA_VERSION
            preferences[AirPodsPreferenceKeys.MonitoringEnabled] = settings.monitoringEnabled
            preferences[AirPodsPreferenceKeys.OverlayEnabled] = settings.overlayEnabled
            preferences[AirPodsPreferenceKeys.NotificationEnabled] = settings.notificationEnabled
            preferences[AirPodsPreferenceKeys.NotificationPermissionGranted] =
                settings.notificationPermissionGranted
        }
    }

    suspend fun saveSnapshot(snapshot: AirPodsBatterySnapshot) {
        dataStore.edit { preferences ->
            preferences[AirPodsPreferenceKeys.SchemaVersion] = SCHEMA_VERSION
            preferences[AirPodsPreferenceKeys.DeviceId] = snapshot.deviceId
            preferences[AirPodsPreferenceKeys.DisplayName] = snapshot.displayName
            preferences.putNullableString(
                AirPodsPreferenceKeys.ModelHint,
                snapshot.modelHint?.serializedName,
            )
            preferences[AirPodsPreferenceKeys.ConnectionState] =
                snapshot.connectionState.serializedName
            preferences.putNullableInt(
                AirPodsPreferenceKeys.LeftBatteryPercent,
                snapshot.leftBatteryPercent,
            )
            preferences.putNullableInt(
                AirPodsPreferenceKeys.RightBatteryPercent,
                snapshot.rightBatteryPercent,
            )
            preferences.putNullableInt(
                AirPodsPreferenceKeys.CaseBatteryPercent,
                snapshot.caseBatteryPercent,
            )
            preferences.putNullableBoolean(AirPodsPreferenceKeys.LeftCharging, snapshot.leftCharging)
            preferences.putNullableBoolean(AirPodsPreferenceKeys.RightCharging, snapshot.rightCharging)
            preferences.putNullableBoolean(AirPodsPreferenceKeys.CaseCharging, snapshot.caseCharging)
            preferences[AirPodsPreferenceKeys.Source] = snapshot.source.serializedName
            preferences[AirPodsPreferenceKeys.LastSeenAt] = snapshot.lastSeenAt
            preferences[AirPodsPreferenceKeys.IsStale] = snapshot.isStale
            preferences.putNullableString(
                AirPodsPreferenceKeys.PopupLastShownAt,
                snapshot.popupLastShownAt,
            )
        }
    }

    suspend fun markPopupShown(
        deviceId: String,
        shownAt: String,
    ) {
        val currentSnapshot = dataStore.data.map { preferences -> preferences.toSnapshot() }.first()
        currentSnapshot
            ?.takeIf { it.deviceId == deviceId }
            ?.copy(popupLastShownAt = shownAt)
            ?.let { snapshot -> saveSnapshot(snapshot) }
    }

    suspend fun clearSnapshot() {
        dataStore.edit { preferences ->
            preferences.removeSnapshot()
        }
    }

    suspend fun saveNotificationState(notificationState: AirPodsNotificationState) {
        dataStore.edit { preferences ->
            preferences[AirPodsPreferenceKeys.SchemaVersion] = SCHEMA_VERSION
            preferences[AirPodsPreferenceKeys.NotificationActive] =
                notificationState.notificationActive
            preferences.putNullableInt(
                AirPodsPreferenceKeys.NotificationId,
                notificationState.notificationId,
            )
            preferences.putNullableString(
                AirPodsPreferenceKeys.LastNotificationUpdateAt,
                notificationState.lastNotificationUpdateAt,
            )
        }
    }

    suspend fun seedLocalDevelopmentData(timestamp: String = "2026-07-14T09:00:00+10:00") {
        val snapshot =
            AirPodsBatterySnapshot(
                deviceId = "airpods_7f4a9c2d1b66",
                displayName = "AirPods Pro",
                modelHint = AirPodsModelHint.AIRPODS_PRO,
                connectionState = AirPodsConnectionState.CONNECTED,
                leftBatteryPercent = 82,
                rightBatteryPercent = 79,
                caseBatteryPercent = 64,
                leftCharging = false,
                rightCharging = false,
                caseCharging = true,
                source = AirPodsSnapshotSource.MANUAL_TEST,
                lastSeenAt = timestamp,
                isStale = false,
                popupLastShownAt = null,
            )
        val notificationState =
            AirPodsNotificationState(
                notificationActive = true,
                notificationId = stableAirPodsNotificationId(snapshot.deviceId),
                lastNotificationUpdateAt = timestamp,
            )

        dataStore.edit { preferences ->
            preferences.clear()
            preferences[AirPodsPreferenceKeys.SchemaVersion] = SCHEMA_VERSION
            preferences[AirPodsPreferenceKeys.MonitoringEnabled] = true
            preferences[AirPodsPreferenceKeys.OverlayEnabled] = true
            preferences[AirPodsPreferenceKeys.NotificationEnabled] = true
            preferences[AirPodsPreferenceKeys.NotificationPermissionGranted] = true
            preferences[AirPodsPreferenceKeys.DeviceId] = snapshot.deviceId
            preferences[AirPodsPreferenceKeys.DisplayName] = snapshot.displayName
            preferences.putNullableString(
                AirPodsPreferenceKeys.ModelHint,
                snapshot.modelHint?.serializedName,
            )
            preferences[AirPodsPreferenceKeys.ConnectionState] =
                snapshot.connectionState.serializedName
            preferences.putNullableInt(
                AirPodsPreferenceKeys.LeftBatteryPercent,
                snapshot.leftBatteryPercent,
            )
            preferences.putNullableInt(
                AirPodsPreferenceKeys.RightBatteryPercent,
                snapshot.rightBatteryPercent,
            )
            preferences.putNullableInt(
                AirPodsPreferenceKeys.CaseBatteryPercent,
                snapshot.caseBatteryPercent,
            )
            preferences.putNullableBoolean(AirPodsPreferenceKeys.LeftCharging, snapshot.leftCharging)
            preferences.putNullableBoolean(AirPodsPreferenceKeys.RightCharging, snapshot.rightCharging)
            preferences.putNullableBoolean(AirPodsPreferenceKeys.CaseCharging, snapshot.caseCharging)
            preferences[AirPodsPreferenceKeys.Source] = snapshot.source.serializedName
            preferences[AirPodsPreferenceKeys.LastSeenAt] = snapshot.lastSeenAt
            preferences[AirPodsPreferenceKeys.IsStale] = snapshot.isStale
            preferences[AirPodsPreferenceKeys.NotificationActive] =
                notificationState.notificationActive
            preferences[AirPodsPreferenceKeys.NotificationId] =
                requireNotNull(notificationState.notificationId)
            preferences.putNullableString(
                AirPodsPreferenceKeys.LastNotificationUpdateAt,
                notificationState.lastNotificationUpdateAt,
            )
        }
    }

    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
            preferences[AirPodsPreferenceKeys.SchemaVersion] = SCHEMA_VERSION
        }
    }

    companion object {
        const val DATASTORE_NAME = "apple_icon_preferences"
        const val SCHEMA_VERSION = 1
    }
}

private object AirPodsPreferenceKeys {
    val SchemaVersion = intPreferencesKey("schema_version")
    val MonitoringEnabled = booleanPreferencesKey("monitoring_enabled")
    val OverlayEnabled = booleanPreferencesKey("overlay_enabled")
    val NotificationEnabled = booleanPreferencesKey("notification_enabled")
    val NotificationPermissionGranted = booleanPreferencesKey("notification_permission_granted")
    val DeviceId = stringPreferencesKey("device_id")
    val DisplayName = stringPreferencesKey("display_name")
    val ModelHint = stringPreferencesKey("model_hint")
    val ConnectionState = stringPreferencesKey("connection_state")
    val LeftBatteryPercent = intPreferencesKey("left_battery_percent")
    val RightBatteryPercent = intPreferencesKey("right_battery_percent")
    val CaseBatteryPercent = intPreferencesKey("case_battery_percent")
    val LeftCharging = booleanPreferencesKey("left_charging")
    val RightCharging = booleanPreferencesKey("right_charging")
    val CaseCharging = booleanPreferencesKey("case_charging")
    val Source = stringPreferencesKey("source")
    val LastSeenAt = stringPreferencesKey("last_seen_at")
    val IsStale = booleanPreferencesKey("is_stale")
    val PopupLastShownAt = stringPreferencesKey("popup_last_shown_at")
    val NotificationActive = booleanPreferencesKey("notification_active")
    val NotificationId = intPreferencesKey("notification_id")
    val LastNotificationUpdateAt = stringPreferencesKey("last_notification_update_at")
}

private fun Preferences.toSettings(): AirPodsSettings =
    AirPodsSettings(
        monitoringEnabled = this[AirPodsPreferenceKeys.MonitoringEnabled] ?: true,
        overlayEnabled = this[AirPodsPreferenceKeys.OverlayEnabled] ?: true,
        notificationEnabled = this[AirPodsPreferenceKeys.NotificationEnabled] ?: true,
        notificationPermissionGranted =
            this[AirPodsPreferenceKeys.NotificationPermissionGranted] ?: false,
    )

private fun Preferences.toSnapshot(): AirPodsBatterySnapshot? {
    val deviceId = this[AirPodsPreferenceKeys.DeviceId] ?: return null
    val displayName = this[AirPodsPreferenceKeys.DisplayName] ?: return null
    val connectionState =
        this[AirPodsPreferenceKeys.ConnectionState]
            ?.let(AirPodsConnectionState::fromSerializedName) ?: return null
    val source =
        this[AirPodsPreferenceKeys.Source]
            ?.let(AirPodsSnapshotSource::fromSerializedName) ?: return null
    val lastSeenAt = this[AirPodsPreferenceKeys.LastSeenAt] ?: return null
    val isStale = this[AirPodsPreferenceKeys.IsStale] ?: return null

    return runCatching {
        AirPodsBatterySnapshot(
            deviceId = deviceId,
            displayName = displayName,
            modelHint =
                this[AirPodsPreferenceKeys.ModelHint]
                    ?.takeIf(String::isNotBlank)
                    ?.let(AirPodsModelHint::fromSerializedName),
            connectionState = connectionState,
            leftBatteryPercent = this[AirPodsPreferenceKeys.LeftBatteryPercent],
            rightBatteryPercent = this[AirPodsPreferenceKeys.RightBatteryPercent],
            caseBatteryPercent = this[AirPodsPreferenceKeys.CaseBatteryPercent],
            leftCharging = this[AirPodsPreferenceKeys.LeftCharging],
            rightCharging = this[AirPodsPreferenceKeys.RightCharging],
            caseCharging = this[AirPodsPreferenceKeys.CaseCharging],
            source = source,
            lastSeenAt = lastSeenAt,
            isStale = isStale,
            popupLastShownAt = this[AirPodsPreferenceKeys.PopupLastShownAt],
        )
    }.getOrNull()
}

private fun Preferences.toNotificationState(): AirPodsNotificationState {
    val active = this[AirPodsPreferenceKeys.NotificationActive] ?: false
    val notificationId = this[AirPodsPreferenceKeys.NotificationId]
    return runCatching {
        AirPodsNotificationState(
            notificationActive = active,
            notificationId = notificationId,
            lastNotificationUpdateAt = this[AirPodsPreferenceKeys.LastNotificationUpdateAt],
        )
    }.getOrDefault(AirPodsNotificationState())
}

private fun androidx.datastore.preferences.core.MutablePreferences.removeSnapshot() {
    remove(AirPodsPreferenceKeys.DeviceId)
    remove(AirPodsPreferenceKeys.DisplayName)
    remove(AirPodsPreferenceKeys.ModelHint)
    remove(AirPodsPreferenceKeys.ConnectionState)
    remove(AirPodsPreferenceKeys.LeftBatteryPercent)
    remove(AirPodsPreferenceKeys.RightBatteryPercent)
    remove(AirPodsPreferenceKeys.CaseBatteryPercent)
    remove(AirPodsPreferenceKeys.LeftCharging)
    remove(AirPodsPreferenceKeys.RightCharging)
    remove(AirPodsPreferenceKeys.CaseCharging)
    remove(AirPodsPreferenceKeys.Source)
    remove(AirPodsPreferenceKeys.LastSeenAt)
    remove(AirPodsPreferenceKeys.IsStale)
    remove(AirPodsPreferenceKeys.PopupLastShownAt)
}

private fun androidx.datastore.preferences.core.MutablePreferences.putNullableString(
    key: Preferences.Key<String>,
    value: String?,
) {
    if (value == null) {
        remove(key)
    } else {
        this[key] = value
    }
}

private fun androidx.datastore.preferences.core.MutablePreferences.putNullableInt(
    key: Preferences.Key<Int>,
    value: Int?,
) {
    if (value == null) {
        remove(key)
    } else {
        this[key] = value
    }
}

private fun androidx.datastore.preferences.core.MutablePreferences.putNullableBoolean(
    key: Preferences.Key<Boolean>,
    value: Boolean?,
) {
    if (value == null) {
        remove(key)
    } else {
        this[key] = value
    }
}
