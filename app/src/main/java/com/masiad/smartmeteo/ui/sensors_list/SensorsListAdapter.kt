package com.masiad.smartmeteo.ui.sensors_list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.masiad.smartmeteo.R
import com.masiad.smartmeteo.data.Sensor

/**
 * Sensor List [RecyclerView.Adapter]
 */
abstract class SensorsListAdapter internal constructor(
    val context: Context, val favouriteSensorId: Int
) : RecyclerView.Adapter<SensorsListAdapter.SensorsViewHolder>() {
    abstract fun onItemClick(sensorId: Int)
    abstract fun onFavouriteItemClick(sensorId: Int)
    abstract fun onLongItemClick(sensorId: Int): Boolean

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var sensors = emptyList<Sensor>() // Cached copy of words
    private val viewHoldersList = mutableListOf<SensorsViewHolder>()

    inner class SensorsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sensorItemTextView: TextView = itemView.findViewById(R.id.textView)
        val sensorItemImageButton: ImageButton = itemView.findViewById(R.id.favouriteImageButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorsViewHolder {
        val itemView = inflater.inflate(R.layout.sensor_recyclerview_item, parent, false)

        return SensorsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SensorsViewHolder, position: Int) {
        viewHoldersList.add(holder)
        val current = sensors[position]
        holder.sensorItemTextView.text = current.sensorName
        // Set favourite sensor image
        if (current.sensorId == favouriteSensorId) {
            holder.sensorItemImageButton.setImageDrawable(
                context.resources.getDrawable(
                    R.drawable.ic_favorite_black,
                    null
                )
            )
            onFavouriteItemClick(current.sensorId)
        }

        holder.itemView.setOnClickListener {
            onItemClick(current.sensorId)
        }

        holder.sensorItemImageButton.setOnClickListener {
            viewHoldersList.forEach { h ->
                h.sensorItemImageButton.setImageDrawable(
                    context.resources.getDrawable(
                        R.drawable.ic_favorite_border_black,
                        null
                    )
                )
            }
            (it as ImageButton).setImageDrawable(
                context.resources.getDrawable(
                    R.drawable.ic_favorite_black,
                    null
                )
            )
            onFavouriteItemClick(current.sensorId)
        }

        holder.itemView.setOnLongClickListener {
            return@setOnLongClickListener onLongItemClick(current.sensorId)
        }
    }

    internal fun setSensors(sensors: List<Sensor>) {
        this.sensors = sensors
        notifyDataSetChanged()
    }

    override fun getItemCount() = sensors.size
}