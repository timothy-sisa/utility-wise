package com.timothysisa.utilitywise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.timothysisa.utilitywise.data.UtilityRepository
import com.timothysisa.utilitywise.model.Reading
import com.timothysisa.utilitywise.ui.addreading.AddReadingRoute
import com.timothysisa.utilitywise.ui.theme.UtilityWiseTheme
import com.timothysisa.utilitywise.ui.theme.enableLightEdgeToEdge

class AddReadingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableLightEdgeToEdge()
        setContent {
            UtilityWiseTheme {
                AddReadingRoute(
                    readingToEdit = null,
                    onSave = ::saveReading,
                    onBackClick = ::finish
                )
            }
        }
    }

    private fun saveReading(reading: Reading) {
        UtilityRepository.addReading(reading)
        finish()
    }
}
