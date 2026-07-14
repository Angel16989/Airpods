package com.angel16989.appleicon.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppleIconLightColorScheme =
    lightColorScheme(
        primary = Color(0xFF2457A6),
        onPrimary = Color(0xFFFFFFFF),
        secondary = Color(0xFF0F766E),
        tertiary = Color(0xFF854D0E),
        background = Color(0xFFF7F8FA),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFE6EAF0),
        onBackground = Color(0xFF17202A),
        onSurface = Color(0xFF17202A),
        onSurfaceVariant = Color(0xFF526071),
        error = Color(0xFFB42318),
    )

private val AppleIconDarkColorScheme =
    darkColorScheme(
        primary = Color(0xFF9DC2FF),
        onPrimary = Color(0xFF082C5D),
        secondary = Color(0xFF67D7CE),
        tertiary = Color(0xFFF0B15D),
        background = Color(0xFF101418),
        surface = Color(0xFF181D23),
        surfaceVariant = Color(0xFF313943),
        onBackground = Color(0xFFE7EAEE),
        onSurface = Color(0xFFE7EAEE),
        onSurfaceVariant = Color(0xFFB8C1CC),
        error = Color(0xFFFFB4AB),
    )

@Composable
fun AppleIconTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme =
            if (darkTheme) {
                AppleIconDarkColorScheme
            } else {
                AppleIconLightColorScheme
            },
        content = content,
    )
}
