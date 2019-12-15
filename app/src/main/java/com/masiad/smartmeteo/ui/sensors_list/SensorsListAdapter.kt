package com.masiad.smartmeteo.ui.sensors_list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.masiad.smartmeteo.R
import com.masiad.smartmeteo.data.Sensor

class SensorsListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<SensorsListAdapter.SensorsViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var sensors = emptyList<Sensor>() // Cached copy of words

    inner class SensorsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sensorItemView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorsViewHolder {
        val itemView = inflater.inflate(R.layout.sensor_recyclerview_item, parent, false)

        return SensorsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SensorsViewHolder, position: Int) {
        val current = sensors[position]
        holder.sensorItemView.text = current.sensorName

        holder.itemView.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_home)
        }
        //todo on click put sensor serial to next fragment and show temperature
    }

    internal fun setSensors(sensors: List<Sensor>) {
        this.sensors = sensors
        notifyDataSetChanged()
    }

    override fun getItemCount() = sensors.size
}