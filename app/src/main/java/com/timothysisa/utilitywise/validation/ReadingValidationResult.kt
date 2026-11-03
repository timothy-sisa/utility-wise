package com.timothysisa.utilitywise.validation

import java.time.LocalDate

sealed interface ReadingValidationResult {

    /** Every field is valid; holds the parsed values ready to store. */
    data class Valid(
        val meterValue: Double,
        val date: LocalDate,
        val tariff: Double
    ) : ReadingValidationResult

    /** At least one field is not valid. A null error means that field is fine. */
    data class Invalid(
        val meterError: ReadingError?,
        val dateError: ReadingError?,
        val tariffError: ReadingError?
    ) : ReadingValidationResult
}
