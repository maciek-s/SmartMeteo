package com.masiad.smartmeteo.ui.sensor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.Entry
import com.masiad.smartmeteo.MainActivity
import com.masiad.smartmeteo.R
import com.masiad.smartmeteo.ui.sensor.recyclerview.SensorCardAdapter
import com.masiad.smartmeteo.utils.AppPreferences
import com.masiad.smartmeteo.utils.observeOnce

/**
 * Sensor [Fragment]
 */
class SensorFragment : Fragment() {
    companion object {
        val TAG: String = SensorFragment::class.java.simpleName
    }

    private lateinit var sensorViewModel: SensorViewModel

    private val args: SensorFragmentArgs by navArgs()


    private lateinit var sensorValuesRecyclerView: RecyclerView
    private lateinit var sensorValuesViewAdapter: RecyclerView.Adapter<*>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sensorViewModel = ViewModelProviders.of(this).get(SensorViewModel::class.java)

        val root = inflater.inflate(R.layout.sensor_fragment, container, false)

        // Hide floating action button
        (activity as MainActivity).hideFloatingActionButton()

        // Bind recycler view
        sensorValuesViewAdapter = SensorCardAdapter(sensorViewModel.getSensorCardsList())
        sensorValuesRecyclerView =
            root.findViewById<RecyclerView>(R.id.sensorValuesRecyclerView).apply {
                adapter = sensorValuesViewAdapter
            }

        setUpObservers()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set selected or favourite sensorId
        val sensorId = if (args.sensorId != -1) {
            args.sensorId
        } else {
            AppPreferences.favouriteSensorId
        }
        sensorViewModel.setSensorRoom(sensorId)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Show floating action button
        (activity as MainActivity).showFloatingActionButton()
    }

    private fun setUpObservers() {
        // Set sensor room observer
        sensorViewModel.getSensorRoom().observeOnce(viewLifecycleOwner, Observer {
            Log.i(TAG, "Load sensor data from Room: ${it.sensorName}")
            // After sensor loaded from room set up view
            (requireActivity() as AppCompatActivity).supportActionBar?.title = it.sensorName

            sensorViewModel.getSensorFirebaseValues()
                .observeOnce(viewLifecycleOwner, Observer { list ->
                    if (sensorViewModel.getSensorCardsList()[0].chartValues.count() > 0) {
                        return@Observer
                    }
                    Log.i(TAG, "Load sensor data from Firebase")
                    val count = list.count()
                    val averageArray = FloatArray(4) { 0f }
                    val maxArray = FloatArray(4) { 0f }
                    val minArray = FloatArray(4) { 0f }
                    val currentArray = floatArrayOf(
                        list[count - 1].temperature,
                        list[count - 1].humidity,
                        list[count - 1].pm10,
                        list[count - 1].pm25
                    )

                    list.forEach { sensorFirebase ->
                        val timestamp = sensorFirebase.timestamp
                        val values = floatArrayOf(
                            sensorFirebase.temperature,
                            sensorFirebase.humidity,
                            sensorFirebase.pm10,
                            sensorFirebase.pm25
                        )
                        for ((i, value) in values.withIndex()) {
                            averageArray[i] += value
                            if (value > maxArray[i]) {
                                maxArray[i] = value
                            } else if (value < minArray[i]) {
                                minArray[i] = value
                            }
                            //todo use timestamp
                            sensorViewModel.addSensorCardChartValue(
                                i,
                                Entry(list.indexOf(sensorFirebase).toFloat(), value)
                            )
                        }
                    }

                    for (i in 0 until 4) {
                        sensorViewModel.setSensorCardValues(
                            i,
                            currentArray[i],
                            averageArray[i],
                            maxArray[i],
                            minArray[i]
                        )
                    }

                    sensorValuesViewAdapter.notifyDataSetChanged()
                })
        })
    }

}
