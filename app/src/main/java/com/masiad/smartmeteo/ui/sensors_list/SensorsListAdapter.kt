package com.masiad.smartmeteo.ui.sensors_list

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.masiad.smartmeteo.R
import com.masiad.smartmeteo.data.Sensor
import com.masiad.smartmeteo.utils.AppPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Sensor List [RecyclerView.Adapter]
 */
abstract class SensorsListAdapter(val sensorsList: MutableList<Sensor>) :
    RecyclerView.Adapter<SensorsListAdapter.SensorsViewHolder>() {
    companion object {
        val TAG: String = SensorsListFragment::class.java.simpleName
    }

    abstract fun onItemClick(sensorId: Int)
    abstract fun onLongItemClick(sensorId: Int)

    private lateinit var context: Context

    class SensorsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sensorItemTextView: TextView = itemView.findViewById(R.id.textView)
        val sensorItemImageButton: ImageButton = itemView.findViewById(R.id.favouriteImageButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorsViewHolder {
        context = parent.context
        val view =
            LayoutInflater.from(context).inflate(R.layout.sensor_recyclerview_item, parent, false)

        return SensorsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SensorsViewHolder, position: Int) {
        val currentItem = sensorsList[position]
        holder.sensorItemTextView.text = currentItem.sensorName
        // Set favourite sensor image
        val isFavourite = currentItem.sensorId == AppPreferences.favouriteSensorId
        if (isFavourite) {
            holder.sensorItemImageButton.setImageResource(R.drawable.ic_favorite_black)
        } else {
            holder.sensorItemImageButton.setImageResource(R.drawable.ic_favorite_border_black)
        }

        holder.itemView.setOnClickListener {
            onItemClick(currentItem.sensorId)
        }

        holder.sensorItemImageButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val favouriteDrawable = if (isFavourite) {
                    AppPreferences.favouriteSensorId = -1
                    R.drawable.ic_favorite_border_black
                } else {
                    Log.i(TAG, "Favourite sensor set: ${currentItem.sensorName}")
                    AppPreferences.favouriteSensorId = currentItem.sensorId
                    R.drawable.ic_favorite_black
                }
                (it as ImageButton).setImageResource(favouriteDrawable)
                notifyDataSetChanged()
            }
        }

        holder.itemView.setOnLongClickListener {
            onLongItemClick(currentItem.sensorId)
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount() = sensorsList.size

    fun updateSensorsList(list: List<Sensor>) {
        sensorsList.clear()
        sensorsList.addAll(list)
        notifyDataSetChanged()
    }
}