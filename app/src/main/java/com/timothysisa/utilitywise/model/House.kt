package com.timothysisa.utilitywise.model

data class House(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val tariffs: Map<UtilityType, Double>
)
