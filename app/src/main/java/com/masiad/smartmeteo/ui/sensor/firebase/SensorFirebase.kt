package com.masiad.smartmeteo.ui.sensor.firebase

data class SensorFirebase(
    val timestamp: Long,
    val temperature: Float,
    val humidity: Float,
    val pm10: Float,
    val pm25: Float
)
