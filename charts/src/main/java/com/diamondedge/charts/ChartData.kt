/*
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 * @author  Reed Ellsworth
 */
package com.diamondedge.charts

import kotlin.math.abs

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

    val id: Any

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
        val log = moduleLogging()

        // datasets with 2 or 3 values
        const val xIndex = 1
        const val yIndex = 0    // 0th value so 2nd value can be dataPtNum if not supplied
        const val zIndex = 2    // index for bubble value

        // HLOC (high, low, open, close) stock data, the 4th value is the timestamp, 0th is close for displaying in LineGraph
        const val dateIndex = 4
        const val highIndex = 1
        const val lowIndex = 2
        const val openIndex = 3
        const val closeIndex = 0

        val COMBINE_SERIES = 0x1
        val COMBINE_PERCENT_SERIES = 0x3
        val GROUP_CENTER = 0x4

        fun dataPointsAtX(x: Double, list: List<ChartData>, interpolate: Boolean = true): List<Triple<ChartData, Double, Int>> {
            log.d { "dataPointsAt($x, $list)" }
            val values = ArrayList<Triple<ChartData, Double, Int>>()
            for (data in list) {
                val closest = findClosest(x, data, 0, ChartData.xIndex, ChartData.yIndex, interpolate)
                log.d { "${data.id}  closest = $closest" }
                if (closest != null) {
                    values.add(closest)
                }
            }
            return values
        }

        fun findClosest(
            value: Double,
            data: ChartData,
            series: Int,
            compareValueNum: Int,
            returnValueNum: Int,
            interpolate: Boolean
        ): Triple<ChartData, Double, Int>? {
            val minVal = if (compareValueNum == ChartData.yIndex) data.minValue else data.minValue2
            val maxVal = if (compareValueNum == ChartData.yIndex) data.maxValue else data.maxValue2
            if (value < minVal || value > maxVal) {
                log.d { "value: $value not within min: $minVal max: $maxVal" }
                // point not within the range of the data set
                return null
            }
            var bestMatch = 0
            var bestMatchVal = 0.0
            for (i in 0 until data.dataCount) {
                val thisVal = data.getDouble(series, i, compareValueNum)
                if (abs(value - thisVal) < abs(value - bestMatchVal)) {
                    bestMatch = i
                    bestMatchVal = thisVal
                }
            }
            log.d { "bestMatch: $bestMatch $bestMatchVal temp: ${data.getDouble(series, bestMatch, returnValueNum)}" }
            if (interpolate) {
                // for variable naming purposes, assume compareValueNum = xIndex and returnValueNum = yIndex
                var x1 = bestMatchVal
                var x2 = bestMatchVal
                var y1 = data.getDouble(series, bestMatch, returnValueNum)
                var y2 = y1

                if (bestMatchVal < value && (bestMatch + 1) < data.dataCount) {
                    x1 = bestMatchVal
                    y1 = data.getDouble(series, bestMatch, returnValueNum)
                    x2 = data.getDouble(series, bestMatch + 1, compareValueNum)
                    y2 = data.getDouble(series, bestMatch + 1, returnValueNum)
                } else if (bestMatchVal > value && (bestMatch - 1) >= 0) {
                    x1 = data.getDouble(series, bestMatch - 1, compareValueNum)
                    y1 = data.getDouble(series, bestMatch - 1, returnValueNum)
                    x2 = bestMatchVal
                    y2 = data.getDouble(series, bestMatch, returnValueNum)
                }
                val y = y1 + (value - x1) * (y2 - y1) / (x2 - x1)
                return Triple(data, y, bestMatch)
            } else {
                return Triple(data, data.getDouble(series, bestMatch, returnValueNum), bestMatch)
            }
        }
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

