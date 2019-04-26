package com.loro.klinechart.entity;

/**
 * MACD指标(指数平滑移动平均线)接口
 * @see <a href="https://baike.baidu.com/item/MACD指标"/>相关说明</a>
 */

public interface IMACD {


    /**
     * DEA值
     */
    float getDea();

    /**
     * DIF值
     */
    float getDif();

    /**
     * MACD值
     */
    float getMacd();

}
