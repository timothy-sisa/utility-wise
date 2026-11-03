package com.timothysisa.utilitywise.ui.theme

import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge

/** Draws the activity behind transparent system bars that suit the app's light theme. */
fun ComponentActivity.enableLightEdgeToEdge() {
    // The app is light only, so the system bar icons stay dark even in system dark mode.
    enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
    )
}
