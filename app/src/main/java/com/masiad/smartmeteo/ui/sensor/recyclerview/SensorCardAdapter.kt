package com.masiad.smartmeteo.ui.sensor.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.masiad.smartmeteo.R
import com.masiad.smartmeteo.chart.XAxisFormatter
import com.masiad.smartmeteo.chart.YAxisFormatter
import com.masiad.smartmeteo.utils.dp
import com.masiad.smartmeteo.utils.slideAnimate

/**
 * Sensor Card [RecyclerView.Adapter]
 */
class SensorCardAdapter(val sensorCardList: List<SensorCard>, val sensorTimestampList: List<Long>) :
    RecyclerView.Adapter<SensorCardAdapter.ViewHolder>() {

    private lateinit var context: Context
    private var isLiveValueObserverPhase = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SensorCardAdapter.ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.card_view, parent, false)

        view.findViewById<LineChart>(R.id.sensorLineChart).apply {
            description = null
            isScaleYEnabled = false
            isHighlightPerTapEnabled = false
            isHighlightPerDragEnabled = false
            val dataSet = LineDataSet(mutableListOf(), null)
            dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            dataSet.setCircleColors(intArrayOf(R.color.colorAccent), context)
            dataSet.setColors(intArrayOf(R.color.colorAccent), context)

            xAxis.valueFormatter =
                XAxisFormatter(sensorTimestampList)
            axisLeft?.valueFormatter =
                YAxisFormatter()
            axisRight?.valueFormatter =
                YAxisFormatter()
            data = LineData(dataSet)
            data.setValueFormatter(YAxisFormatter())
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SensorCardAdapter.ViewHolder, position: Int) {
        val sensorCardItem = sensorCardList[position]
        holder.sensorImageView.setImageResource(sensorCardItem.sensorType.resourceId)
        holder.sensorTitleTextView.text =
            context.resources.getString(sensorCardItem.sensorType.nameId)
        val unit = context.resources.getString(sensorCardItem.sensorType.unitId)
        holder.sensorValueTextView.text = String.format("%.1f%s", sensorCardItem.currentValue, unit)
        holder.chartAvgTextView.text = String.format(
            "%.1f%s",
            sensorCardItem.avgSum / sensorCardItem.chartValues.count(),
            unit
        )
        holder.chartMaxTextView.text = String.format("%.1f%s", sensorCardItem.max, unit)
        holder.chartMinTextView.text = String.format("%.1f%s", sensorCardItem.min, unit)

        holder.chartDropDownImageButton.setOnClickListener {
            val isExpand = holder.chartExpandView.layoutParams.height > 1
            if (isExpand) {
                (it as ImageButton).setImageResource(R.drawable.ic_arrow_drop_down)
                holder.chartExpandView.slideAnimate(300.dp, 0.dp)
            } else {
                (it as ImageButton).setImageResource(R.drawable.ic_arrow_drop_up)
                holder.chartExpandView.slideAnimate(0.dp, 300.dp)
            }
        }

        holder.sensorLineChart.data.dataSets[0].label = unit
        if (isLiveValueObserverPhase) {
            val liveEntry = sensorCardItem.chartValues.last()
            holder.sensorLineChart.data.addEntry(liveEntry, 0)
        } else {
            sensorCardItem.chartValues.forEach {
                holder.sensorLineChart.data.addEntry(it, 0)
            }
        }
        holder.sensorLineChart.notifyDataSetChanged()
        holder.sensorLineChart.invalidate()

    }

    override fun getItemCount(): Int {
        return sensorCardList.count()
    }

    fun setIsLiveValueObserverPhase(isObserverPhase: Boolean) {
        isLiveValueObserverPhase = isObserverPhase
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sensorImageView: ImageView = itemView.findViewById(R.id.sensorImageView)
        val sensorTitleTextView: TextView = itemView.findViewById(R.id.sensorTitleTextView)
        val chartDropDownImageButton: ImageButton =
            itemView.findViewById(R.id.chartDropDownImageButton)
        val sensorValueTextView: TextView = itemView.findViewById(R.id.sensorValueTextView)
        val chartAvgTextView: TextView = itemView.findViewById(R.id.chartAvgTextView)
        val chartMaxTextView: TextView = itemView.findViewById(R.id.chartMaxTextView)
        val chartMinTextView: TextView = itemView.findViewById(R.id.chartMinTextView)
        val sensorLineChart: LineChart = itemView.findViewById(R.id.sensorLineChart)
        val chartExpandView: ConstraintLayout = itemView.findViewById(R.id.chartExpandView)

    }

}