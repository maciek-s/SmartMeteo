package com.masiad.smartmeteo.ui.sensors_list

import android.app.Application
import androidx.lifecycle.*
import com.masiad.smartmeteo.data.Sensor
import com.masiad.smartmeteo.data.SensorRepository
import com.masiad.smartmeteo.data.AppRoomDatabase
import kotlinx.coroutines.launch

class SensorsListViewModel(application: Application) : AndroidViewModel(application) {

    private val _text = MutableLiveData<String>().apply {
        value = "This is sensors list Fragment"
    }
    val text: LiveData<String> = _text

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

}