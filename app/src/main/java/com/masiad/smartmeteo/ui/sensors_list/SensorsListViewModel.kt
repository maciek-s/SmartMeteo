package com.masiad.smartmeteo.ui.sensors_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.masiad.smartmeteo.data.AppRoomDatabase
import com.masiad.smartmeteo.data.Sensor
import com.masiad.smartmeteo.data.SensorRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * ViewModel for [SensorsListFragment]
 */
class SensorsListViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        val TAG: String = SensorsListViewModel::class.java.simpleName
    }

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: SensorRepository
    private val allSensors: LiveData<List<Sensor>>

    init {
        // Gets reference to SensorDao from appRoomDatabase to construct the correct SensorRepository.
        val sensorDao = AppRoomDatabase.getDatabase(application, viewModelScope).sensorDao()
        repository = SensorRepository(sensorDao)
        allSensors = repository.allSensors
    }

    fun insert(sensor: Sensor) = viewModelScope.launch {
        repository.insert(sensor)
    }

    fun deleteById(sensorId: Int) = viewModelScope.launch {
        repository.deleteById(sensorId)
    }

    fun getAllSensorsLiveData(): LiveData<List<Sensor>> {
        return allSensors
    }

    fun isNameAlreadyInserted(name: String): Boolean {
        var sensorNameExists: Sensor? = null
        runBlocking {
            sensorNameExists = repository.loadByName(name)
        }
        return sensorNameExists != null
    }
}