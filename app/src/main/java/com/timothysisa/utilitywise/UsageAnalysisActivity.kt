package com.timothysisa.utilitywise

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.timothysisa.utilitywise.ui.dashboard.currentReportingMonth
import com.timothysisa.utilitywise.ui.theme.UtilityWiseTheme
import com.timothysisa.utilitywise.ui.theme.enableLightEdgeToEdge
import com.timothysisa.utilitywise.ui.usage.UsageAnalysisScreen
import com.timothysisa.utilitywise.ui.usage.decodeUsagePeriod
import com.timothysisa.utilitywise.ui.usage.encodeUsagePeriod
import com.timothysisa.utilitywise.ui.usage.usageAnalysisStateFor
import java.time.YearMonth

class UsageAnalysisActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableLightEdgeToEdge()
        // Falls back to the dashboard's reporting month when opened without a period.
        val period = decodeUsagePeriod(intent.getStringExtra(EXTRA_PERIOD))
            ?: currentReportingMonth()
        setContent {
            UtilityWiseTheme {
                UsageAnalysisScreen(state = usageAnalysisStateFor(period))
            }
        }
    }

    companion object {
        const val EXTRA_PERIOD = "com.timothysisa.utilitywise.extra.PERIOD"

        /** Creates an intent that opens Usage Analysis for the selected [period]. */
        fun newIntent(context: Context, period: YearMonth): Intent =
            Intent(context, UsageAnalysisActivity::class.java)
                .putExtra(EXTRA_PERIOD, encodeUsagePeriod(period))
    }
}
