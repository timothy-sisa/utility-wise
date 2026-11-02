package com.timothysisa.utilitywise.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// The app is dark only, so one scheme is used regardless of the system setting
// and dynamic colour is not applied.
private val UtilityWiseColorScheme = darkColorScheme(
    primary = Wheat,
    onPrimary = Evergreen,
    secondary = Mint,
    onSecondary = Evergreen,
    background = Evergreen,
    onBackground = Parchment,
    surface = ForestGreen,
    onSurface = Parchment,
    surfaceVariant = PineGreen,
    onSurfaceVariant = Sage,
    surfaceContainer = ForestGreen,
    outline = MossGreen,
    outlineVariant = MossGreen
)

@Composable
fun UtilityWiseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = UtilityWiseColorScheme,
        typography = Typography,
        content = content
    )
}
