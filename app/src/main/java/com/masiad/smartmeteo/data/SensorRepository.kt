package com.masiad.smartmeteo.data

import androidx.lifecycle.LiveData

class SensorRepository(private val sensorDao: SensorDao) {
    val allSensors: LiveData<List<Sensor>> = sensorDao.getAll()

    suspend fun insert(sensor: Sensor) {
        sensorDao.insert(sensor)
    }
}