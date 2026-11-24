package com.timothysisa.utilitywise.location

import com.timothysisa.utilitywise.model.House
import com.timothysisa.utilitywise.model.UtilityType

fun tariffFor(house: House?, type: UtilityType): Double? = house?.tariffs?.get(type)
