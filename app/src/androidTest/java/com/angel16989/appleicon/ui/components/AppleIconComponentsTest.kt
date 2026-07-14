package com.angel16989.appleicon.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.angel16989.appleicon.ui.theme.AppleIconTheme
import org.junit.Rule
import org.junit.Test

class AppleIconComponentsTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun buttonShowsLoadingState() {
        composeRule.setContent {
            AppleIconTheme {
                AppleIconButton(
                    label = "Continue",
                    loading = true,
                    onClick = {},
                )
            }
        }

        composeRule.onNodeWithText("Continue").assertIsDisplayed()
        composeRule.onNodeWithTag("apple_icon_button_loading").assertIsDisplayed()
    }

    @Test
    fun textFieldShowsSupportingText() {
        composeRule.setContent {
            AppleIconTheme {
                AppleIconTextField(
                    value = "",
                    onValueChange = {},
                    label = "Device label",
                    supportingText = "Optional",
                )
            }
        }

        composeRule.onNodeWithText("Device label").assertIsDisplayed()
        composeRule.onNodeWithText("Optional").assertIsDisplayed()
    }

    @Test
    fun emptyAndErrorStatesRenderCopy() {
        composeRule.setContent {
            AppleIconTheme {
                Column {
                    AppleIconEmptyState(
                        title = "Nothing here",
                        message = "Try again later.",
                    )
                    AppleIconErrorState(
                        title = "Bluetooth off",
                        message = "Turn on Bluetooth.",
                    )
                }
            }
        }

        composeRule.onNodeWithTag("apple_icon_empty_state").assertIsDisplayed()
        composeRule.onNodeWithText("Bluetooth off").assertIsDisplayed()
    }
}
