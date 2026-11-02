package com.timothysisa.utilitywise.model

import java.time.LocalDate

data class Reading(
    val id: Long,
    val type: UtilityType,
    val value: Double,
    val date: LocalDate,
    val tariff: Double,
    val photoUri: String? = null
)
