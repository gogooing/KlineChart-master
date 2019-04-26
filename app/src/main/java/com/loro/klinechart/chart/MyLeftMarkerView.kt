package com.loro.klinechart.chart

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.loro.klinechart.R
import java.text.DecimalFormat

class MyLeftMarkerView @JvmOverloads constructor(context: Context, layoutResource: Int) :
    MarkerView(context, layoutResource) {

    private val markerTv: TextView = findViewById(R.id.tvMarker)
    private var num: Float = 0.toFloat()
    private val mFormat: DecimalFormat = DecimalFormat("#0.00")

    init {
        markerTv.textSize = 10f
    }

    fun setData(num: Float) {
        this.num = num
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        markerTv.text = mFormat.format(num.toDouble())
    }

    override fun getOffset(): MPPointF {
        return MPPointF.getInstance(0f, 0f)
    }
}