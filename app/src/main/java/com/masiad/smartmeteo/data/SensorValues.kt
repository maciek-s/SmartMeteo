package com.masiad.smartmeteo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "senor_values_table")
data class SensorValues(
    @PrimaryKey(autoGenerate = false)
    val favouriteId: Int = 0,
    @ColumnInfo(name = "name") val name: String = "",
    @ColumnInfo(name = "timestamp") val timestamp: Long = 0L,
    @ColumnInfo(name = "temperature") val temperature: Float = 0f,
    @ColumnInfo(name = "humidity") val humidity: Float = 0f,
    @ColumnInfo(name = "pm10") val pm10: Float = 0f,
    @ColumnInfo(name = "pm25") val pm25: Float = 0f
)