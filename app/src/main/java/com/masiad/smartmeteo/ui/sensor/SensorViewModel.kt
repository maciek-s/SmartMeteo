package com.masiad.smartmeteo.ui.sensor

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.google.firebase.database.*
import com.masiad.smartmeteo.data.AppRoomDatabase
import com.masiad.smartmeteo.data.Sensor
import com.masiad.smartmeteo.data.SensorRepository
import com.masiad.smartmeteo.ui.sensor.firebase.SensorFirebase
import com.masiad.smartmeteo.ui.sensor.recyclerview.SensorCard
import com.masiad.smartmeteo.ui.sensor.recyclerview.SensorType
import com.masiad.smartmeteo.utils.*
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
    // Sensor history values from firebase
    private val sensorFirebaseValues: MutableLiveData<List<SensorFirebase>> = MutableLiveData()
    // Sensor current value from firebase
    private val sensorLiveFirebaseValue: MutableLiveData<SensorFirebase> = MutableLiveData()
    private lateinit var sensorLiveFirebaseValueChildEventListener: ChildEventListener

    // Each card view values
    private val sensorCardsList: List<SensorCard> = listOf(
        SensorCard(SensorType.TEMPERATURE),
        SensorCard(SensorType.HUMIDITY),
        SensorCard(SensorType.PM10),
        SensorCard(SensorType.PM25)
    )
    // Each value timestamp
    private val sensorFirebaseTimestamp = mutableListOf<Long>()

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
                .orderByChild(FIREBASE_TIMESTAMP_KEY)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(dataSnapshot: DatabaseError) {
                        // onCanceled
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

    fun getSensorLiveFirebaseValue(): LiveData<SensorFirebase> {
        return sensorLiveFirebaseValue
    }

    fun removeSensorLiveFirebaseValueChildEventListener() {
        FirebaseDatabase.getInstance()
            .getReference(sensorRoom.value!!.serialNumber!!)
            .removeEventListener(sensorLiveFirebaseValueChildEventListener)
    }

    fun setUpListenerForLiveFirebaseValues() {
        sensorLiveFirebaseValueChildEventListener = FirebaseDatabase.getInstance()
            .getReference(sensorRoom.value!!.serialNumber!!)
            .orderByChild(FIREBASE_TIMESTAMP_KEY)
            .startAt(sensorFirebaseTimestamp.last().toDouble() + 1.0)
            .addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    // onChildCanceled
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                    // onChildMoved
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    // onChildChanged
                }

                override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                    Log.i(
                        TAG,
                        "Live child added ${dataSnapshot.value} lastTimestamp: ${sensorFirebaseTimestamp.last()}"
                    )

                    val values = dataSnapshot.value as HashMap<*, *>

                    val timestamp = values[FIREBASE_TIMESTAMP_KEY] as Long
                    val temp = values[FIREBASE_TEMPERATURE_KEY] as Double
                    val humidity = values[FIREBASE_HUMIDITY_KEY] as Double
                    val pm10 = values[FIREBASE_PM10_KEY] as Double
                    val pm25 = values[FIREBASE_PM25_KEY] as Double

                    sensorLiveFirebaseValue.postValue(
                        SensorFirebase(
                            timestamp,
                            temp.toFloat(),
                            humidity.toFloat(),
                            pm10.toFloat(),
                            pm25.toFloat()
                        )
                    )
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                    // onChildRemoved
                }

            })
    }

    // Method to insert from firebase dataSnapshot
    private fun toSensorFirebase(dataSnapshot: DataSnapshot): List<SensorFirebase>? {
        val sensorFirebaseList = mutableListOf<SensorFirebase>()
        for (snapshot in dataSnapshot.children) {

            val values = snapshot.value as HashMap<*, *>

            val timestamp = values[FIREBASE_TIMESTAMP_KEY] as Long
            val temp = values[FIREBASE_TEMPERATURE_KEY] as Double
            val humidity = values[FIREBASE_HUMIDITY_KEY] as Double
            val pm10 = values[FIREBASE_PM10_KEY] as Double
            val pm25 = values[FIREBASE_PM25_KEY] as Double

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

        return sensorFirebaseList
    }

    fun getSensorCardsList(): List<SensorCard> {
        return sensorCardsList
    }

    fun addSensorCardChartValue(idx: Int, entry: Entry) {
        sensorCardsList[idx].chartValues.add(entry)
    }

    fun setSensorCardCurrentValue(idx: Int, currentValue: Float) {
        sensorCardsList[idx].currentValue = currentValue
    }

    fun setSensorCardAvgSum(idx: Int, avgSum: Float) {
        sensorCardsList[idx].avgSum = avgSum
    }

    fun increaseSensorCardAvgSum(idx: Int, value: Float) {
        sensorCardsList[idx].avgSum += value
    }

    fun setSensorCardMax(idx: Int, max: Float) {
        sensorCardsList[idx].max = max
    }

    fun updateSensorCardMaxIfNeeded(idx: Int, max: Float) {
        if (max > sensorCardsList[idx].max) {
            setSensorCardMax(idx, max)
        }
    }

    fun setSensorCardMin(idx: Int, min: Float) {
        sensorCardsList[idx].min = min
    }

    fun updateSensorCardMinIfNeeded(idx: Int, min: Float) {
        if (min < sensorCardsList[idx].min) {
            setSensorCardMin(idx, min)
        }
    }

    fun setSensorCardValues(idx: Int, currentValue: Float, avgSum: Float, max: Float, min: Float) {
        setSensorCardCurrentValue(idx, currentValue)
        setSensorCardAvgSum(idx, avgSum)
        setSensorCardMax(idx, max)
        setSensorCardMin(idx, min)
    }

    fun updateSensorCardValues(idx: Int, liveValue: Float) {
        setSensorCardCurrentValue(idx, liveValue)
        increaseSensorCardAvgSum(idx, liveValue)
        updateSensorCardMaxIfNeeded(idx, liveValue)
        updateSensorCardMinIfNeeded(idx, liveValue)
        val cardIdx = sensorFirebaseTimestamp.count() - 1
        addSensorCardChartValue(idx, Entry(cardIdx.toFloat(), liveValue))
    }

    fun getSensorCardValues(idx: Int): SensorCard {
        return sensorCardsList[idx]
    }

    fun getSensorFirebaseTimestampList(): List<Long> {
        return sensorFirebaseTimestamp
    }

    fun addToSensorFirebaseList(timestamp: Long) {
        sensorFirebaseTimestamp.add(timestamp)
    }

}
