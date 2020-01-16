package com.masiad.smartmeteo.ui.sensor.recyclerview

import com.github.mikephil.charting.data.Entry

/**
 * RecyclerView card item sensor representation
 */
data class SensorCard(
    val sensorType: SensorType,
    var currentValue: Float = 0f,
    var avgSum: Float = 0f,
    var min: Float = 0f,
    var max: Float = 0f,
    var chartValues: MutableList<Entry> = mutableListOf()
)