package com.masiad.smartmeteo.ui.sensor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
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
import com.masiad.smartmeteo.MainActivity
import com.masiad.smartmeteo.R
import com.masiad.smartmeteo.utils.*

class SensorFragment : Fragment() {
    companion object {
        val TAG: String = SensorFragment::class.java.simpleName
    }

    private lateinit var sensorViewModel: SensorViewModel

    private val args: SensorFragmentArgs by navArgs()

    private var temperatureChartItem: View? = null
    private var humidityChartItem: View? = null
    private var particulateMatter25ChartItem: View? = null
    private var particulateMatter10ChartItem: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sensorViewModel = ViewModelProviders.of(this).get(SensorViewModel::class.java)

        val root = inflater.inflate(R.layout.sensor_fragment, container, false)

        // Hide floating action button
        (activity as MainActivity).hideFloatingActionButton()

        // Set sensor charts
        setSensorCharts(root)

        // Set timestamp live data list observer
        sensorViewModel.getTimestampLiveData().observe(viewLifecycleOwner, Observer {
            Log.i(TAG, "model list time ${it.toString()}")
        })

        // Set temperature live data list observer
        sensorViewModel.getTemperatureLiveData().observe(viewLifecycleOwner, Observer {
            val tempString = "Temperature: ${(it.lastOrNull() ?: 0f).format(2)}°"
            temperatureChartItem?.findViewById<TextView>(R.id.sensorTitleTextView)?.text =
                tempString
        })

        // Set humidity live data list observer
        sensorViewModel.getHumidityLiveData().observe(viewLifecycleOwner, Observer {
            val humidityString = "Humidity: ${(it.lastOrNull() ?: 0f).format(2)}%"
            humidityChartItem?.findViewById<TextView>(R.id.sensorTitleTextView)?.text =
                humidityString
        })

        // Set pm10 live data list observer
        sensorViewModel.getPM10LiveData().observe(viewLifecycleOwner, Observer {
            val pm10String =
                "PM 10: ${(it.lastOrNull()
                    ?: 0f).format(2)}${resources.getString(R.string.pm_unit)}"
            particulateMatter10ChartItem?.findViewById<TextView>(R.id.sensorTitleTextView)?.text =
                pm10String
        })

        // Set pm25 live data list observer
        sensorViewModel.getPM25LiveData().observe(viewLifecycleOwner, Observer {
            val pm25String =
                "PM 2,5: ${(it.lastOrNull()
                    ?: 0f).format(2)}${resources.getString(R.string.pm_unit)}"
            particulateMatter25ChartItem?.findViewById<TextView>(R.id.sensorTitleTextView)?.text =
                pm25String
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
                sensorViewModel.addCurrentTimestamp(timestamp)

                val values = dataSnapshot.value as HashMap<*, *>

                val temp = values[FIREBASE_TEMPERATURE_KEY] as Number
                Log.i(TAG, "Temp: $temp")
                sensorViewModel.addCurrentTemperature(temp.toFloat())

                val humidity = values[FIREBASE_HUMIDITY_KEY] as Number
                Log.i(TAG, "Humidity: $humidity")
                sensorViewModel.addCurrentHumidity(humidity.toFloat())

                val pm10 = values[FIREBASE_PM10_KEY] as Number
                Log.i(TAG, "PM10: $pm10")
                sensorViewModel.addCurrentPM10(pm10.toFloat())

                val pm25 = values[FIREBASE_PM25_KEY] as Number
                Log.i(TAG, "PM25: $pm25")
                sensorViewModel.addCurrentPM25(pm25.toFloat())

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.i(TAG, "sensor child onRemoved")

            }

        })
    }

    private fun setSensorCharts(root: View) {
        val contentLinearLayout: LinearLayout = root.findViewById(R.id.contentLinearLayout)

        temperatureChartItem = layoutInflater.inflate(R.layout.chart_item, null)
        humidityChartItem = layoutInflater.inflate(R.layout.chart_item, null)
        particulateMatter25ChartItem = layoutInflater.inflate(R.layout.chart_item, null)
        particulateMatter10ChartItem = layoutInflater.inflate(R.layout.chart_item, null)

        contentLinearLayout.addView(temperatureChartItem)
        contentLinearLayout.addView(humidityChartItem)
        contentLinearLayout.addView(particulateMatter10ChartItem)
        contentLinearLayout.addView(particulateMatter25ChartItem)

        temperatureChartItem?.findViewById<ImageView>(R.id.sensorImageView)
            ?.setImageDrawable(resources.getDrawable(R.drawable.thermometer_icon, null))

        humidityChartItem?.findViewById<ImageView>(R.id.sensorImageView)
            ?.setImageDrawable(resources.getDrawable(R.drawable.humidity_icon, null))

        particulateMatter10ChartItem?.findViewById<ImageView>(R.id.sensorImageView)
            ?.setImageDrawable(resources.getDrawable(R.drawable.pm10_icon, null))

        particulateMatter25ChartItem?.findViewById<ImageView>(R.id.sensorImageView)
            ?.setImageDrawable(resources.getDrawable(R.drawable.pm25_icon, null))

//        val temperatureLineChart: LineChart = root.findViewById(R.id.temperatureLineChart)
//        val entryList = arrayListOf<Entry>()
//        entryList.add(Entry(12f,5f))
//        entryList.add(Entry(13f,7f))
//        entryList.add(Entry(14f,3f))
//        val dataSet = LineDataSet(entryList, "Label")
//        //todo custom data set
//        val lineData = LineData(dataSet)
//        temperatureLineChart.data = lineData
//        temperatureLineChart.invalidate()
        //todo chart

    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Show floating action button
        (activity as MainActivity).showFloatingActionButton()
    }

}
