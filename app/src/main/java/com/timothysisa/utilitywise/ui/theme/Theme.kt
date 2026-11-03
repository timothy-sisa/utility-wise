package com.timothysisa.utilitywise.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// The app has a single light look, so one scheme is used regardless of the system
// setting and dynamic colour is not applied.
private val UtilityWiseColorScheme = lightColorScheme(
    primary = Ink,
    onPrimary = Lime,
    secondary = Lime,
    onSecondary = Ink,
    background = Mint,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = Mint,
    onSurfaceVariant = Mute,
    surfaceContainer = Paper,
    inverseSurface = Ink,
    inverseOnSurface = Paper,
    outline = Mute,
    outlineVariant = Line
)

@Composable
fun UtilityWiseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = UtilityWiseColorScheme,
        typography = Typography,
        content = content
    )
}
