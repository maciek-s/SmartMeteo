package com.masiad.smartmeteo.ui.sensor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.masiad.smartmeteo.R
import com.masiad.smartmeteo.utils.FIREBASE_HUMIDITY_KEY
import com.masiad.smartmeteo.utils.FIREBASE_TEMPERATURE_KEY
import com.masiad.smartmeteo.utils.format

class SensorFragment : Fragment() {
    companion object {
        val TAG: String = SensorFragment::class.java.simpleName
    }

    private lateinit var sensorViewModel: SensorViewModel

    private val args: SensorFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sensorViewModel = ViewModelProviders.of(this).get(SensorViewModel::class.java)

        val root = inflater.inflate(R.layout.sensor_fragment, container, false)
        // Set temperature observer
        val textCurrentTemperature: TextView = root.findViewById(R.id.textCurrentTemperature)
        sensorViewModel.currentTemperature.observe(viewLifecycleOwner, Observer {
            val tempString = "${it.format(2)}°"
            textCurrentTemperature.text = tempString
        })
        // Set humidity observer
        val textCurrentHumidity: TextView = root.findViewById(R.id.textCurrentHumidity)
        sensorViewModel.currentHumidity.observe(viewLifecycleOwner, Observer {
            val humidityString = "${it.format(2)}%"
            textCurrentHumidity.text = humidityString
        })
        // Set sensor observer
        sensorViewModel.sensor.observe(viewLifecycleOwner, Observer {
            (requireActivity() as AppCompatActivity).supportActionBar?.title = it.sensorName
            setSensorDataListener(it.serialNumber ?: "Empty")
            //todo handle empty serial
        })
        // Set selected sensor
        sensorViewModel.setSensor(args.sensorId)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun setSensorDataListener(serialNumber: String) {
        Log.i(TAG, "Serial number: $serialNumber")
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference(serialNumber)
//        val query = reference.limitToLast(1)
//        val query = reference.orderByKey().startAt( "1578240150").limitToFirst(1)
        val query = reference

        query.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.i(TAG, "sensor child onCanceled")
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {
                Log.i(TAG, "sensor child onMoved: $p1")

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                Log.i(TAG, "sensor child onChanged: $p1")

            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                Log.i(TAG, "sensor child onAdded: $p1")

                val timestamp = (dataSnapshot.key as String).toLong()
                Log.i(TAG, "Timestamp: $timestamp")
                Log.i(TAG, "TimestamC: ${System.currentTimeMillis() / 1000}")

                val values = dataSnapshot.value as HashMap<*, *>

                val temp = values[FIREBASE_TEMPERATURE_KEY] as Number
                Log.i(TAG, "Temp: $temp")
                sensorViewModel.setCurrentTemperature(temp.toFloat())

                val humidity = values[FIREBASE_HUMIDITY_KEY] as Number
                Log.i(TAG, "Humidity: $humidity")
                sensorViewModel.setCurrentHumidity(humidity.toFloat())
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.i(TAG, "sensor child onRemoved")

            }

        })
    }

}
