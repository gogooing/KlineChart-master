package com.loro.klinechart.chart

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.loro.klinechart.R

class MyBottomMarkerView @JvmOverloads constructor(context: Context, layoutResource: Int) :
    MarkerView(context, layoutResource) {

    private val markerTv: TextView = findViewById(R.id.tvMarker)
    private var time: String? = null

    init {
        markerTv.textSize = 10f
    }

    fun setData(time: String) {
        this.time = time
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        markerTv.text = time
    }

    override fun getOffset(): MPPointF {
        return MPPointF.getInstance(0f, 0f)
    }
}