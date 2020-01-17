package com.masiad.smartmeteo.utils

import android.content.SharedPreferences
import com.chibatching.kotpref.KotprefModel

/**
 * [SharedPreferences] saved data object representation
 */
object AppPreferences : KotprefModel() {
    var favouriteSensorId by intPref(default = -1)
}