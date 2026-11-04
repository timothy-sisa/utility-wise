package com.timothysisa.utilitywise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.lifecycleScope
import com.timothysisa.utilitywise.ui.dashboard.DashboardScreen
import com.timothysisa.utilitywise.ui.dashboard.currentReportingMonth
import com.timothysisa.utilitywise.ui.dashboard.dashboardStateFor
import com.timothysisa.utilitywise.ui.theme.UtilityWiseTheme
import com.timothysisa.utilitywise.ui.theme.enableLightEdgeToEdge
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val snackbarHostState = SnackbarHostState()

    // Registered as a property so it exists before the activity starts, as the API requires.
    private val addReadingLauncher = registerForActivityResult(AddReadingContract()) { savedReadingId ->
        if (savedReadingId != null) {
            lifecycleScope.launch {
                snackbarHostState.showSnackbar(getString(R.string.dashboard_reading_saved))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableLightEdgeToEdge()
        setContent {
            UtilityWiseTheme {
                DashboardScreen(
                    state = dashboardStateFor(currentReportingMonth()),
                    snackbarHostState = snackbarHostState,
                    onAddReadingClick = { addReadingLauncher.launch(null) },
                    onReadingClick = { readingId -> addReadingLauncher.launch(readingId) },
                    onUsageClick = {},
                    onBillsClick = {}
                )
            }
        }
    }
}
