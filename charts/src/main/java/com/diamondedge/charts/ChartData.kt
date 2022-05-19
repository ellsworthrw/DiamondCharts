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
     * simple series = 1 (pie, bar, line, area)
     * XY data == 2 (XYGraph)
     * HLOC data = 5 (StockChart)
     */
    val valueCount: Int

    /**
     * The minimum value in the data set for the 1st value
     * This must be set during the recalc() function
     */
    val minValue: Double

    /**
     * The maximum value in the data set for the 1st value
     * This must be set during the recalc() function
     */
    val maxValue: Double

    /**
     * The minimum value in the data set for the 2nd value
     * This must be set during the recalc() function
     */
    val minValue2: Double

    /**
     * The maximum value in the data set for the 2nd value
     * This must be set during the recalc() function
     */
    val maxValue2: Double

    val id: Any
        get() = ""

    fun getValue(series: Int, dataPtNum: Int): Double {
        return getValue(series, dataPtNum, valueIndex)
    }

    fun getValue(series: Int, dataPtNum: Int, valueIndex: Int): Double

    fun getSeriesLabel(series: Int): String?

    fun getDataLabel(dataPtNum: Int): String?

    fun getGraphicAttributes(series: Int): GraphicAttributes

    fun recalc(combineSeries: Boolean)

    fun isEmpty(): Boolean {
        return dataCount == 0
    }

    companion object {
        val log = moduleLogging()

        // valueIndex for datasets with 1 value (bar,pie,line,area,radar,pareto)
        const val valueIndex = 0

        // valueIndex for datasets with 2 or 3 values (XYGraph)
        const val xIndex = 1
        const val yIndex = 0    // 0th value so 2nd value can be dataPtNum if not supplied
        const val zIndex = 2    // index for bubble value

        // valueIndex for HLOC (high, low, open, close) stock data with date on x axis
        const val highIndex = 2
        const val lowIndex = 3
        const val openIndex = 4
        const val closeIndex = 0
        const val dateIndex = 1

        fun dataPointsAtX(x: Double, list: List<ChartData>, interpolate: Boolean = true): List<Triple<ChartData, Double, Int>> {
            log.d { "dataPointsAt($x, $list)" }
            val values = ArrayList<Triple<ChartData, Double, Int>>()
            for (data in list) {
                val closest = findClosest(x, data, 0, xIndex, yIndex, interpolate)
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
            compareValueIndex: Int,
            returnValueNum: Int,
            interpolate: Boolean
        ): Triple<ChartData, Double, Int>? {
            val minVal = if (compareValueIndex == yIndex) data.minValue else data.minValue2
            val maxVal = if (compareValueIndex == yIndex) data.maxValue else data.maxValue2
            if (value < minVal || value > maxVal || data.dataCount == 0) {
                log.d { "value: $value not within min: $minVal max: $maxVal" }
                // point not within the range of the data set
                return null
            }
            var bestMatch = 0
            var bestMatchVal = 0.0
            for (i in 0 until data.dataCount) {
                val thisVal = data.getValue(series, i, compareValueIndex)
                if (abs(value - thisVal) < abs(value - bestMatchVal)) {
                    bestMatch = i
                    bestMatchVal = thisVal
                }
            }
            val bestMatchY = data.getValue(series, bestMatch, returnValueNum)
            log.d { "bestMatch: index: $bestMatch  coord: ($bestMatchVal, $bestMatchY)" }
            if (interpolate) {
                // for variable naming purposes, assume compareValueNum = xIndex and returnValueNum = yIndex
                val x1: Double
                val x2: Double
                val y1: Double
                val y2: Double

                if (value > bestMatchVal && (bestMatch + 1) < data.dataCount) {
                    x1 = bestMatchVal
                    y1 = bestMatchY
                    x2 = data.getValue(series, bestMatch + 1, compareValueIndex)
                    y2 = data.getValue(series, bestMatch + 1, returnValueNum)
                } else if (value < bestMatchVal && (bestMatch - 1) >= 0) {
                    x1 = data.getValue(series, bestMatch - 1, compareValueIndex)
                    y1 = data.getValue(series, bestMatch - 1, returnValueNum)
                    x2 = bestMatchVal
                    y2 = bestMatchY
                } else {
                    val firstValue = data.getValue(series, 0, compareValueIndex)
                    val lastValue = data.getValue(series, data.dataCount - 1, compareValueIndex)
                    if (value < firstValue || value > lastValue) {
                        // not inside the boundaries of the dataset. minVal or maxVal are probably not right
                        if (minVal < firstValue)
                            log.e { "error: minVal $minVal is less than first value $firstValue. Please correct the calculation of min/max values in your ChartData.recalc function." }
                        if (maxVal > lastValue)
                            log.e { "error: maxVal $maxVal is greater than last value $lastValue. Please correct the calculation of min/max values in your ChartData.recalc function." }
                        log.d { "value: $value not within first value: $firstValue and last value: $lastValue" }
                        return null
                    }
                    return Triple(data, bestMatchY, bestMatch)
                }
                val y = y1 + (value - x1) * (y2 - y1) / (x2 - x1)
                return Triple(data, y, bestMatch)
            } else {
                return Triple(data, bestMatchY, bestMatch)
            }
        }
    }
}
