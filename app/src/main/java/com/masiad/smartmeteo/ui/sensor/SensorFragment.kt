package com.masiad.smartmeteo.ui.sensor

import android.os.Bundle
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
import com.masiad.smartmeteo.data.AppKotpref
import com.masiad.smartmeteo.ui.sensor.recyclerview.SensorCardAdapter
import com.masiad.smartmeteo.utils.observeOnce

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
        sensorValuesViewAdapter = SensorCardAdapter(sensorViewModel.sensorCardList)
        sensorValuesRecyclerView =
            root.findViewById<RecyclerView>(R.id.sensorValuesRecyclerView).apply {
                adapter = sensorValuesViewAdapter
            }

        // Observes
        sensorViewModel.sensor.observeOnce(viewLifecycleOwner, Observer {
            (requireActivity() as AppCompatActivity).supportActionBar?.title = it.sensorName

            sensorViewModel.getSensorFirebaseValues().observe(viewLifecycleOwner, Observer { list ->
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
                        sensorViewModel.sensorCardList[i].chartValues.add(
                            Entry(
                                list.indexOf(
                                    sensorFirebase
                                ).toFloat(), value
                            )
                        )
                    }
                }

                for (i in 0 until 4) {
                    sensorViewModel.sensorCardList[i].currentValue = currentArray[i]
                    sensorViewModel.sensorCardList[i].avgSum = averageArray[i] / count
                    sensorViewModel.sensorCardList[i].max = maxArray[i]
                    sensorViewModel.sensorCardList[i].min = minArray[i]
                }

                sensorValuesViewAdapter.notifyDataSetChanged()
            })
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Checking is opened favourite sensor
        val favouriteSensorId = AppKotpref.favouriteSensorId
        sensorViewModel.isFavourite = favouriteSensorId == args.sensorId
        sensorViewModel.setSensor(args.sensorId)
    }

    override fun onPause() {
        super.onPause()

        if (sensorViewModel.isFavourite) {
            // Insert last data to model
            sensorViewModel.insertLastSensorValues()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Show floating action button
        (activity as MainActivity).showFloatingActionButton()
    }

}
