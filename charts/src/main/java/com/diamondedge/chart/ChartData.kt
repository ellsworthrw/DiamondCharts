/*
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 * @author  Reed Ellsworth
 */
package com.diamondedge.chart

interface ChartData {
    /**
     * Returns the number of series.
     */
    val seriesCount: Int

    /**
     * Returns the number of data points in each series
     * Each series must have the same number of data points.
     */
    val dataCount: Int

    /**
     * returns the number of values for each data point.
     * simple series = 1 (pie, bar)
     * XY data == 2
     * HLOC data = 5
     */
    val valueCount: Int

    /**
     * return the minimum value in the data set for the 1st value
     * This must be set during the recalc() function
     */
    val minValue: Double

    /**
     * return the maximum value in the data set for the 1st value
     * This must be set during the recalc() function
     */
    val maxValue: Double

    /**
     * return the minimum value in the data set for the 2nd value
     * This must be set during the recalc() function
     */
    val minValue2: Double

    /**
     * return the maximum value in the data set for the 2nd value
     * This must be set during the recalc() function
     */
    val maxValue2: Double

    var options: Int

    fun getDouble(series: Int, dataPtNum: Int): Double {
        return getDouble(series, dataPtNum, valueIndex)
    }

    fun getDouble(series: Int, dataPtNum: Int, valueNum: Int): Double

    fun getDataPoint(series: Int, dataPtNum: Int, createIfNull: Boolean): DataPoint?

    fun getSeriesLabel(series: Int): String?

    fun getDataLabel(dataPtNum: Int): String?

    fun getGraphicAttributes(series: Int): GraphicAttributes

    fun recalc()

    fun isEmpty(): Boolean {
        return dataCount == 0
    }

    companion object {

        const val valueIndex = 0

        // datasets with 2 or 3 values
        const val xIndex = 1
        const val yIndex = 0    // 0th value so 2nd value can be dataPtNum if not supplied
        const val zIndex = 2    // index for bubble value

        // HLOC (high, low, open, close) stock data, the 0th value is the timestamp
        const val dateIndex = 0
        const val highIndex = 1
        const val lowIndex = 2
        const val openIndex = 3
        const val closeIndex = 4

        val COMBINE_SERIES = 0x1
        val COMBINE_PERCENT_SERIES = 0x3
        val GROUP_CENTER = 0x4
    }

    /*
data 1,2,3,4 columns of data
  1 bar,pie,line,area,radar,pareto
  2 xy(line,scatter,time)
  3 xy bubble (line,scatter,time)
  4 candle or hloc, high/low/open/close
  ? radar
*/

    //for xyBubble getDoubleAt( ptNum, seriesNum*3 + X )

}

