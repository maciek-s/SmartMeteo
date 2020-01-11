package com.masiad.smartmeteo.data

import com.chibatching.kotpref.KotprefModel

object AppKotpref: KotprefModel() {
    var favouriteSensorId by intPref(default = -1)
}