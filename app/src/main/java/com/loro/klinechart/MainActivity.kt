package com.loro.klinechart

import android.graphics.Paint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
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
import com.loro.klinechart.chart.MyCombinedChart
import com.loro.klinechart.chart.MyHMarkerView
import com.loro.klinechart.chart.MyLeftMarkerView
import com.loro.klinechart.util.ChartUtils
import com.loro.klinechart.util.VolFormatter
import com.loro.klinechart.util.XVolFormatter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private var xAxis: XAxis? = null
    private var xAxisVolume: XAxis? = null
    private var xAxisIndex: XAxis? = null
    private var yAxisLeft: YAxis? = null
    private var yAxisLeftVolume: YAxis? = null
    private var yAxisLeftIndex: YAxis? = null
    private var yAxisRight: YAxis? = null
    private var yAxisRightVolume: YAxis? = null
    private var yAxisRightIndex: YAxis? = null

    private var mMaxVolume = 0f
    private var mDatas = mutableListOf<KLineEntity>()
    private var mKlineDatas = mutableListOf<CandleEntry>()
    private var mVolumeDatas = mutableListOf<BarEntry>()
    private var mVolumeMa5Datas = mutableListOf<Entry>()
    private var mVolumeMa10Datas = mutableListOf<Entry>()
    private var mMa5Datas = mutableListOf<Entry>()
    private var mMa10Datas = mutableListOf<Entry>()
    private var mMa20Datas = mutableListOf<Entry>()
    private var mMa30Datas = mutableListOf<Entry>()
    private var mMa60Datas = mutableListOf<Entry>()

    private var mMacdDatas = mutableListOf<BarEntry>()
    private var mDeaDatas = mutableListOf<Entry>()
    private var mDifDatas = mutableListOf<Entry>()

    private var mMa5Colors = mutableListOf<Int>()
    private var mMa10Colors = mutableListOf<Int>()
    private var mMa20Colors = mutableListOf<Int>()
    private var mMa30Colors = mutableListOf<Int>()
    private var mMa60Colors = mutableListOf<Int>()
    private var mXVals = mutableMapOf<Float, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initKLine()
        initVolume()
        initIndex()

        setMarkerView()
        setMarkerView(volumeChart)
        setMarkerView(indexChart)

        volumeChart.setChartTag("volumeChart")

        setChartListener()
        getData()

        if (!mDatas.isNullOrEmpty()) {
            setKlineChart()
            setVolumeChart()
            setMACDByChart()
        }

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

//        klineChart.isHighlightFullBarEnabled = true

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

    private fun initVolume() {
        volumeChart.setDrawBorders(true)  //边框是否显示
        volumeChart.setBorderWidth(1f)//边框的宽度，float类型，dp单位
        volumeChart.setBorderColor(ContextCompat.getColor(this@MainActivity, R.color.border_color))//边框颜色
        volumeChart.description.isEnabled = false //禁用图表描述
        volumeChart.description.text = ""//图表描述文字
        volumeChart.isDragEnabled = true// 是否可以拖拽
        volumeChart.isScaleYEnabled = false //是否可以缩放 仅y轴
        volumeChart.minOffset = 3f
        volumeChart.setExtraOffsets(0f, 0f, 0f, 5f)
//        volumeChart.isHighlightFullBarEnabled = true
        val legend = volumeChart.legend // 设置比例图标示，就是那个一组y的value的
        legend.isEnabled = false//是否绘制比例图

        //bar x y轴
        xAxisVolume = volumeChart.xAxis
        xAxisVolume?.isEnabled = false

        yAxisLeftVolume = volumeChart.axisLeft
        yAxisLeftVolume?.axisMinimum = 0f//设置Y轴坐标最小为多少
        yAxisLeftVolume?.setDrawGridLines(true)
        yAxisLeftVolume?.setDrawAxisLine(false)
        yAxisLeftVolume?.setDrawLabels(true)
        yAxisLeftVolume?.enableGridDashedLine(10f, 10f, 0f)
        yAxisLeftVolume?.textColor = ContextCompat.getColor(this@MainActivity, R.color.text_color_common)
        yAxisLeftVolume?.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        yAxisLeftVolume?.setLabelCount(1, false) //第一个参数是Y轴坐标的个数，第二个参数是 是否不均匀分布，true是不均匀分布
        yAxisLeftVolume?.spaceTop = 0f//距离顶部留白

        yAxisRightVolume = volumeChart.axisRight
        yAxisRightVolume?.setDrawLabels(false)
        yAxisRightVolume?.setDrawGridLines(false)
        yAxisRightVolume?.setDrawAxisLine(false)

        volumeChart.isDragDecelerationEnabled = true
        volumeChart.dragDecelerationFrictionCoef = 0.2f

        volumeChart.animateXY(2000, 2000)
    }

    private fun initIndex() {
        indexChart.setScaleEnabled(true)//启用图表缩放事件
        indexChart.setDrawBorders(true)//是否绘制边线
        indexChart.setBorderWidth(1f)//边线宽度，单位dp
        indexChart.isDragEnabled = true//启用图表拖拽事件
        indexChart.isScaleYEnabled = false//启用Y轴上的缩放
        indexChart.setBorderColor(resources.getColor(R.color.border_color))//边线颜色
        indexChart.description.isEnabled = false //禁用图表描述
        indexChart.description.text = ""//图表描述文字
        indexChart.minOffset = 0f
        indexChart.setExtraOffsets(0f, 0f, 0f, 3f)
//        indexChart.isHighlightFullBarEnabled = true
        val lineChartLegend = indexChart.legend
        lineChartLegend.isEnabled = false//是否绘制 Legend 图例

        //bar x y轴
        xAxisIndex = indexChart.xAxis
        xAxisIndex?.isEnabled = false

        yAxisLeftIndex = indexChart.axisLeft
        yAxisLeftIndex?.setDrawGridLines(true)
        yAxisLeftIndex?.setDrawAxisLine(false)
        yAxisLeftIndex?.setDrawLabels(true)
        yAxisLeftIndex?.enableGridDashedLine(10f, 10f, 0f)
        yAxisLeftIndex?.textColor = ContextCompat.getColor(this@MainActivity, R.color.text_color_common)
        yAxisLeftIndex?.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        yAxisLeftIndex?.setLabelCount(1, false) //第一个参数是Y轴坐标的个数，第二个参数是 是否不均匀分布，true是不均匀分布


        yAxisRightIndex = indexChart.axisRight
        yAxisRightIndex?.setDrawLabels(false)
        yAxisRightIndex?.setDrawGridLines(false)
        yAxisRightIndex?.setDrawAxisLine(false)

        indexChart.isDragDecelerationEnabled = true
        indexChart.dragDecelerationFrictionCoef = 0.2f

        indexChart.animateXY(2000, 2000)
    }

    private fun setChartListener() {
        klineChart.onChartGestureListener =
            CoupleChartGestureListener(klineChart, arrayOf<Chart<*>>(volumeChart, indexChart))
        volumeChart.onChartGestureListener =
            CoupleChartGestureListener(volumeChart, arrayOf<Chart<*>>(klineChart, indexChart))
        indexChart.onChartGestureListener =
            CoupleChartGestureListener(indexChart, arrayOf<Chart<*>>(volumeChart, klineChart))

        klineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {
                volumeChart.highlightValue(null)
                indexChart.highlightValue(null)
            }

            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val highlight = Highlight(h!!.x, h.y, h.dataIndex, h.dataSetIndex)
//                val highlight = h!!

                val touchY = h.touchY - klineChart.height
                val h1 = volumeChart.getHighlightByTouchPoint(h.x, touchY)
                highlight.touchY = touchY
                if (null == h1) {
                    highlight.touchYValue = 0f
                } else {
                    highlight.touchYValue = h1!!.touchYValue
                }
                volumeChart.highlightValues(arrayOf(highlight))

                val highlight2 = Highlight(h!!.x, h.y, h.dataIndex, h.dataSetIndex)

                val touchY2 = h.touchY - klineChart.height - volumeChart.height
                val h2 = indexChart.getHighlightByTouchPoint(h.x, touchY2)
                highlight2.touchY = touchY2
                if (null == h2) {
                    highlight2.touchYValue = 0f
                } else {
                    highlight2.touchYValue = h2!!.touchYValue
                }
                indexChart.highlightValues(arrayOf(highlight2))
            }

        })

        volumeChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val highlight = Highlight(h!!.x, h.y, h.dataIndex, h.dataSetIndex)

                val touchY = h.touchY + klineChart.height
                val h1 = klineChart.getHighlightByTouchPoint(h.x, touchY)
                highlight.touchY = touchY
                if (null == h1) {
                    highlight.touchYValue = 0f
                } else {
                    highlight.touchYValue = h1!!.touchYValue
                }
                klineChart.highlightValues(arrayOf(highlight))

                val highlight2 = Highlight(h!!.x, h.y, h.dataIndex, h.dataSetIndex)

                val touchY2 = h.touchY - volumeChart.height
                val h2 = indexChart.getHighlightByTouchPoint(h.x, touchY2)
                highlight2.touchY = touchY2
                if (null == h2) {
                    highlight2.touchYValue = 0f
                } else {
                    highlight2.touchYValue = h2!!.touchYValue
                }
                indexChart.highlightValues(arrayOf(highlight2))
            }

            override fun onNothingSelected() {
                klineChart.highlightValue(null)
                indexChart.highlightValue(null)
            }
        })

        indexChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val highlight = Highlight(h!!.x, h.y, h.dataIndex, h.dataSetIndex)

                val touchY = h.touchY + volumeChart.height
                val h1 = volumeChart.getHighlightByTouchPoint(h.x, touchY)
                highlight.touchY = touchY
                if (null == h1) {
                    highlight.touchYValue = 0f
                } else {
                    highlight.touchYValue = h1!!.touchYValue
                }
                volumeChart.highlightValues(arrayOf(highlight))

                val highlight2 = Highlight(h!!.x, h.y, h.dataIndex, h.dataSetIndex)

                val touchY2 = h.touchY + volumeChart.height + klineChart.height
                val h2 = klineChart.getHighlightByTouchPoint(h.x, touchY2)
                highlight2.touchY = touchY2
                if (null == h2) {
                    highlight2.touchYValue = 0f
                } else {
                    highlight2.touchYValue = h2!!.touchYValue
                }
                klineChart.highlightValues(arrayOf(highlight2))

            }

            override fun onNothingSelected() {
                klineChart.highlightValue(null)
                volumeChart.highlightValue(null)
            }
        })

    }

    private fun setMarkerView() {
        val leftMarkerView = MyLeftMarkerView(this@MainActivity, R.layout.markerview_kline)
        val hMarkerView = MyHMarkerView(this@MainActivity, R.layout.markerview_line)
        val bottomMarkerView = MyBottomMarkerView(this@MainActivity, R.layout.markerview_kline)
        klineChart.setMarker(leftMarkerView, bottomMarkerView, hMarkerView, mXVals)
    }

    private fun setMarkerView(combinedChart: MyCombinedChart) {
        val leftMarkerView = MyLeftMarkerView(this@MainActivity, R.layout.markerview_kline)
        val hMarkerView = MyHMarkerView(this@MainActivity, R.layout.markerview_line)
        combinedChart.setMarker(leftMarkerView, hMarkerView, mXVals)
    }


    private fun getData() {
        mDatas = DataRequest.getALL(this@MainActivity) {
            mMaxVolume = it
        }
        mKlineDatas.clear()
        mMa5Datas.clear()
        mMa10Datas.clear()
        mMa20Datas.clear()
        mMa30Datas.clear()
        mMa60Datas.clear()
        mVolumeDatas.clear()
        mVolumeMa5Datas.clear()
        mVolumeMa10Datas.clear()
        mMacdDatas.clear()
        mDeaDatas.clear()
        mDifDatas.clear()

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
            mVolumeDatas.add(
                BarEntry(
                    i, mDatas[i].highPrice,
                    mDatas[i].lowPrice,
                    mDatas[i].openPrice,
                    mDatas[i].closePrice, mDatas[i].Volume
                )
            )
            mXVals[i.toFloat()] = mDatas[i].date ?: ""

            mVolumeMa5Datas.add(Entry(i.toFloat(), mDatas[i].mA5Volume))
            mVolumeMa10Datas.add(Entry(i.toFloat(), mDatas[i].mA10Volume))
            mMa5Datas.add(Entry(i.toFloat(), mDatas[i].mA5Price))
            mMa10Datas.add(Entry(i.toFloat(), mDatas[i].mA10Price))
            mMa20Datas.add(Entry(i.toFloat(), mDatas[i].mA20Price))
            mMa30Datas.add(Entry(i.toFloat(), mDatas[i].mA30Price))
            mMa60Datas.add(Entry(i.toFloat(), mDatas[i].mA60Price))
            mMacdDatas.add(BarEntry(i.toFloat(), mDatas[i].macd))
            mDeaDatas.add(Entry(i.toFloat(), mDatas[i].dea))
            mDifDatas.add(Entry(i.toFloat(), mDatas[i].dif))
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

        }

    }


    private fun setKlineChart() {

        klineChart.xAxis.valueFormatter = XVolFormatter(mXVals)

        val set = CandleDataSet(mKlineDatas, "")
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

        val sets = ArrayList<ILineDataSet>()
        /******此处修复如果显示的点的个数达不到MA均线的位置所有的点都从0开始计算最小值的问题******************************/
        sets.add(setMaLine(5, mMa5Datas))
        sets.add(setMaLine(10, mMa10Datas))
        sets.add(setMaLine(20, mMa20Datas))
        sets.add(setMaLine(30, mMa30Datas))
        sets.add(setMaLine(60, mMa60Datas))

        val data = CombinedData()
        data.setData(CandleData(set))
        data.setData(LineData(sets))

        klineChart.data = data
        setHandler(klineChart)
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

    private fun setVolumeChart() {
        val unit = ChartUtils.getVolUnit(mMaxVolume)
        val wan = getString(R.string.wan_unit)
        val yi = getString(R.string.yi_unit)
        var u = 1
        if (wan == unit) {
            u = 4
        } else if (yi == unit) {
            u = 8
        }
        volumeChart.axisLeft.valueFormatter = VolFormatter(Math.pow(10.0, u.toDouble()).toInt())

        val set = BarDataSet(mVolumeDatas, "成交量")
        set.isHighlightEnabled = true
        set.highLightAlpha = 255
        set.highLightColor = ContextCompat.getColor(this@MainActivity, R.color.marker_line_bg)
        set.setDrawValues(false)

        val list = ArrayList<Int>()
        list.add(ContextCompat.getColor(this@MainActivity, R.color.increasing_color))
        list.add(ContextCompat.getColor(this@MainActivity, R.color.decreasing_color))
        set.colors = list
        val barData = BarData(set)

        val sets = ArrayList<ILineDataSet>()

        /******此处修复如果显示的点的个数达不到MA均线的位置所有的点都从0开始计算最小值的问题 */
        sets.add(setMaLine(5, mVolumeMa5Datas))
        sets.add(setMaLine(10, mVolumeMa5Datas))

        val lineData = LineData(sets)

        val combinedData = CombinedData()
        combinedData.setData(barData)
        combinedData.setData(lineData)

        volumeChart.data = combinedData

        setHandler(volumeChart)
    }

    private fun setMACDByChart(isRefresh: Boolean = true) {
        val set = BarDataSet(mMacdDatas, "BarDataSet")
        set.isHighlightEnabled = true
        set.highLightAlpha = 255
        set.highLightColor = ContextCompat.getColor(this@MainActivity, R.color.marker_line_bg)
        set.setDrawValues(false)
        set.axisDependency = YAxis.AxisDependency.LEFT
        val list = ArrayList<Int>()
        list.add(ContextCompat.getColor(this@MainActivity, R.color.increasing_color))
        list.add(ContextCompat.getColor(this@MainActivity, R.color.decreasing_color))
        set.colors = list

        val barData = BarData(set)

        val sets = ArrayList<ILineDataSet>()
        sets.add(setMACDMaLine(0, mDeaDatas))
        sets.add(setMACDMaLine(1, mDifDatas))
        val lineData = LineData(sets)

        val combinedData = CombinedData()
        combinedData.setData(barData)
        combinedData.setData(lineData)
        indexChart.data = combinedData

        if (isRefresh)
            setHandler(indexChart)
    }

    private fun setMACDMaLine(type: Int, lineEntries: MutableList<Entry>): LineDataSet {
        val lineDataSetMa = LineDataSet(lineEntries, "ma$type")
        lineDataSetMa.isHighlightEnabled = false
        lineDataSetMa.setDrawValues(false)

        //DEA
        if (type == 0) {
            lineDataSetMa.color = ContextCompat.getColor(this@MainActivity, R.color.ma5)
        } else {
            lineDataSetMa.color = ContextCompat.getColor(this@MainActivity, R.color.ma10)
        }

        lineDataSetMa.lineWidth = 1f
        lineDataSetMa.setDrawCircles(false)
        lineDataSetMa.axisDependency = YAxis.AxisDependency.LEFT

        return lineDataSetMa
    }

    private fun setHandler(combinedChart: MyCombinedChart) {
        val viewPortHandlerBar = combinedChart.viewPortHandler
        viewPortHandlerBar.setMaximumScaleX(culcMaxscale(mDatas.size.toFloat()))
        val touchMatrix = viewPortHandlerBar.matrixTouch
        val xScale = 3f
        touchMatrix.postScale(xScale, 1f)
    }

    private fun culcMaxscale(count: Float): Float {
        var max = 1f
        max = count / 127 * 5
        return max
    }

}
