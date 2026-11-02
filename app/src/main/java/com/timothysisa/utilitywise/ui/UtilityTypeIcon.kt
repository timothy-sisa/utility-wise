package com.timothysisa.utilitywise.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.ui.graphics.vector.ImageVector
import com.timothysisa.utilitywise.model.UtilityType

val UtilityType.icon: ImageVector
    get() = when (this) {
        UtilityType.ELECTRICITY -> Icons.Rounded.Bolt
        UtilityType.GAS -> Icons.Rounded.LocalFireDepartment
        UtilityType.BROADBAND -> Icons.Rounded.Wifi
    }
