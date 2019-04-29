package com.loro.klinechart.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import android.util.Log;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.loro.klinechart.bean.KLineEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by loro on 2019/4/26.
 */
public class MyCombinedChart extends CombinedChart {
    private MyLeftMarkerView myMarkerViewLeft;
    private MyHMarkerView myMarkerViewH;
    private MyBottomMarkerView myBottomMarkerView;
    private Map<Float, String> mDatas;

    private String mTag = "";

    public MyCombinedChart(Context context) {
        super(context);
    }

    public MyCombinedChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCombinedChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setChartTag(String tag) {
        this.mTag = tag;
    }

    public void setMarker(MyLeftMarkerView markerLeft, MyHMarkerView markerH, Map<Float, String> datas) {
        this.myMarkerViewLeft = markerLeft;
        this.myMarkerViewH = markerH;
        this.mDatas = datas;
    }

    public void setMarker(MyLeftMarkerView markerLeft, MyBottomMarkerView markerBottom, Map<Float, String> datas) {
        this.myMarkerViewLeft = markerLeft;
        this.myBottomMarkerView = markerBottom;
        this.mDatas = datas;
    }

    public void setMarker(MyLeftMarkerView markerLeft, MyBottomMarkerView markerBottom, MyHMarkerView markerH, Map<Float, String> datas) {
        this.myMarkerViewLeft = markerLeft;
        this.myBottomMarkerView = markerBottom;
        this.myMarkerViewH = markerH;
        this.mDatas = datas;
    }

    @Override
    protected void drawMarkers(Canvas canvas) {
        // if there is no marker view or drawing marker is disabled
        if (!isDrawMarkersEnabled() || !valuesToHighlight())
            return;

        for (int i = 0; i < mIndicesToHighlight.length; i++) {

            Highlight highlight = mIndicesToHighlight[i];

            IDataSet set = mData.getDataSetByHighlight(highlight);

            Entry e = mData.getEntryForHighlight(highlight);

            if ("volumeChart" == mTag)
                Log.e("onValueSelected", "pos = ");
            if (e == null)
                continue;

            int entryIndex = set.getEntryIndex(e);

            // make sure entry not null
            if (entryIndex > set.getEntryCount() * mAnimator.getPhaseX())
                continue;

            float[] pos = getMarkerPosition(highlight);

            if ("volumeChart" == mTag)
                Log.e("onValueSelected", "pos = " + pos);
            // check bounds
            if (!mViewPortHandler.isInBounds(pos[0], pos[1]))
                continue;

            if ("volumeChart" == mTag)
                Log.e("onValueSelected", "touchY = " + mIndicesToHighlight[i].getTouchY());

            if (null != myMarkerViewH) {
                myMarkerViewH.refreshContent(e, mIndicesToHighlight[i]);
                int width = (int) mViewPortHandler.contentWidth();
                myMarkerViewH.setIvWidth(width);
                myMarkerViewH.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                myMarkerViewH.layout(0, 0, width,
                        myMarkerViewH.getMeasuredHeight());
                myMarkerViewH.draw(canvas, mViewPortHandler.contentLeft(), mIndicesToHighlight[i].getTouchY() - myMarkerViewH.getHeight() / 2);
            }

            if (null != myMarkerViewLeft && mIndicesToHighlight[i].getTouchY() >= 0) {
                //修改标记值
                float yValForHighlight = mIndicesToHighlight[i].getTouchYValue();
                myMarkerViewLeft.setData(yValForHighlight);

                myMarkerViewLeft.refreshContent(e, mIndicesToHighlight[i]);

                myMarkerViewLeft.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                myMarkerViewLeft.layout(0, 0, myMarkerViewLeft.getMeasuredWidth(),
                        myMarkerViewLeft.getMeasuredHeight());

                myMarkerViewLeft.draw(canvas, mViewPortHandler.contentLeft(), mIndicesToHighlight[i].getTouchY() - myMarkerViewLeft.getHeight() / 2);

            }

            if (null != myBottomMarkerView) {
                myBottomMarkerView.setData(mDatas.get(mIndicesToHighlight[i].getX()));
                myBottomMarkerView.refreshContent(e, mIndicesToHighlight[i]);

                myBottomMarkerView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                myBottomMarkerView.layout(0, 0, myBottomMarkerView.getMeasuredWidth(),
                        myBottomMarkerView.getMeasuredHeight());

                myBottomMarkerView.draw(canvas, pos[0] - myBottomMarkerView.getWidth() / 2, mViewPortHandler.contentBottom());
            }
        }
    }
}
