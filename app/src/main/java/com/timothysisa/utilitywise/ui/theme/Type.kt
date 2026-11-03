package com.timothysisa.utilitywise.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.timothysisa.utilitywise.R

val PlusJakartaSans = FontFamily(
    Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_extrabold, FontWeight.ExtraBold)
)

private val baseline = Typography()

val Typography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold),
    displayMedium = baseline.displayMedium.copy(fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold),
    displaySmall = baseline.displaySmall.copy(fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold),
    titleLarge = baseline.titleLarge.copy(fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold),
    titleMedium = baseline.titleMedium.copy(fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold),
    titleSmall = baseline.titleSmall.copy(fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = PlusJakartaSans, fontWeight = FontWeight.Medium),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = PlusJakartaSans, fontWeight = FontWeight.Medium),
    bodySmall = baseline.bodySmall.copy(fontFamily = PlusJakartaSans, fontWeight = FontWeight.Medium),
    labelLarge = baseline.labelLarge.copy(fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold),
    labelMedium = baseline.labelMedium.copy(fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold),
    labelSmall = baseline.labelSmall.copy(fontFamily = PlusJakartaSans, fontWeight = FontWeight.SemiBold)
)
