package com.timothysisa.utilitywise.validation

/** A reason a field on the reading form is not valid. */
sealed interface ReadingError {
    data object Required : ReadingError
    data object NotANumber : ReadingError
    data object NotPositive : ReadingError
    data object InFuture : ReadingError
    data class BelowPrevious(val previousValue: Double) : ReadingError
    data class AboveNext(val nextValue: Double) : ReadingError
}
