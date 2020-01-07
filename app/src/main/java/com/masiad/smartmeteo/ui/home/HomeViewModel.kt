package com.masiad.smartmeteo.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.masiad.smartmeteo.data.AppRoomDatabase
import com.masiad.smartmeteo.data.Sensor
import com.masiad.smartmeteo.data.SensorRepository
import com.masiad.smartmeteo.data.SensorValues
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        val TAG: String = HomeViewModel::class.java.simpleName
    }

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: SensorRepository

    private val sensorValues = MutableLiveData<SensorValues>()

    init {
        // Gets reference to SensorDao from appRoomDatabase to construct
        // the correct SensorRepository.
        val sensorDao = AppRoomDatabase.getDatabase(application, viewModelScope).sensorDao()
        repository = SensorRepository(sensorDao)
    }

    fun setFavouriteSensorId(favouriteSensorId: Int) {
        viewModelScope.launch {
            sensorValues.value = repository.getSensorValues(favouriteSensorId)
        }
    }

    fun getSensorValuesLiveData(): LiveData<SensorValues> {
        return sensorValues
    }


}