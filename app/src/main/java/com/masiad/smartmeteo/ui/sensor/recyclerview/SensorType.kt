package com.masiad.smartmeteo.ui.sensor.recyclerview

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.masiad.smartmeteo.R

/**
 * Sensor Type helper class
 */
enum class SensorType(@DrawableRes val resourceId: Int, @StringRes val nameId: Int, @StringRes val unitId: Int) {
    TEMPERATURE(R.drawable.thermometer_icon, R.string.temp, R.string.temp_unit),
    HUMIDITY(R.drawable.humidity_icon, R.string.humid, R.string.humid_unit),
    PM10(R.drawable.pm10_icon, R.string.pm10, R.string.pm10_unit),
    PM25(R.drawable.pm25_icon, R.string.pm25, R.string.pm25_unit);
}