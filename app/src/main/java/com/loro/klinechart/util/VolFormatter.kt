package com.loro.klinechart.util


import com.github.mikephil.charting.formatter.ValueFormatter

import java.text.DecimalFormat

class VolFormatter(private val unit: Int) : ValueFormatter() {
    private var mFormat: DecimalFormat? = null
    private val u: String

    init {
        mFormat = if (unit == 1) {
            DecimalFormat("#0")
        } else {
            DecimalFormat("#0.00")
        }
        this.u = ChartUtils.getVolUnit(unit.toFloat())
    }


    override fun getFormattedValue(value: Float): String {
        var value = value
        value /= unit
        return if (value == 0f) {
            u
        } else mFormat!!.format(value.toDouble())
    }

}
