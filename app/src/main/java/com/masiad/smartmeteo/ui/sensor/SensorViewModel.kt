package com.masiad.smartmeteo.ui.sensor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.masiad.smartmeteo.data.AppRoomDatabase
import com.masiad.smartmeteo.data.Sensor
import com.masiad.smartmeteo.data.SensorRepository
import kotlinx.coroutines.launch

class SensorViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: SensorRepository
    init {
        // Gets reference to SensorDao from appRoomDatabase to construct
        // the correct SensorRepository.
        val sensorDao = AppRoomDatabase.getDatabase(application, viewModelScope).sensorDao()
        repository = SensorRepository(sensorDao)
    }

    val currentTemperature = MutableLiveData<Float>()
    val currentHumidity = MutableLiveData<Float>()

    val sensor = MutableLiveData<Sensor>()

    fun setSensor(sensorID: Int) = viewModelScope.launch {
        sensor.value = repository.loadById(sensorID)

        println("Sensor set: ${sensor.value?.serialNumber}")
    }

    fun setCurrentTemperature(temp: Float) {
        currentTemperature.value = temp
    }

    fun setCurrentHumidity(humidity: Float) {
        currentHumidity.value = humidity
    }

}
