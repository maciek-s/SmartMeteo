package com.masiad.smartmeteo.ui.sensor

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.masiad.smartmeteo.data.AppRoomDatabase
import com.masiad.smartmeteo.data.Sensor
import com.masiad.smartmeteo.data.SensorRepository
import kotlinx.coroutines.launch

class SensorViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        val TAG: String = SensorViewModel::class.java.simpleName
    }

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: SensorRepository

    val sensor = MutableLiveData<Sensor>()

    private val timestampLiveDataList = MutableLiveData<MutableList<Long>>()
    private val temperatureLiveDataList = MutableLiveData<MutableList<Float>>()
    private val humidityLiveDataList = MutableLiveData<MutableList<Float>>()
    private val pm10LiveDataList = MutableLiveData<MutableList<Float>>()
    private val pm25LiveDataList = MutableLiveData<MutableList<Float>>()

    init {
        // Gets reference to SensorDao from appRoomDatabase to construct
        // the correct SensorRepository.
        val sensorDao = AppRoomDatabase.getDatabase(application, viewModelScope).sensorDao()
        repository = SensorRepository(sensorDao)

        temperatureLiveDataList.value = mutableListOf()
        timestampLiveDataList.value = mutableListOf()
        humidityLiveDataList.value = mutableListOf()
        pm10LiveDataList.value = mutableListOf()
        pm25LiveDataList.value = mutableListOf()
    }

    fun setSensor(sensorID: Int) = viewModelScope.launch {
        sensor.value = repository.loadById(sensorID)

        Log.i(TAG, "Sensor set serial: ${sensor.value?.serialNumber}")
    }

    fun getTimestampLiveData(): MutableLiveData<MutableList<Long>> {
        return timestampLiveDataList
    }

    fun getTemperatureLiveData(): MutableLiveData<MutableList<Float>> {
        return temperatureLiveDataList
    }

    fun getHumidityLiveData(): MutableLiveData<MutableList<Float>> {
        return humidityLiveDataList
    }

    fun getPM10LiveData(): MutableLiveData<MutableList<Float>> {
        return pm10LiveDataList
    }

    fun getPM25LiveData(): MutableLiveData<MutableList<Float>> {
        return pm25LiveDataList
    }

    fun addCurrentTimestamp(timestamp: Long) {
        timestampLiveDataList.value?.add(timestamp)
        timestampLiveDataList.value = timestampLiveDataList.value
    }

    fun addCurrentTemperature(temp: Float) {
        temperatureLiveDataList.value?.add(temp)
        temperatureLiveDataList.value = temperatureLiveDataList.value
    }

    fun addCurrentHumidity(humidity: Float) {
        humidityLiveDataList.value?.add(humidity)
        humidityLiveDataList.value = humidityLiveDataList.value
    }

    fun addCurrentPM10(pm10: Float) {
        pm10LiveDataList.value?.add(pm10)
        pm10LiveDataList.value = pm10LiveDataList.value
    }

    fun addCurrentPM25(pm25: Float) {
        pm25LiveDataList.value?.add(pm25)
        pm25LiveDataList.value = pm25LiveDataList.value
    }

}
