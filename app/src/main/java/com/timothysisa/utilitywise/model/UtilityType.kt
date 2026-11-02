package com.timothysisa.utilitywise.model

import androidx.annotation.StringRes
import com.timothysisa.utilitywise.R

enum class UtilityType(
    @param:StringRes val displayNameRes: Int,
    @param:StringRes val unitRes: Int?
) {
    ELECTRICITY(R.string.utility_electricity, R.string.unit_kilowatt_hours),
    GAS(R.string.utility_gas, R.string.unit_kilowatt_hours),
    BROADBAND(R.string.utility_broadband, null);

    val hasMeter: Boolean
        get() = unitRes != null
}
