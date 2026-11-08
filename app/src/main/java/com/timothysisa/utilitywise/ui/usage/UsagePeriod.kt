package com.timothysisa.utilitywise.ui.usage

import java.time.YearMonth
import java.time.format.DateTimeParseException

/** Encodes [period] as the ISO year-month text (for example "2026-10") carried in an intent. */
fun encodeUsagePeriod(period: YearMonth): String = period.toString()

/** Decodes text produced by [encodeUsagePeriod], or returns null if it is missing or malformed. */
fun decodeUsagePeriod(text: String?): YearMonth? =
    text?.let {
        try {
            YearMonth.parse(it)
        } catch (_: DateTimeParseException) {
            null
        }
    }
