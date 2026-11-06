package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = RadarCyan,
    onPrimary = Color(0xFF00363F),
    primaryContainer = Color(0xFF004E5B),
    onPrimaryContainer = Color(0xFFA6EEFF),
    secondary = RadarGold,
    onSecondary = Color(0xFF452B00),
    secondaryContainer = Color(0xFF623F00),
    onSecondaryContainer = Color(0xFFFFDDB8),
    tertiary = SignalGreen,
    onTertiary = Color(0xFF003919),
    tertiaryContainer = Color(0xFF005327),
    onTertiaryContainer = Color(0xFF6CFFA0),
    background = RadarBackground,
    onBackground = TextPrimary,
    surface = RadarSurface,
    onSurface = TextPrimary,
    surfaceVariant = RadarSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = RadarBorder,
    error = SignalRed,
    onError = Color.White
)

private val LightColorScheme = DarkColorScheme // Always maintain dark financial radar aesthetic for charts and signals

@Composable
fun IlkhanateRadarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
