package com.timothysisa.utilitywise

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

/**
 * Opens [AddReadingActivity] and returns the id of the reading it saved.
 *
 * The input is the id of a reading to edit, or null to add a new one. The output is the
 * saved reading's id, or null if the user left without saving.
 */
class AddReadingContract : ActivityResultContract<Long?, Long?>() {

    override fun createIntent(context: Context, input: Long?): Intent =
        AddReadingActivity.newIntent(context, input)

    override fun parseResult(resultCode: Int, intent: Intent?): Long? =
        if (resultCode == Activity.RESULT_OK) AddReadingActivity.readingIdFrom(intent) else null
}
