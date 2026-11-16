package com.timothysisa.utilitywise

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.timothysisa.utilitywise.sensor.ShakeSensor
import com.timothysisa.utilitywise.ui.dashboard.DashboardScreen
import com.timothysisa.utilitywise.ui.dashboard.currentReportingMonth
import com.timothysisa.utilitywise.ui.dashboard.dashboardStateFor
import com.timothysisa.utilitywise.ui.theme.UtilityWiseTheme
import com.timothysisa.utilitywise.ui.theme.enableLightEdgeToEdge
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val snackbarHostState = SnackbarHostState()

    // The dashboard already recomposes when repository readings change, but the reporting month
    // falls back to the clock, which Compose cannot observe. Bumping this forces a recompute.
    private var dashboardRefreshCount by mutableIntStateOf(0)

    // Lazy because system services are not available until the activity is attached to a context.
    private val shakeSensor by lazy { ShakeSensor(this, onShake = ::refreshDashboard) }

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
                val dashboardState = key(dashboardRefreshCount) {
                    dashboardStateFor(currentReportingMonth())
                }
                DashboardScreen(
                    state = dashboardState,
                    snackbarHostState = snackbarHostState,
                    onAddReadingClick = { addReadingLauncher.launch(null) },
                    onReadingClick = { readingId -> addReadingLauncher.launch(readingId) },
                    onUsageClick = {
                        startActivity(
                            UsageAnalysisActivity.newIntent(this@MainActivity, currentReportingMonth())
                        )
                    },
                    onBillsClick = { startActivity(BillManagementActivity.newIntent(this@MainActivity)) },
                    onRefreshClick = ::refreshDashboard
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        shakeSensor.start()
    }

    override fun onPause() {
        shakeSensor.stop()
        super.onPause()
    }

    private fun refreshDashboard() {
        dashboardRefreshCount++
        lifecycleScope.launch {
            // Replace rather than queue, so several quick refreshes show one message, not a backlog.
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(getString(R.string.dashboard_refreshed))
        }
    }

    companion object {
        /** Creates an intent that returns to the existing dashboard when one is already present. */
        fun newIntent(context: Context): Intent =
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
    }
}
