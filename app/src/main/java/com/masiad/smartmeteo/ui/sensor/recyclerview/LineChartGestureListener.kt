package com.masiad.smartmeteo.ui.sensor.recyclerview

import android.view.MotionEvent
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener

class LineChartGestureListener(val chart: LineChart) : OnChartGestureListener {
    companion object {
        val TAG: String = SensorCardAdapter::class.java.simpleName
    }

    init {
        chart.onChartGestureListener = this
    }

    override fun onChartGestureEnd(
        me: MotionEvent?,
        lastPerformedGesture: ChartTouchListener.ChartGesture?
    ) {
        // onChartGestureEnd
    }

    override fun onChartFling(
        me1: MotionEvent?,
        me2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ) {
        // onChartFling
    }

    override fun onChartSingleTapped(me: MotionEvent?) {
        // onChartSingleTapped
    }

    override fun onChartGestureStart(
        me: MotionEvent?,
        lastPerformedGesture: ChartTouchListener.ChartGesture?
    ) {
        // onChartGestureStart
    }

    override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
        // onChartScale
    }

    override fun onChartLongPressed(me: MotionEvent?) {
        // onChartLongPressed
    }

    override fun onChartDoubleTapped(me: MotionEvent?) {
        // onChartDoubleTapped
    }

    override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
        // onChartTranslate
    }
}