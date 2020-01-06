package com.masiad.smartmeteo.ui.sensor

import android.content.res.Configuration
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
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.masiad.smartmeteo.MainActivity
import com.masiad.smartmeteo.R
import com.masiad.smartmeteo.chart.XAxisFormatter
import com.masiad.smartmeteo.chart.YAxisFormatter
import com.masiad.smartmeteo.utils.*

class SensorFragment : Fragment() {
    companion object {
        val TAG: String = SensorFragment::class.java.simpleName
    }

    private lateinit var sensorViewModel: SensorViewModel

    private val args: SensorFragmentArgs by navArgs()

    private var temperatureChartItem: View? = null
    private var humidityChartItem: View? = null
    private var pm10ChartItem: View? = null
    private var pm25ChartItem: View? = null

    private var temperatureLineChart: LineChart? = null
    private var humidityLineChart: LineChart? = null
    private var pm10LineChart: LineChart? = null
    private var pm25LineChart: LineChart? = null


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
            if (it.count() == 0) return@Observer

            // Set current
            val temperature = it.last()
            val tempString = "Temperature: ${it.last().format(2)}°"
            temperatureChartItem?.findViewById<TextView>(R.id.sensorTitleTextView)?.text =
                tempString

            // Refresh chart
            val entry = Entry((it.count() - 1).toFloat(), temperature)
            temperatureLineChart?.data?.addEntry(entry, 0)

            temperatureLineChart?.notifyDataSetChanged()
            temperatureLineChart?.invalidate()
//            humidityLineChart?.setVisibleXRangeMaximum(20f)

        })

        // Set humidity live data list observer
        sensorViewModel.getHumidityLiveData().observe(viewLifecycleOwner, Observer {
            if (it.count() == 0) return@Observer

            // Set current
            val humidity = it.last()
            val humidityString = "Humidity: ${humidity.format(2)}%"
            humidityChartItem?.findViewById<TextView>(R.id.sensorTitleTextView)?.text =
                humidityString

            // Refresh chart
            val entry = Entry((it.count() - 1).toFloat(), humidity)
            humidityLineChart?.data?.addEntry(entry, 0)

            humidityLineChart?.notifyDataSetChanged()
            humidityLineChart?.invalidate()
        })

        // Set pm10 live data list observer
        sensorViewModel.getPM10LiveData().observe(viewLifecycleOwner, Observer {
            if (it.count() == 0) return@Observer

            // Set current
            val pm10 = it.last()
            val pm10String =
                "PM 10: ${pm10.format(2)}${resources.getString(R.string.pm_unit)}"
            pm10ChartItem?.findViewById<TextView>(R.id.sensorTitleTextView)?.text =
                pm10String

            // Refresh chart
            val entry = Entry((it.count() - 1).toFloat(), pm10)
            pm10LineChart?.data?.addEntry(entry, 0)

            pm10LineChart?.notifyDataSetChanged()
            pm10LineChart?.invalidate()
        })

        // Set pm25 live data list observer
        sensorViewModel.getPM25LiveData().observe(viewLifecycleOwner, Observer {
            if (it.count() == 0) return@Observer

            // Set current
            val pm25 = it.last()
            val pm25String =
                "PM 2,5: ${pm25.format(2)}${resources.getString(R.string.pm_unit)}"
            pm25ChartItem?.findViewById<TextView>(R.id.sensorTitleTextView)?.text =
                pm25String

            // Refresh chart
            val entry = Entry((it.count() - 1).toFloat(), pm25)
            pm25LineChart?.data?.addEntry(entry, 0)

            pm25LineChart?.notifyDataSetChanged()
            pm25LineChart?.invalidate()
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
        pm25ChartItem = layoutInflater.inflate(R.layout.chart_item, null)
        pm10ChartItem = layoutInflater.inflate(R.layout.chart_item, null)

        // Add chart items to layout
        contentLinearLayout.addView(temperatureChartItem)
        contentLinearLayout.addView(humidityChartItem)
        contentLinearLayout.addView(pm10ChartItem)
        contentLinearLayout.addView(pm25ChartItem)

        // Set chart items icon
        temperatureChartItem?.findViewById<ImageView>(R.id.sensorImageView)
            ?.setImageDrawable(resources.getDrawable(R.drawable.thermometer_icon, null))

        humidityChartItem?.findViewById<ImageView>(R.id.sensorImageView)
            ?.setImageDrawable(resources.getDrawable(R.drawable.humidity_icon, null))

        pm10ChartItem?.findViewById<ImageView>(R.id.sensorImageView)
            ?.setImageDrawable(resources.getDrawable(R.drawable.pm10_icon, null))

        pm25ChartItem?.findViewById<ImageView>(R.id.sensorImageView)
            ?.setImageDrawable(resources.getDrawable(R.drawable.pm25_icon, null))

        // Get chart items chart
        temperatureLineChart = temperatureChartItem?.findViewById(R.id.sensorLineChart)
        humidityLineChart = humidityChartItem?.findViewById(R.id.sensorLineChart)
        pm10LineChart = pm10ChartItem?.findViewById(R.id.sensorLineChart)
        pm25LineChart = pm25ChartItem?.findViewById(R.id.sensorLineChart)

        // All chart settings
        allChartsSettings(temperatureLineChart, humidityLineChart, pm10LineChart, pm25LineChart)
    }

    private fun allChartsSettings(vararg charts: LineChart?) {
        val labels = arrayOf(
            "°C",
            "%",
            resources.getString(R.string.pm_unit),
            resources.getString(R.string.pm_unit)
        )
        for ((i, chart) in charts.withIndex()) {
            chart?.description = null
            chart?.isScaleYEnabled = false
//            chart?.isAutoScaleMinMaxEnabled = true
            chart?.isHighlightPerTapEnabled = false
            chart?.isHighlightPerDragEnabled = false

            val dataSet = LineDataSet(mutableListOf<Entry>(), labels[i])
            dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            dataSet.setCircleColors(intArrayOf(R.color.colorAccent), requireContext())
            dataSet.setColors(intArrayOf(R.color.colorAccent), requireContext())

            chart?.xAxis?.valueFormatter =
                XAxisFormatter(sensorViewModel.getTimestampLiveData().value!!)
            chart?.axisLeft?.valueFormatter =
                YAxisFormatter()
            chart?.axisRight?.valueFormatter =
                YAxisFormatter()
            chart?.data = LineData(dataSet)
            chart?.data?.setValueFormatter(YAxisFormatter())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Show floating action button
        (activity as MainActivity).showFloatingActionButton()
    }

}
