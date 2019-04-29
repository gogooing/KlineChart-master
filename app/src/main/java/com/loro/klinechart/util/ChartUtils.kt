package com.loro.klinechart.util

import java.text.DecimalFormat

/**
 * Created by loro on 2017/2/8.
 */
object ChartUtils {

    fun getVolUnit(num: Float): String {

        val e = Math.floor(Math.log10(num.toDouble())).toInt()

        return when {
            e >= 8 -> "亿手"
            e >= 4 -> "万手"
            else -> "手"
        }
    }

    fun getVolUnitNum(num: Float): Int {

        val e = Math.floor(Math.log10(num.toDouble())).toInt()

        return when {
            e >= 8 -> 8
            e >= 4 -> 4
            else -> 1
        }
    }

    fun getVolUnitText(unit: Int, num: Float): String {
        var num = num
        val mFormat: DecimalFormat = if (unit == 1) {
            DecimalFormat("#0")
        } else {
            DecimalFormat("#0.00")
        }
        num /= unit
        return if (num == 0f) {
            "0"
        } else mFormat.format(num.toDouble())
    }

    fun getDecimalFormatVol(vol: Float): String {
        val decimalFormat = DecimalFormat("#0.00")//构造方法的字符格式这里如果小数不足2位,会以0补足.
        return decimalFormat.format(vol.toDouble())//format 返回的是字符串
    }
}
