package com.timothysisa.utilitywise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.timothysisa.utilitywise.ui.dashboard.DashboardScreen
import com.timothysisa.utilitywise.ui.dashboard.currentReportingMonth
import com.timothysisa.utilitywise.ui.dashboard.dashboardStateFor
import com.timothysisa.utilitywise.ui.theme.UtilityWiseTheme
import com.timothysisa.utilitywise.ui.theme.enableLightEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableLightEdgeToEdge()
        setContent {
            UtilityWiseTheme {
                DashboardScreen(
                    state = dashboardStateFor(currentReportingMonth()),
                    onRefreshClick = {},
                    onShareClick = {},
                    onSeeAllReadingsClick = {},
                    onAddReadingClick = {},
                    onUsageClick = {},
                    onBillsClick = {}
                )
            }
        }
    }
}
