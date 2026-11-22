package com.timothysisa.utilitywise.location

data class Coordinates(val latitude: Double, val longitude: Double)

interface LocationProvider {
    suspend fun currentLocation(): Coordinates?
}
