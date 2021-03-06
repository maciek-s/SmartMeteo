package com.masiad.smartmeteo.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SensorDao {
    @Query("SELECT * FROM sensors_table")
    fun getAll(): LiveData<List<Sensor>>

    @Query("SELECT * FROM sensors_table WHERE sensorId = (:sensorID)")
    suspend fun loadById(sensorID: Int): Sensor

    @Query("DELETE FROM sensors_table WHERE sensorId = (:sensorID)")
    suspend fun deleteById(sensorID: Int)

    @Query("DELETE FROM sensors_table")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sensor: Sensor): Long

    @Delete
    suspend fun delete(sensor: Sensor)

    @Query("SELECT * FROM senor_values_table WHERE favouriteId =(:favouriteId)")
    suspend fun getSensorValues(favouriteId: Int): SensorValues

    @Insert(entity = SensorValues::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSensorValues(sensorValues: SensorValues): Long
}