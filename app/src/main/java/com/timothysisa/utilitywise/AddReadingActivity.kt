package com.timothysisa.utilitywise

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
        val readingId = readingIdFrom(intent)
        val readingToEdit = readingId?.let(UtilityRepository::getReadingById)
        if (readingId != null && readingToEdit == null) {
            Toast.makeText(this, R.string.add_reading_not_found, Toast.LENGTH_SHORT).show()
            cancel()
            return
        }
        enableLightEdgeToEdge()
        setContent {
            UtilityWiseTheme {
                AddReadingRoute(
                    readingToEdit = readingToEdit,
                    onSave = { reading -> saveReading(reading, isEditing = readingToEdit != null) },
                    onBackClick = ::cancel
                )
            }
        }
    }

    private fun saveReading(reading: Reading, isEditing: Boolean) {
        if (isEditing) {
            UtilityRepository.updateReading(reading)
        } else {
            UtilityRepository.addReading(reading)
        }
        setResult(RESULT_OK, Intent().putExtra(EXTRA_READING_ID, reading.id))
        finish()
    }

    private fun cancel() {
        setResult(RESULT_CANCELED)
        finish()
    }

    companion object {
        const val EXTRA_READING_ID = "com.timothysisa.utilitywise.extra.READING_ID"

        /**
         * Creates an intent that opens the reading form: empty when [readingId] is null,
         * otherwise filled in with that reading for editing.
         */
        fun newIntent(context: Context, readingId: Long? = null): Intent =
            Intent(context, AddReadingActivity::class.java).apply {
                readingId?.let { putExtra(EXTRA_READING_ID, it) }
            }

        /** Returns the reading id carried by [intent], or null if it has none. */
        fun readingIdFrom(intent: Intent?): Long? =
            // getLongExtra needs a default, so hasExtra tells a missing id apart from that default.
            intent?.takeIf { it.hasExtra(EXTRA_READING_ID) }?.getLongExtra(EXTRA_READING_ID, 0L)
    }
}
