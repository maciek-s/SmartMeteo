package com.masiad.smartmeteo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [AppRoomDatabase] POJO object
 */
@Entity(tableName = "sensors_table")
data class Sensor(
    @PrimaryKey(autoGenerate = true)
    val sensorId: Int = 0,
    @ColumnInfo(name = "sensor_name") val sensorName: String?,
    @ColumnInfo(name = "serial_number") val serialNumber: String?
)