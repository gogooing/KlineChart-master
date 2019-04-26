package com.loro.klinechart.entity;

/**
 * 成交量接口
 */

public interface IVolume {

    /**
     * 开盘价
     */
    float getOpenPrice();

    /**
     * 收盘价
     */
    float getClosePrice();

    /**
     * 成交量
     */
    float getVolume();

    /**
     * 五(月，日，时，分，5分等)均量
     */
    float getMA5Volume();

    /**
     * 十(月，日，时，分，5分等)均量
     */
    float getMA10Volume();
}
