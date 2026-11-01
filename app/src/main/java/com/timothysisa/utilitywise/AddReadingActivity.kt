package com.timothysisa.utilitywise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.timothysisa.utilitywise.ui.theme.UtilityWiseTheme

class AddReadingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UtilityWiseTheme {
                Text(text = "Add Reading")
            }
        }
    }
}
