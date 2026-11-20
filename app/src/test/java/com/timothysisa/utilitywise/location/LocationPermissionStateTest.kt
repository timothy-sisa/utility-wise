package com.timothysisa.utilitywise.location

import android.Manifest
import org.junit.Assert.assertEquals
import org.junit.Test

class LocationPermissionStateTest {

    @Test
    fun bothGrantedIsGranted() {
        val results = mapOf(
            Manifest.permission.ACCESS_FINE_LOCATION to true,
            Manifest.permission.ACCESS_COARSE_LOCATION to true
        )

        assertEquals(LocationPermissionState.Granted, locationPermissionStateFrom(results))
    }

    @Test
    fun onlyCoarseGrantedIsGranted() {
        val results = mapOf(
            Manifest.permission.ACCESS_FINE_LOCATION to false,
            Manifest.permission.ACCESS_COARSE_LOCATION to true
        )

        assertEquals(LocationPermissionState.Granted, locationPermissionStateFrom(results))
    }

    @Test
    fun noneGrantedIsDenied() {
        val results = mapOf(
            Manifest.permission.ACCESS_FINE_LOCATION to false,
            Manifest.permission.ACCESS_COARSE_LOCATION to false
        )

        assertEquals(LocationPermissionState.Denied, locationPermissionStateFrom(results))
    }
}
