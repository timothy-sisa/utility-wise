package com.timothysisa.utilitywise.model

import java.time.LocalDate

data class Bill(
    val id: Long,
    val type: UtilityType,
    val amount: Double,
    val periodLabel: String,
    val dueDate: LocalDate,
    val isPaid: Boolean,
    val sharedWith: Set<Long>
)
