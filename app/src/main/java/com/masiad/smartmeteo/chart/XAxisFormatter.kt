package com.masiad.smartmeteo.chart

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import com.masiad.smartmeteo.ui.sensor.SensorViewModel
import java.util.*


class XAxisFormatter(private val timestampList: MutableList<Long>): ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val calendar = Calendar.getInstance(Locale.getDefault())
        val timestamp = timestampList[value.toInt()]
        calendar.timeInMillis = timestamp * 1000L
        return android.text.format.DateFormat.format("HH:mm:ss", calendar).toString()
    }
}