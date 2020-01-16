package com.masiad.smartmeteo.ui.sensor

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.masiad.smartmeteo.data.AppRoomDatabase
import com.masiad.smartmeteo.data.Sensor
import com.masiad.smartmeteo.data.SensorRepository
import com.masiad.smartmeteo.ui.sensor.firebase.SensorFirebase
import com.masiad.smartmeteo.ui.sensor.recyclerview.SensorCard
import com.masiad.smartmeteo.ui.sensor.recyclerview.SensorType
import com.masiad.smartmeteo.utils.FIREBASE_HUMIDITY_KEY
import com.masiad.smartmeteo.utils.FIREBASE_PM10_KEY
import com.masiad.smartmeteo.utils.FIREBASE_PM25_KEY
import com.masiad.smartmeteo.utils.FIREBASE_TEMPERATURE_KEY
import kotlinx.coroutines.launch

class SensorViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        val TAG: String = SensorViewModel::class.java.simpleName
    }

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: SensorRepository

    var isFavourite = false

    val sensor = MutableLiveData<Sensor>()

    val sensorFirebaseValues: MutableLiveData<List<SensorFirebase>> = MutableLiveData()

    val sensorCardList: MutableList<SensorCard> = mutableListOf(
        SensorCard(SensorType.TEMPERATURE),
        SensorCard(SensorType.HUMIDITY),
        SensorCard(SensorType.PM10),
        SensorCard(SensorType.PM25)
    )

    init {
        // Gets reference to SensorDao from appRoomDatabase to construct
        // the correct SensorRepository.
        val sensorDao = AppRoomDatabase.getDatabase(application, viewModelScope).sensorDao()
        repository = SensorRepository(sensorDao)
    }

    fun setSensor(sensorID: Int) = viewModelScope.launch {
        sensor.value = repository.loadById(sensorID)

        Log.i(TAG, "Sensor set serial: ${sensor.value?.serialNumber}")
    }

    fun getSensorFirebaseValues(): LiveData<List<SensorFirebase>> {
        if (sensorFirebaseValues.value == null) {
            FirebaseDatabase.getInstance()
                .getReference(sensor.value!!.serialNumber!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(dataSnapshot: DatabaseError) {
                        //
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            sensorFirebaseValues.postValue(toSensorFirebase(dataSnapshot))
                        }
                    }

                })
        }
        return sensorFirebaseValues
    }

    private fun toSensorFirebase(dataSnapshot: DataSnapshot): List<SensorFirebase>? {
        val sensorFirebaseList = mutableListOf<SensorFirebase>()
        for (snapshot in dataSnapshot.children) {
            snapshot.key?.let {
                val timestamp = it.toLong()

                val values = snapshot.value as HashMap<*, *>
                val temp = values[FIREBASE_TEMPERATURE_KEY] as Number
                val humidity = values[FIREBASE_HUMIDITY_KEY] as Number
                val pm10 = values[FIREBASE_PM10_KEY] as Number
                val pm25 = values[FIREBASE_PM25_KEY] as Number

                sensorFirebaseList.add(
                    SensorFirebase(
                        timestamp,
                        temp.toFloat(),
                        humidity.toFloat(),
                        pm10.toFloat(),
                        pm25.toFloat()
                    )
                )
            }

        }
        return sensorFirebaseList
    }

    //todo below
    fun insertLastSensorValues() {
//        val sensorValues = SensorValues(
//            sensor.value!!.sensorId,
//            sensor.value!!.sensorName!!,
//            timestampLiveDataList.value!!.last(),
//            temperatureLiveDataList.value!!.last(),
//            humidityLiveDataList.value!!.last(),
//            pm10LiveDataList.value!!.last(),
//            pm25LiveDataList.value!!.last()
//        )
//        runBlocking {
//            repository.insertSensorValues(sensorValues)
//        }
    }

}
