package com.loro.klinechart.chart

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.loro.klinechart.R

class MyHMarkerView @JvmOverloads constructor(context: Context, layoutResource: Int) :
    MarkerView(context, layoutResource) {

    private val markerTv: ImageView = findViewById(R.id.ivMarker)
    private var num: Float = 0.toFloat()

    init {
    }

    fun setData(num: Float) {
        this.num = num
    }

    fun setIvWidth(width: Int) {
        val params = markerTv.layoutParams as LinearLayout.LayoutParams
        params.width = width
        markerTv.layoutParams = params
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
    }

    override fun getOffset(): MPPointF {
        return MPPointF.getInstance(0f, 0f)
    }
}