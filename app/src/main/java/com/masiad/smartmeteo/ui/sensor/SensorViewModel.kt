package com.masiad.smartmeteo.ui.sensor

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
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

/**
 * ViewModel for [SensorFragment]
 */
class SensorViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        val TAG: String = SensorViewModel::class.java.simpleName
    }

    // The ViewModel maintains a reference to the repository to get Room data.
    private val repository: SensorRepository
    // Sensor data saved in room
    private val sensorRoom = MutableLiveData<Sensor>()
    // Sensor values from firebase
    private val sensorFirebaseValues: MutableLiveData<List<SensorFirebase>> = MutableLiveData()
    // Each card view values
    private val sensorCardsList: MutableLiveData<MutableList<SensorCard>> = MutableLiveData(
        mutableListOf(
            SensorCard(SensorType.TEMPERATURE),
            SensorCard(SensorType.HUMIDITY),
            SensorCard(SensorType.PM10),
            SensorCard(SensorType.PM25)
        )
    )

    init {
        // Gets reference to SensorDao from appRoomDatabase to construct the correct SensorRepository.
        val sensorDao = AppRoomDatabase.getDatabase(application, viewModelScope).sensorDao()
        repository = SensorRepository(sensorDao)
    }

    fun setSensorRoom(sensorID: Int) = viewModelScope.launch {
        sensorRoom.value = repository.loadById(sensorID)

        Log.i(TAG, "Sensor set serial: ${sensorRoom.value?.serialNumber}")
    }

    fun getSensorRoom(): LiveData<Sensor> {
        return sensorRoom
    }

    // Get sensor values from firebase
    fun getSensorFirebaseValues(): LiveData<List<SensorFirebase>> {
        if (sensorFirebaseValues.value == null) {
            FirebaseDatabase.getInstance()
                .getReference(sensorRoom.value!!.serialNumber!!)
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

    // Method to insert from firebase dataSnapshot
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

    fun getSensorCardsList(): List<SensorCard> {
        return sensorCardsList.value!!
    }

    fun addSensorCardChartValue(idx: Int, entry: Entry) {
        sensorCardsList.value!![idx].chartValues.add(entry)
    }

    fun setSensorCardCurrentValue(idx: Int, currentValue: Float) {
        sensorCardsList.value!![idx].currentValue = currentValue
    }

    fun setSensorCardAvgSum(idx: Int, avgSum: Float) {
        sensorCardsList.value!![idx].avgSum = avgSum
    }

    fun setSensorCardMax(idx: Int, max: Float) {
        sensorCardsList.value!![idx].max = max
    }

    fun setSensorCardMin(idx: Int, min: Float) {
        sensorCardsList.value!![idx].min = min
    }

    fun setSensorCardValues(idx: Int, currentValue: Float, avgSum: Float, max: Float, min: Float) {
        setSensorCardCurrentValue(idx, currentValue)
        setSensorCardAvgSum(idx, avgSum)
        setSensorCardMax(idx, max)
        setSensorCardMin(idx, min)
    }

}
