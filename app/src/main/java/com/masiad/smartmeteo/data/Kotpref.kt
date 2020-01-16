package com.masiad.smartmeteo.data

import android.content.SharedPreferences
import com.chibatching.kotpref.KotprefModel

/**
 * [SharedPreferences] saved data object representation
 */
object AppKotpref : KotprefModel() {
    var favouriteSensorId by intPref(default = -1)
}