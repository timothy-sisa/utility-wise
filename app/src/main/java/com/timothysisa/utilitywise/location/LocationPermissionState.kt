package com.timothysisa.utilitywise.location

import android.Manifest

/**
 * Both location permissions, requested together. Since Android 12 the system dialog lets the
 * user choose precise or approximate access only when both are asked for in one request.
 */
val LOCATION_PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

/** Whether the app may read the device location. */
sealed interface LocationPermissionState {
    data object Granted : LocationPermissionState
    data object Denied : LocationPermissionState
}

/**
 * Maps permission results, keyed by permission name, to a [LocationPermissionState].
 * Approximate access counts as granted: telling which house the user is in needs a rough
 * position, and treating it as denied would punish users who chose the more private option.
 */
fun locationPermissionStateFrom(results: Map<String, Boolean>): LocationPermissionState =
    if (LOCATION_PERMISSIONS.any { results[it] == true }) {
        LocationPermissionState.Granted
    } else {
        LocationPermissionState.Denied
    }
