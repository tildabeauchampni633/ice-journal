package com.Faxzoku9.Qozcud.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val IceColorScheme = darkColorScheme(
    primary = ClearIce,
    onPrimary = NightIce,
    primaryContainer = FrostSurface,
    onPrimaryContainer = IceWhite,
    secondary = Lantern,
    onSecondary = DeepLake,
    secondaryContainer = LanternDeep,
    onSecondaryContainer = NightIce,
    tertiary = IceCyan,
    onTertiary = NightIce,
    background = DeepLake,
    onBackground = IceWhite,
    surface = FrostSurface,
    onSurface = IceWhite,
    surfaceVariant = FrostSurfaceHi,
    onSurfaceVariant = TextMuted,
    error = Danger,
    onError = NightIce,
    outline = FrostSurfaceHi,
    outlineVariant = FrostSurface,
    scrim = NightIce
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = IceColorScheme,
        typography = AppTypography,
        content = content
    )
}
