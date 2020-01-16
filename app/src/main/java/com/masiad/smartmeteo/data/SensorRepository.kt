package com.masiad.smartmeteo.data

import androidx.lifecycle.LiveData

/**
 * [SensorDao] Repository
 */
class SensorRepository(private val sensorDao: SensorDao) {
    val allSensors: LiveData<List<Sensor>> = sensorDao.getAll()

    suspend fun insert(sensor: Sensor): Long {
        return sensorDao.insert(sensor)
    }

    suspend fun loadById(sensorID: Int): Sensor {
        return sensorDao.loadById(sensorID)
    }

    suspend fun deleteById(sensorID: Int) {
        return sensorDao.deleteById(sensorID)
    }
}