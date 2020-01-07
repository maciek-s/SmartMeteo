package com.masiad.smartmeteo.data

import androidx.lifecycle.LiveData

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

    suspend fun getSensorValues(favouriteId: Int): SensorValues {
        return sensorDao.getSensorValues(favouriteId)
    }

    suspend fun insertSensorValues(sensorValues: SensorValues): Long {
        return sensorDao.insertSensorValues(sensorValues)
    }
}