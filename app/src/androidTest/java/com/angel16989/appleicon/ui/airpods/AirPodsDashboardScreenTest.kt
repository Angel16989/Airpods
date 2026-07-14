package com.angel16989.appleicon.ui.airpods

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.angel16989.appleicon.ui.theme.AppleIconTheme
import org.junit.Rule
import org.junit.Test

class AirPodsDashboardScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun emptyStateShowsSetupGuidance() {
        composeRule.setContent {
            AppleIconTheme {
                AirPodsDashboardScreen(
                    state = baseState(mode = AirPodsDashboardMode.EMPTY),
                    actions = AirPodsDashboardActions(),
                )
            }
        }

        composeRule.onNodeWithText("No AirPods detected yet").assertIsDisplayed()
        composeRule.onNodeWithText("Grant permissions, then open the case near this phone.").assertIsDisplayed()
    }

    @Test
    fun loadingStateShowsScanningIndicator() {
        composeRule.setContent {
            AppleIconTheme {
                AirPodsDashboardScreen(
                    state =
                        baseState(
                            mode = AirPodsDashboardMode.LOADING,
                            statusTitle = "Scanning",
                            isScanning = true,
                        ),
                    actions = AirPodsDashboardActions(),
                )
            }
        }

        composeRule.onAllNodesWithTag("apple_icon_loading_indicator").assertCountEquals(2)
    }

    @Test
    fun errorStateShowsRecoveryAction() {
        composeRule.setContent {
            AppleIconTheme {
                AirPodsDashboardScreen(
                    state =
                        baseState(
                            mode = AirPodsDashboardMode.ERROR,
                            statusTitle = "Needs attention",
                            primaryIssue =
                                AirPodsIssueUi(
                                    title = "Bluetooth permission needed",
                                    message = "Apple Icon needs Bluetooth permission to detect AirPods.",
                                    actionLabel = "Grant Bluetooth",
                                    action = AirPodsPermissionAction.REQUEST_BLUETOOTH_PERMISSION,
                                ),
                        ),
                    actions = AirPodsDashboardActions(),
                )
            }
        }

        composeRule.onNodeWithText("Bluetooth permission needed").assertIsDisplayed()
        composeRule.onNodeWithText("Grant Bluetooth").assertIsDisplayed()
    }

    @Test
    fun fallbackIssuesShowSpecMessagesAndRecoveryActions() {
        composeRule.setContent {
            AppleIconTheme {
                AirPodsDashboardScreen(
                    state =
                        baseState(
                            mode = AirPodsDashboardMode.SUCCESS,
                            snapshot = snapshotUi(),
                            fallbackIssues =
                                listOf(
                                    AirPodsIssueUi(
                                        title = "Popup fallback active",
                                        message = "Popup permission is off, so battery status will appear inside the app.",
                                        actionLabel = "Open Popup Settings",
                                        action = AirPodsPermissionAction.OPEN_OVERLAY_SETTINGS,
                                    ),
                                    AirPodsIssueUi(
                                        title = "Battery unavailable",
                                        message = "AirPods detected, but battery level is unavailable right now.",
                                        actionLabel = "Retry scan",
                                        action = AirPodsPermissionAction.RETRY_SCAN,
                                    ),
                                    AirPodsIssueUi(
                                        title = "Last known battery",
                                        message = "Showing last known battery from earlier.",
                                        actionLabel = "Retry scan",
                                        action = AirPodsPermissionAction.RETRY_SCAN,
                                    ),
                                ),
                        ),
                    actions = AirPodsDashboardActions(),
                )
            }
        }

        composeRule
            .onNodeWithText("Popup permission is off, so battery status will appear inside the app.")
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("AirPods detected, but battery level is unavailable right now.")
            .assertIsDisplayed()
        composeRule.onNodeWithText("Showing last known battery from earlier.").assertIsDisplayed()
        composeRule.onNodeWithText("Open Popup Settings").assertIsDisplayed()
        composeRule.onAllNodesWithText("Retry scan").assertCountEquals(2)
    }

    @Test
    fun successStateShowsSnapshotAndPopup() {
        val snapshot = snapshotUi()
        composeRule.setContent {
            AppleIconTheme {
                AirPodsDashboardScreen(
                    state =
                        baseState(
                            mode = AirPodsDashboardMode.SUCCESS,
                            snapshot = snapshot,
                            popup = AirPodsPopupUi(snapshot = snapshot, fallbackMessage = null),
                        ),
                    actions = AirPodsDashboardActions(),
                )
            }
        }

        composeRule.onNodeWithTag("battery_right_earbud").assertIsDisplayed()
        composeRule.onNodeWithTag("airpods_popup").assertIsDisplayed()
        composeRule.onNodeWithText("Close").assertIsDisplayed()
        composeRule.onAllNodesWithText("AirPods Pro").assertCountEquals(2)
        composeRule.onAllNodesWithText("Unknown").assertCountEquals(2)
    }

    private fun baseState(
        mode: AirPodsDashboardMode,
        statusTitle: String = "Ready to scan",
        isScanning: Boolean = false,
        snapshot: AirPodsSnapshotUi? = null,
        primaryIssue: AirPodsIssueUi? = null,
        fallbackIssues: List<AirPodsIssueUi> = emptyList(),
        popup: AirPodsPopupUi? = null,
    ): AirPodsDashboardUiState =
        AirPodsDashboardUiState(
            mode = mode,
            statusTitle = statusTitle,
            statusDetail = "Grant permissions, then open the case or use the test popup.",
            isScanning = isScanning,
            settings =
                AirPodsSettingsUi(
                    monitoringEnabled = true,
                    popupEnabled = true,
                    notificationEnabled = true,
                ),
            permissionRows =
                listOf(
                    AirPodsPermissionRowUi(
                        label = "Bluetooth permission",
                        status = "Granted",
                        detail = "AirPods scan access is available.",
                    ),
                    AirPodsPermissionRowUi(
                        label = "Bluetooth",
                        status = "On",
                        detail = "Device Bluetooth is available.",
                    ),
                    AirPodsPermissionRowUi(
                        label = "Popup",
                        status = "Ready",
                        detail = "Popup permission is available.",
                    ),
                    AirPodsPermissionRowUi(
                        label = "Notifications",
                        status = "Ready",
                        detail = "Notification fallback can be used.",
                    ),
                ),
            snapshot = snapshot,
            primaryIssue = primaryIssue,
            fallbackIssues = fallbackIssues,
            popup = popup,
        )

    private fun snapshotUi(): AirPodsSnapshotUi =
        AirPodsSnapshotUi(
            deviceName = "AirPods Pro",
            modelLabel = "AirPods Pro",
            connectionLabel = "Connected",
            sourceLabel = "Manual test",
            lastSeenLabel = "Last seen 2026-07-14 09:00 +10:00",
            staleLabel = null,
            batteries =
                listOf(
                    AirPodsBatteryUi(
                        label = "Left earbud",
                        value = "82%",
                        progress = 0.82f,
                        chargingLabel = "Not charging",
                        contentDescription = "Left earbud battery 82 percent, Not charging",
                    ),
                    AirPodsBatteryUi(
                        label = "Right earbud",
                        value = "Unknown",
                        progress = null,
                        chargingLabel = "Charging unknown",
                        contentDescription = "Right earbud battery unknown, Charging unknown",
                    ),
                    AirPodsBatteryUi(
                        label = "Case",
                        value = "64%",
                        progress = 0.64f,
                        chargingLabel = "Charging",
                        contentDescription = "Case battery 64 percent, Charging",
                    ),
                ),
        )
}
