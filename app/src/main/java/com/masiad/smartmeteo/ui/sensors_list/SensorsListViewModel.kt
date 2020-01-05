package com.masiad.smartmeteo.ui.sensors_list

import android.app.Application
import androidx.lifecycle.*
import com.masiad.smartmeteo.data.Sensor
import com.masiad.smartmeteo.data.SensorRepository
import com.masiad.smartmeteo.data.AppRoomDatabase
import com.masiad.smartmeteo.ui.sensor.SensorViewModel
import kotlinx.coroutines.launch

class SensorsListViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        val TAG: String = SensorsListViewModel::class.java.simpleName
    }

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: SensorRepository
    // LiveData gives us updated words when they change.
    val allSensors: LiveData<List<Sensor>>

    init {
        // Gets reference to SensorDao from appRoomDatabase to construct
        // the correct SensorRepository.
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

}