package com.masiad.smartmeteo.ui.home

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.masiad.smartmeteo.R
import com.masiad.smartmeteo.data.AppKotpref
import com.masiad.smartmeteo.utils.FAVOURITE_SENSOR_ID_KEY
import com.masiad.smartmeteo.utils.format
import java.util.*

class HomeFragment : Fragment() {
    companion object {
        val TAG: String = HomeFragment::class.java.simpleName
    }

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val favouriteSensorNameTextView: TextView = root.findViewById(R.id.favouriteSensorNameTextView)
        val temperatureItem = root.findViewById<View>(R.id.temperatureItem)
        val humidityItem = root.findViewById<View>(R.id.humidityItem)
        val pm10Item = root.findViewById<View>(R.id.pm10Item)
        val pm25Item = root.findViewById<View>(R.id.pm25Item)
        val updateTimeTextView: TextView = root.findViewById(R.id.updateTimeTextView)
        val showLiveFavouriteSensor: Button = root.findViewById(R.id.showLiveFavouriteSensor)

        homeViewModel.getSensorValuesLiveData().observe(viewLifecycleOwner, Observer {
            favouriteSensorNameTextView.text = it.name

            temperatureItem.findViewById<TextView>(R.id.sensorValueTextView).text = String.format("%.2f%s", it.temperature, "°")
            temperatureItem.findViewById<ImageView>(R.id.sensorImageView).setImageDrawable(resources.getDrawable(R.drawable.thermometer_icon, null))

            humidityItem.findViewById<TextView>(R.id.sensorValueTextView).text = String.format("%.2f%s", it.humidity, "%")
            humidityItem.findViewById<ImageView>(R.id.sensorImageView).setImageDrawable(resources.getDrawable(R.drawable.humidity_icon, null))

            pm10Item.findViewById<TextView>(R.id.sensorValueTextView).text = String.format("%.2f%s", it.pm10, resources.getString(R.string.pm_unit))
            pm10Item.findViewById<ImageView>(R.id.sensorImageView).setImageDrawable(resources.getDrawable(R.drawable.pm10_icon, null))

            pm25Item.findViewById<TextView>(R.id.sensorValueTextView).text = String.format("%.2f%s", it.pm25, resources.getString(R.string.pm_unit))
            pm25Item.findViewById<ImageView>(R.id.sensorImageView).setImageDrawable(resources.getDrawable(R.drawable.pm25_icon, null))

            val calendar = Calendar.getInstance(Locale.getDefault())
            calendar.timeInMillis = it.timestamp * 1000L
            val updateTime = android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", calendar).toString()
//            updateTimeTextView.text = "${resources.getString(R.string.last_update)} $updateTime"
            updateTimeTextView.text = String.format("%s %s", resources.getString(R.string.last_update), updateTime)

            showLiveFavouriteSensor.setOnClickListener { _ ->
                Navigation.findNavController(root).navigate(HomeFragmentDirections.actionNavHomeToSensorFragment(it.favouriteId))
            }
        })

        val favouriteSensorId = AppKotpref.favouriteSensorId

        homeViewModel.getEmptyListLiveData().observe(viewLifecycleOwner, Observer {
            if (it == true) {
                updateTimeTextView.text = "No data. Open live measurement"
                showLiveFavouriteSensor.setOnClickListener { _ ->
                    Navigation.findNavController(root).navigate(HomeFragmentDirections.actionNavHomeToSensorFragment(favouriteSensorId))
                }
            }
        })

        if (favouriteSensorId != -1) {
            homeViewModel.setFavouriteSensorId(favouriteSensorId)
        } else {
            temperatureItem.visibility = View.GONE
            humidityItem.visibility = View.GONE
            pm10Item.visibility = View.GONE
            pm25Item.visibility = View.GONE
            showLiveFavouriteSensor.visibility = View.GONE
        }

        return root
    }
}
