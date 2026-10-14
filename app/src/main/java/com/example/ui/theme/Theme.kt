package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SleekBrightBlueColorScheme = lightColorScheme(
    primary = PythonBlue,
    onPrimary = Color.White,
    primaryContainer = SleekCanvasBg,
    onPrimaryContainer = PythonBlueDark,
    secondary = PythonBlueDark,
    onSecondary = Color.White,
    secondaryContainer = SleekNavyCard,
    onSecondaryContainer = TextPrimary,
    tertiary = AccentEmerald,
    onTertiary = Color.White,
    background = SleekNavyBg,
    onBackground = TextPrimary,
    surface = SleekNavySurface,
    onSurface = TextPrimary,
    surfaceVariant = SleekNavyCard,
    onSurfaceVariant = TextSecondary,
    outline = SleekNavyBorder,
    outlineVariant = SleekNavyBorderSubtle,
    error = AccentCoral,
    onError = Color.White
)

@Composable
fun PythonDataLabTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SleekBrightBlueColorScheme,
        typography = Typography,
        content = content
    )
}

// Alias for compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    PythonDataLabTheme(darkTheme = darkTheme, content = content)
}
