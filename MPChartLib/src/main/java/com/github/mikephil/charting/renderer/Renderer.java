
package com.github.mikephil.charting.renderer;

import com.github.mikephil.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Abstract baseclass of all Renderers.
 * 
 * @author Philipp Jahoda
 */
public abstract class Renderer {

    /** the minimum value on the x-axis that should be plotted */
    protected float mMinX = 0;

    /** the maximum value on the x-axis that should be plotted */
    protected float mMaxX = 0;

    /**
     * the component that handles the drawing area of the chart and it's offsets
     */
    protected ViewPortHandler mViewPortHandler;

    public Renderer(ViewPortHandler viewPortHandler) {
        this.mViewPortHandler = viewPortHandler;
    }

    /**
     * Calculates the minimum and maximum x-value the chart can currently
     * display (with the given zoom level). -> mMinX, mMaxX
     *
     * @param dataProvider
     * @param xAxisModulus
     */
    public void calcXBounds(BarLineScatterCandleBubbleDataProvider dataProvider, int xAxisModulus) {

        float low = dataProvider.getLowestVisibleX();
        float high = dataProvider.getHighestVisibleX();

        int subLow = (low % xAxisModulus == 0) ? xAxisModulus : 0;

        mMinX = Math.max((low / xAxisModulus) * (xAxisModulus) - subLow, 0);
        mMaxX = Math.min((high / xAxisModulus) * (xAxisModulus) + xAxisModulus, (int) dataProvider.getXChartMax());
    }
}
