package com.timothysisa.utilitywise

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.timothysisa.utilitywise.ui.dashboard.DashboardScreen
import com.timothysisa.utilitywise.ui.dashboard.currentReportingMonth
import com.timothysisa.utilitywise.ui.dashboard.dashboardStateFor
import com.timothysisa.utilitywise.ui.theme.UtilityWiseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The app is dark only, so the system bar icons stay light even in system light mode.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            UtilityWiseTheme {
                DashboardScreen(
                    state = dashboardStateFor(currentReportingMonth()),
                    onNotificationsClick = {},
                    onSeeAllReadingsClick = {},
                    onAddReadingClick = {},
                    onUsageClick = {},
                    onBillsClick = {}
                )
            }
        }
    }
}
