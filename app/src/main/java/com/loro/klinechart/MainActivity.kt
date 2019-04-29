package com.loro.klinechart

import android.graphics.Paint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.CoupleChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.loro.klinechart.bean.KLineEntity
import com.loro.klinechart.chart.MyBottomMarkerView
import com.loro.klinechart.chart.MyHMarkerView
import com.loro.klinechart.chart.MyLeftMarkerView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private var xAxis: XAxis? = null
    private var yAxisLeft: YAxis? = null
    private var yAxisRight: YAxis? = null

    private var mDatas = mutableListOf<KLineEntity>()
    private var mKlineDatas = mutableListOf<CandleEntry>()
    private var mMa5Datas = mutableListOf<Entry>()
    private var mMa10Datas = mutableListOf<Entry>()
    private var mMa20Datas = mutableListOf<Entry>()
    private var mMa30Datas = mutableListOf<Entry>()
    private var mMa60Datas = mutableListOf<Entry>()

    private var mMa5Colors = mutableListOf<Int>()
    private var mMa10Colors = mutableListOf<Int>()
    private var mMa20Colors = mutableListOf<Int>()
    private var mMa30Colors = mutableListOf<Int>()
    private var mMa60Colors = mutableListOf<Int>()
    private var mXVals = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initKLine()
        setMarkerView()
        setChartListener()
        getData()

    }

    private fun initKLine() {
        klineChart.setScaleEnabled(true)//启用图表缩放
        klineChart.setDrawBorders(true)//是否绘制边线
        klineChart.setBorderWidth(1f)//边线宽度，单位dp
        klineChart.setBorderColor(ContextCompat.getColor(this@MainActivity, R.color.border_color))//边线颜色
        klineChart.isDragEnabled = true//启用图表拖拽事件
        klineChart.isScaleYEnabled = false //禁用Y轴上的缩放

        klineChart.description.isEnabled = false //禁用图表描述
        klineChart.description.text = ""//图表描述文字
        klineChart.minOffset = 0f
        klineChart.setExtraOffsets(0f, 0f, 0f, 3f) //设置要附加到图表周围的偏移量

        klineChart.legend.isEnabled = false //禁用图例
//        klineChart.legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
//        klineChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
//        klineChart.legend.orientation = Legend.LegendOrientation.HORIZONTAL
//        klineChart.legend.setDrawInside(false)
//        klineChart.legend.form = Legend.LegendForm.SQUARE
//        klineChart.legend.formSize = 9f
//        klineChart.legend.textSize = 11f
//        klineChart.legend.xEntrySpace = 4f

        //bar x y 轴
        xAxis = klineChart.xAxis
        xAxis?.setDrawLabels(true)//是否显示X坐标轴上的刻度，默认是true
        xAxis?.setDrawGridLines(false)//是否显示X坐标轴上的刻度竖线，默认是true
        xAxis?.setDrawAxisLine(false)//是否绘制坐标轴的线，即含有坐标的那条线，默认是true
        xAxis?.enableGridDashedLine(
            10f,
            10f,
            0f
        )//虚线表示X轴上的刻度竖线(float lineLength, float spaceLength, float phase)三个参数，1.线长，2.虚线间距，3.虚线开始坐标
        xAxis?.textColor = ContextCompat.getColor(this@MainActivity, R.color.text_color_common)//设置x轴文字的颜色
        xAxis?.position = XAxis.XAxisPosition.BOTTOM //设置文字显示在x轴什么位置
        xAxis?.setAvoidFirstLastClipping(true)//设置首尾的值是否自动调整，避免被遮挡

        yAxisLeft = klineChart.axisLeft
        yAxisLeft?.setDrawGridLines(true)//是否显示Y坐标轴上的刻度横线，默认是true
        yAxisLeft?.setDrawAxisLine(false)//是否绘制坐标轴的线，即含有坐标的那条线，默认是true
        yAxisLeft?.setDrawZeroLine(false)//是否绘制0刻度线
        yAxisLeft?.setDrawLabels(true) //是否显示y轴的刻度
        yAxisLeft?.enableGridDashedLine(10f, 10f, 0f)
        yAxisLeft?.textColor = ContextCompat.getColor(this@MainActivity, R.color.text_color_common)
        yAxisLeft?.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)//设置文字显示在x轴什么位置
        yAxisLeft?.labelCount = 4
        yAxisLeft?.spaceTop = 10f //距离顶部留白

        yAxisRight = klineChart.axisRight
        yAxisRight?.setDrawLabels(false)
        yAxisRight?.setDrawGridLines(false)
        yAxisRight?.setDrawAxisLine(false)
        yAxisRight?.labelCount = 4

        klineChart.isDragDecelerationEnabled = true //设置触摸后继续滚动
        klineChart.dragDecelerationFrictionCoef = 0.2f //减速摩擦系数在[0; 1]间隔，更高的值表示速度将缓慢下降
        klineChart.animateXY(2000, 2000) //在x轴和y轴上绘制/渲染图表的动画的时间
    }

    private fun setChartListener() {
        klineChart.onChartGestureListener = CoupleChartGestureListener(klineChart, arrayOf<Chart<*>>(volumeChart))
        klineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {

            }

            override fun onValueSelected(e: Entry?, h: Highlight?) {
//                val highlight = Highlight(h!!.x, h.touchYValue, h.dataIndex, h.dataSetIndex)
//
//                val touchY = h.getTouchY() - mChartKline.getHeight()
//                val h1 = mChartVolume.getHighlightByTouchPoint(h.getXIndex(), touchY)
//                highlight.setTouchY(touchY)
//                if (null == h1) {
//                    highlight.setTouchYValue(0f)
//                } else {
//                    highlight.setTouchYValue(h1!!.getTouchYValue())
//                }
//                mChartVolume.highlightValues(arrayOf<Highlight>(highlight))
//
//                val highlight2 = Highlight(h.getXIndex(), h.getValue(), h.getDataIndex(), h.getDataSetIndex())
//
//                val touchY2 = h.getTouchY() - mChartKline.getHeight() - mChartVolume.getHeight()
//                val h2 = mChartCharts.getHighlightByTouchPoint(h.getXIndex(), touchY2)
//                highlight2.setTouchY(touchY2)
//                if (null == h2) {
//                    highlight2.setTouchYValue(0f)
//                } else {
//                    highlight2.setTouchYValue(h2!!.getTouchYValue())
//                }
//                mChartCharts.highlightValues(arrayOf<Highlight>(highlight2))
//
//                updateText(e.getXIndex())
            }

        })
    }

    private fun setMarkerView() {
//        val leftMarkerView = MyLeftMarkerView(this@MainActivity, R.layout.markerview_kline)
//        val hMarkerView = MyHMarkerView(this@MainActivity, R.layout.markerview_line)
//        val bottomMarkerView = MyBottomMarkerView(this@MainActivity, R.layout.markerview_kline)
//        klineChart.setMarker(leftMarkerView, bottomMarkerView, hMarkerView, mDatas)
    }

    private fun getData() {
        mDatas = DataRequest.getALL(this@MainActivity)
        mKlineDatas.clear()
        mMa5Datas.clear()
        mMa10Datas.clear()
        mMa20Datas.clear()
        mMa30Datas.clear()
        mMa60Datas.clear()

        for (i in 0 until mDatas.size) {
            mKlineDatas.add(
                CandleEntry(
                    i.toFloat(),
                    mDatas[i].highPrice,
                    mDatas[i].lowPrice,
                    mDatas[i].openPrice,
                    mDatas[i].closePrice
                )
            )
            mMa5Datas.add(Entry(i.toFloat(), mDatas[i].mA5Price))
            mMa10Datas.add(Entry(i.toFloat(), mDatas[i].mA10Price))
            mMa20Datas.add(Entry(i.toFloat(), mDatas[i].mA20Price))
            mMa30Datas.add(Entry(i.toFloat(), mDatas[i].mA30Price))
            mMa60Datas.add(Entry(i.toFloat(), mDatas[i].mA60Price))

            when (i) {
                in 0 until 5 -> {
                    mMa5Colors.add(R.color.transparent)
                    mMa10Colors.add(R.color.transparent)
                    mMa20Colors.add(R.color.transparent)
                    mMa30Colors.add(R.color.transparent)
                    mMa60Colors.add(R.color.transparent)
                }
                in 5 until 10 -> {
                    mMa5Colors.add(R.color.ma5)
                    mMa10Colors.add(R.color.transparent)
                    mMa20Colors.add(R.color.transparent)
                    mMa30Colors.add(R.color.transparent)
                    mMa60Colors.add(R.color.transparent)
                }
                in 10 until 20 -> {
                    mMa5Colors.add(R.color.ma5)
                    mMa10Colors.add(R.color.ma10)
                    mMa20Colors.add(R.color.transparent)
                    mMa30Colors.add(R.color.transparent)
                    mMa60Colors.add(R.color.transparent)
                }
                in 20 until 30 -> {
                    mMa5Colors.add(R.color.ma5)
                    mMa10Colors.add(R.color.ma10)
                    mMa20Colors.add(R.color.ma20)
                    mMa30Colors.add(R.color.transparent)
                    mMa60Colors.add(R.color.transparent)
                }
                in 30 until 60 -> {
                    mMa5Colors.add(R.color.ma5)
                    mMa10Colors.add(R.color.ma10)
                    mMa20Colors.add(R.color.ma20)
                    mMa30Colors.add(R.color.ma30)
                    mMa60Colors.add(R.color.transparent)
                }
                else -> {
                    mMa5Colors.add(R.color.ma5)
                    mMa10Colors.add(R.color.ma10)
                    mMa20Colors.add(R.color.ma20)
                    mMa30Colors.add(R.color.ma30)
                    mMa60Colors.add(R.color.ma60)
                }
            }

            mXVals.add(mDatas[i].date ?: "")
        }
        if (!mDatas.isNullOrEmpty()) {
            klineChart.data = generateCombinedData()
            klineChart.invalidate()
        }
    }

    private fun generateCombinedData(): CombinedData {
        val data = CombinedData()
        data.setData(generateCandleData())
        data.setData(generateMaData())
        return data
    }

    private fun generateCandleData(): CandleData {
        val candleData = CandleData()
        val entries = ArrayList<CandleEntry>()
        for (i in 0 until mDatas.size) {
            entries.add(
                CandleEntry(
                    i.toFloat(),
                    mDatas[i].highPrice,
                    mDatas[i].lowPrice,
                    mDatas[i].openPrice,
                    mDatas[i].closePrice
                )
            )
        }

        val set = CandleDataSet(entries, "")
        set.setDrawHorizontalHighlightIndicator(false)
        set.isHighlightEnabled = true
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.shadowWidth = 1f
        set.valueTextSize = 10f
        set.decreasingColor = ContextCompat.getColor(this@MainActivity, R.color.decreasing_color)//设置开盘价高于收盘价的颜色
        set.decreasingPaintStyle = Paint.Style.FILL
        set.increasingColor = ContextCompat.getColor(this@MainActivity, R.color.increasing_color)//设置开盘价地狱收盘价的颜色
        set.increasingPaintStyle = Paint.Style.FILL
        set.neutralColor = ContextCompat.getColor(this@MainActivity, R.color.increasing_color)//设置开盘价等于收盘价的颜色
        set.shadowColorSameAsCandle = true
        set.highlightLineWidth = 1f
        set.highLightColor = ContextCompat.getColor(this@MainActivity, R.color.marker_line_bg)
        set.setDrawValues(true)
        set.valueTextColor = ContextCompat.getColor(this@MainActivity, R.color.marker_text_bg)

        candleData.addDataSet(set)
        return candleData
    }

    private fun generateMaData(): LineData {
        val sets = ArrayList<ILineDataSet>()
        /******此处修复如果显示的点的个数达不到MA均线的位置所有的点都从0开始计算最小值的问题******************************/
        sets.add(setMaLine(5, mMa5Datas))
        sets.add(setMaLine(10, mMa10Datas))
        sets.add(setMaLine(20, mMa20Datas))
        sets.add(setMaLine(30, mMa30Datas))
        sets.add(setMaLine(60, mMa60Datas))

        return LineData(sets)
    }

    private fun setMaLine(ma: Int, lineEntries: MutableList<Entry>): LineDataSet {
        val lineDataSetMa = LineDataSet(lineEntries, "ma$ma")
        if (ma == 5) {
            lineDataSetMa.isHighlightEnabled = true
            lineDataSetMa.setDrawHorizontalHighlightIndicator(false)
            lineDataSetMa.highLightColor = ContextCompat.getColor(this@MainActivity, R.color.marker_line_bg)
        } else {/*此处必须得写*/
            lineDataSetMa.isHighlightEnabled = false
        }
        lineDataSetMa.setDrawValues(false)
        when (ma) {
            5 -> lineDataSetMa.color = ContextCompat.getColor(this@MainActivity, R.color.ma5)
            10 -> lineDataSetMa.color = ContextCompat.getColor(this@MainActivity, R.color.ma10)
            20 -> lineDataSetMa.color = ContextCompat.getColor(this@MainActivity, R.color.ma20)
            30 -> lineDataSetMa.color = ContextCompat.getColor(this@MainActivity, R.color.ma30)
            else -> lineDataSetMa.color = ContextCompat.getColor(this@MainActivity, R.color.ma60)
//
//            5 -> lineDataSetMa.colors = mMa5Colors
//            10 -> lineDataSetMa.colors = mMa10Colors
//            20 -> lineDataSetMa.colors = mMa20Colors
//            30 -> lineDataSetMa.colors = mMa30Colors
//            else -> lineDataSetMa.colors = mMa60Colors
        }
        lineDataSetMa.lineWidth = 1f
        lineDataSetMa.setDrawCircles(false)
        lineDataSetMa.axisDependency = YAxis.AxisDependency.LEFT
        lineDataSetMa.isHighlightEnabled = false
        return lineDataSetMa
    }

}
