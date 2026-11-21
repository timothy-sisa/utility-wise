package com.timothysisa.utilitywise.location

import com.timothysisa.utilitywise.model.House
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// Larger than typical consumer GPS error, smaller than the gap between neighbouring houses.
const val DEFAULT_MAX_METRES = 200.0

private const val EARTH_RADIUS_METRES = 6_371_000.0

fun nearestHouse(
    latitude: Double,
    longitude: Double,
    houses: List<House>,
    maxMetres: Double = DEFAULT_MAX_METRES
): House? = houses
    .minByOrNull { haversineMetres(latitude, longitude, it.latitude, it.longitude) }
    ?.takeIf { haversineMetres(latitude, longitude, it.latitude, it.longitude) <= maxMetres }

private fun haversineMetres(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2) +
        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
    return 2 * EARTH_RADIUS_METRES * asin(sqrt(a))
}
