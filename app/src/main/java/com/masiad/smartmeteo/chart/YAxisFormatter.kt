package com.masiad.smartmeteo.chart

import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import com.masiad.smartmeteo.utils.format

/**
 * Y Axis Formatter for [Chart]
 */
class YAxisFormatter : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return value.format(1)
    }
}