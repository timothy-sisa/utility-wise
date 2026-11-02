package com.timothysisa.utilitywise.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.timothysisa.utilitywise.R

val Nunito = FontFamily(
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_semibold, FontWeight.SemiBold),
    Font(R.font.nunito_bold, FontWeight.Bold),
    Font(R.font.nunito_extrabold, FontWeight.ExtraBold),
    Font(R.font.nunito_black, FontWeight.Black)
)

private val baseline = Typography()

val Typography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = Nunito, fontWeight = FontWeight.Black),
    displayMedium = baseline.displayMedium.copy(fontFamily = Nunito, fontWeight = FontWeight.Black),
    displaySmall = baseline.displaySmall.copy(fontFamily = Nunito, fontWeight = FontWeight.Black),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = Nunito, fontWeight = FontWeight.ExtraBold),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = Nunito, fontWeight = FontWeight.ExtraBold),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = Nunito, fontWeight = FontWeight.ExtraBold),
    titleLarge = baseline.titleLarge.copy(fontFamily = Nunito, fontWeight = FontWeight.ExtraBold),
    titleMedium = baseline.titleMedium.copy(fontFamily = Nunito, fontWeight = FontWeight.Bold),
    titleSmall = baseline.titleSmall.copy(fontFamily = Nunito, fontWeight = FontWeight.Bold),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = Nunito, fontWeight = FontWeight.SemiBold),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = Nunito, fontWeight = FontWeight.SemiBold),
    bodySmall = baseline.bodySmall.copy(fontFamily = Nunito, fontWeight = FontWeight.SemiBold),
    labelLarge = baseline.labelLarge.copy(fontFamily = Nunito, fontWeight = FontWeight.Bold),
    labelMedium = baseline.labelMedium.copy(fontFamily = Nunito, fontWeight = FontWeight.Bold),
    labelSmall = baseline.labelSmall.copy(fontFamily = Nunito, fontWeight = FontWeight.Bold)
)
