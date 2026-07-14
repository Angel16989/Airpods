package com.angel16989.appleicon.ui

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.angel16989.appleicon.data.local.AirPodsPreferencesRepository
import com.angel16989.appleicon.data.local.appleIconDataStore
import com.angel16989.appleicon.data.model.AirPodsBluetoothPayload
import com.angel16989.appleicon.data.model.AirPodsConnectionState
import com.angel16989.appleicon.data.model.AirPodsLocalState
import com.angel16989.appleicon.data.model.AirPodsModelHint
import com.angel16989.appleicon.data.model.AirPodsMonitorRequest
import com.angel16989.appleicon.data.model.AirPodsMonitorResult
import com.angel16989.appleicon.data.model.AirPodsNotificationState
import com.angel16989.appleicon.data.model.AirPodsScanReason
import com.angel16989.appleicon.data.model.AirPodsSettings
import com.angel16989.appleicon.data.model.AirPodsSnapshotSource
import com.angel16989.appleicon.domain.airpods.AirPodsDebugEvent
import com.angel16989.appleicon.domain.airpods.AirPodsDebugEventLogger
import com.angel16989.appleicon.domain.airpods.AirPodsMonitor
import com.angel16989.appleicon.domain.airpods.AirPodsSignalSource
import com.angel16989.appleicon.ui.airpods.AirPodsDashboardActions
import com.angel16989.appleicon.ui.airpods.AirPodsDashboardScreen
import com.angel16989.appleicon.ui.airpods.AirPodsRuntimePermissionState
import com.angel16989.appleicon.ui.airpods.mapAirPodsDashboardUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

@Composable
fun AppleIconApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val appContext = context.applicationContext
    val repository =
        remember(appContext) {
            AirPodsPreferencesRepository(appContext.appleIconDataStore)
        }
    val manualSignals =
        remember {
            Channel<AirPodsBluetoothPayload>(capacity = Channel.BUFFERED)
        }
    val monitor =
        remember(repository, manualSignals) {
            AirPodsMonitor(
                repository = repository,
                signalSource = AirPodsSignalSource { manualSignals.receiveAsFlow() },
                debugEventLogger = LogcatAirPodsDebugEventLogger,
            )
        }
    val scope = rememberCoroutineScope()
    var permissionRefresh by remember { mutableIntStateOf(0) }
    var scanRefresh by remember { mutableIntStateOf(0) }
    var latestMonitorResult by remember { mutableStateOf<AirPodsMonitorResult?>(null) }
    var popupResult by remember { mutableStateOf<AirPodsMonitorResult.Snapshot?>(null) }
    var isScanning by remember { mutableStateOf(false) }

    val bluetoothPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
        ) {
            permissionRefresh += 1
            scanRefresh += 1
        }
    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) {
            permissionRefresh += 1
            scanRefresh += 1
        }
    val settingsLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
        ) {
            permissionRefresh += 1
            scanRefresh += 1
        }

    val runtimePermissions =
        remember(permissionRefresh) {
            appContext.readAirPodsRuntimePermissions()
        }
    val localState by repository.localState.collectAsState(
        initial =
            AirPodsLocalState(
                settings = AirPodsSettings(),
                snapshot = null,
                notificationState = AirPodsNotificationState(),
            ),
    )

    LaunchedEffect(localState.settings, runtimePermissions, scanRefresh) {
        if (!localState.settings.monitoringEnabled) {
            isScanning = false
            return@LaunchedEffect
        }

        latestMonitorResult = null
        isScanning = true
        monitor
            .observeSnapshots(
                request =
                    AirPodsMonitorRequest(
                        monitoringEnabled = localState.settings.monitoringEnabled,
                        overlayEnabled = localState.settings.overlayEnabled,
                        scanReason = AirPodsScanReason.APP_FOREGROUND,
                    ),
                permissions = runtimePermissions.toMonitorPermissions(),
            ).collect { result ->
                latestMonitorResult = result
                isScanning = false
                if (result is AirPodsMonitorResult.Snapshot && result.popupShouldShow) {
                    popupResult = result
                    repository.markPopupShown(
                        deviceId = result.snapshot.deviceId,
                        shownAt = OffsetDateTime.now().toString(),
                    )
                }
            }
    }

    val uiState =
        mapAirPodsDashboardUiState(
            localState = localState,
            monitorResult = latestMonitorResult,
            runtimePermissions = runtimePermissions,
            isScanning = isScanning,
            popupResult = popupResult,
        )
    val actions =
        AirPodsDashboardActions(
            onMonitoringChanged = { enabled ->
                scope.launch {
                    repository.saveSettings(
                        localState.settings
                            .copy(monitoringEnabled = enabled)
                            .withRuntimePermissions(runtimePermissions),
                    )
                    scanRefresh += 1
                }
            },
            onPopupChanged = { enabled ->
                scope.launch {
                    repository.saveSettings(
                        localState.settings
                            .copy(overlayEnabled = enabled)
                            .withRuntimePermissions(runtimePermissions),
                    )
                    scanRefresh += 1
                }
            },
            onNotificationChanged = { enabled ->
                scope.launch {
                    repository.saveSettings(
                        localState.settings
                            .copy(notificationEnabled = enabled)
                            .withRuntimePermissions(runtimePermissions),
                    )
                }
            },
            onRequestBluetoothPermission = {
                bluetoothPermissionLauncher.launch(appContext.requiredBluetoothPermissions())
            },
            onOpenBluetoothSettings = {
                settingsLauncher.launch(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
            },
            onOpenOverlaySettings = {
                settingsLauncher.launch(appContext.overlaySettingsIntent())
            },
            onRequestNotificationPermission = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            },
            onRetryScan = {
                permissionRefresh += 1
                scanRefresh += 1
            },
            onTestPopup = {
                scope.launch {
                    manualSignals.send(manualTestPayload())
                    scanRefresh += 1
                }
            },
            onDismissPopup = {
                popupResult = null
            },
        )

    AirPodsDashboardScreen(
        state = uiState,
        actions = actions,
        modifier = modifier,
    )
}

private fun Context.readAirPodsRuntimePermissions(): AirPodsRuntimePermissionState {
    val bluetoothPermissionGranted =
        requiredBluetoothPermissions().all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    return AirPodsRuntimePermissionState(
        bluetoothPermissionGranted = bluetoothPermissionGranted,
        bluetoothAvailable = bluetoothPermissionGranted && isBluetoothEnabled(),
        overlayPermissionGranted = Settings.canDrawOverlays(this),
        notificationPermissionGranted = isNotificationPermissionGranted(),
        notificationPermissionRequired = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU,
    )
}

private fun Context.requiredBluetoothPermissions(): Array<String> =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
        )
    } else {
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }

private fun Context.isBluetoothEnabled(): Boolean =
    runCatching {
        val manager = getSystemService(BluetoothManager::class.java)
        manager?.adapter?.isEnabled == true
    }.getOrDefault(false)

private fun Context.isNotificationPermissionGranted(): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED

private fun Context.overlaySettingsIntent(): Intent =
    Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:$packageName"),
    )

private fun AirPodsSettings.withRuntimePermissions(runtimePermissions: AirPodsRuntimePermissionState): AirPodsSettings =
    copy(notificationPermissionGranted = runtimePermissions.notificationPermissionGranted)

private fun manualTestPayload(): AirPodsBluetoothPayload =
    AirPodsBluetoothPayload(
        bluetoothIdentity = "manual-test-airpods-pro",
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
        observedAt = OffsetDateTime.now().toString(),
    )

private object LogcatAirPodsDebugEventLogger : AirPodsDebugEventLogger {
    override fun log(event: AirPodsDebugEvent) {
        Log.d(
            "AppleIconAirPods",
            "${event.name.serializedName} ${event.properties.toSortedMap()} at ${event.occurredAt}",
        )
    }
}
