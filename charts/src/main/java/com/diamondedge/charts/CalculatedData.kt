/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import com.diamondedge.fn.Statistics

import java.util.Calendar

/**
 * Beta: subject to change.
 */
class CalculatedData(functionType: Int = 0, periodStyle: Int = ALL_POINTS, private val ddata: ChartData) :
    DefaultData(functionType, XY_SERIES) {

    /** The series used when period is greater than 0
     * and there is more than one series. The default is series 0.
     */
    var series = 0
        set(value) {
            field = value
            if (field >= ddata.seriesCount)
                throw IllegalArgumentException("Series not valid. It must be less than $seriesCount")
        }

    /** The function that will calculate each data point.
     */
    var functionType = 0

    /** The alignment of the calculated data point with respect to
     * the set of data (period) used to calculate it.
     */
    var alignment = CENTER

    /** The period style.
     */
    /* NUMERIC_RANGE is only valid range on a SIMPLE_SERIES
     */
    var periodStyle = ALL_POINTS
        set(value) {
            field = value
            if (this.periodStyle and RANGE > 0) {
                when (this.periodStyle) {
                    NUMERIC_RANGE -> valueRange = period
                    SECOND -> valueRange = period / (24 * 3600)
                    MINUTE -> valueRange = period / (24 * 60)
                    HOUR -> valueRange = period / 24
                    DAY -> valueRange = period
                    WEEK -> valueRange = period * 7
                    MONTH -> valueRange = period * 30
                    YEAR -> valueRange = period * 365
                }
            }
        }
    private var period = 0.0
    private var valueRange = 1.0

    private val label: String
        get() {
            when (functionType) {
                AVERAGE -> return "Average"
                COUNT -> return "Count"
                MEDIAN -> return "Median"
                PRODUCT -> return "Product"
                STDEV -> return "Std Dev"
                STDEVP -> return "Std Dev"
                SUM -> return "Sum"
                SUMSQ -> return "Sum of squares"
                VAR -> return "Variance"
                VARP -> return "Variance"
                LOW -> return "Low"
                HIGH -> return "High"
            }
            return "calculated"
        }

    init {
        if (periodStyle == MOVING_PERIOD)
            alignment = RIGHT
        seriesCount = 1
        recalc(false)
        setSeriesLabel(0, label)
        val gattr = getGraphicAttributes(0)
        gattr.color = Color.black
        if (symbol >= SymbolType.SMALL_DOT)
            symbol = SymbolType.SQUARE
        gattr.symbol = symbol
        log.d { "symbol: $symbol" }
//TODO:        symbol++
    }

    /** Returns the period or set of data over which the function will be calculated.
     */
    fun getPeriod(): Double {
        return period
    }

    /** Sets the period or set of data over which the function will be calculated.
     */
    fun setPeriod(value: Double) {
        period = value
        periodStyle = this.periodStyle  // calculate valueRange
    }

    override fun recalc(combineSeries: Boolean) {

        if (ddata is CalculatedData)
            ddata.recalc(combineSeries)  // recalc dependent data too

        if (this.periodStyle == ALL_POINTS || period <= 0)
            recalcAllPoints()
        else if (this.periodStyle and RANGE > 0)
            recalcRange()
        else
            recalcNumPoints()

        super.recalc(combineSeries)
    }

    private fun getAlignedValue(min: Double, max: Double): Double {
        if (alignment == LEFT)
            return min
        else if (alignment == RIGHT)
            return max
        return min + (max - min) / 2
    }

    private fun recalcAllPoints() {
        var dataCount = ddata.dataCount
        val seriesCount = ddata.seriesCount
        dataCount = dataCount
        val value = DoubleArray(seriesCount)
        var minX: Double
        var maxX: Double
        var x: Double
        for (i in 0 until dataCount) {
            minX = i.toDouble()
            maxX = (i + 1).toDouble()
            for (series in 0 until seriesCount) {
                value[series] = ddata.getValue(series, i, ChartData.yIndex)
                if (ChartData.xIndex >= 0) {
                    x = ddata.getValue(series, i, ChartData.xIndex)
                    if (series == 0) {
                        maxX = x
                        minX = maxX
                    } else {
                        if (x < minX)
                            minX = x
                        if (x > maxX)
                            maxX = x
                    }
                }
            }
            setDouble(0, i, 0, getAlignedValue(minX, maxX))  // set X value
            setDouble(0, i, 1, calculate(value))               // set Y value
        }
    }

    private fun recalcNumPoints() {
        //assert( period > 0 )
        var dataCount = ddata.dataCount
        var dataPt = 0
        dataCount = Math.ceil(1.0 * dataCount / period).toInt()
        var value = DoubleArray(period.toInt())
        var minX: Double
        var maxX: Double
        var x: Double
        var i = 0
        while (i < dataCount) {
            minX = i.toDouble()
            maxX = i + period - 1
            if (maxX >= dataCount) {    // last group of points is smaller than period
                maxX = (dataCount - 1).toDouble()
                value = DoubleArray((maxX - minX + 1).toInt())
            }
            var j = 0
            while (j < period && i + j < dataCount) {
                value[j] = ddata.getValue(this.series, i + j, ChartData.yIndex)
                if (ChartData.xIndex >= 0) {
                    x = ddata.getValue(this.series, i + j, ChartData.xIndex)
                    if (j == 0) {
                        maxX = x
                        minX = maxX
                    } else {
                        if (x < minX)
                            minX = x
                        if (x > maxX)
                            maxX = x
                    }
                }
                j++
            }
            setDouble(0, dataPt, 0, getAlignedValue(minX, maxX))  // set X value
            setDouble(0, dataPt, 1, calculate(value))               // set Y value
            dataPt++
            if (this.periodStyle == MOVING_PERIOD) {
                i++
                if (i + period > dataCount)
                    break
            } else
                i += period.toInt()
        }
    }

    private fun getVal(series: Int, i: Int, xIndex: Int, values: DoubleArray): Double {
        return if (xIndex >= 0) ddata.getValue(series, i, xIndex) else values[i]
    }

    private fun recalcRange() {
        // period specifies range of actual data values on x axis
        // to group the data by
        // data must be sorted by x value

        var dataCount = ddata.dataCount
        var dataPt = 0     // index of data point in this GraphData that is being calculated
        var numPts: Int    // number of points in the current range
        var minXdata = 0.0  // minimum value on x axis for entire data
        var maxXdata = (dataCount - 1).toDouble()
        if (ChartData.xIndex >= 0) {
            minXdata = ddata.minValue2
            maxXdata = ddata.maxValue2
        }
        // approximate data count
        dataCount = Math.ceil((maxXdata - minXdata) / valueRange).toInt()
        val values = DoubleArray(dataCount)  // all values in series
        var value: DoubleArray? = null                      // values in the range
        for (i in 0 until dataCount) {
            values[i] = ddata.getValue(this.series, i, ChartData.yIndex)
        }
        if (ChartData.xIndex < 0 && dataCount > 0) {   // cannot sort if have an xIndex since the x & y values will no longer be in the same order
            java.util.Arrays.sort(values)
            minXdata = values[0]
            maxXdata = values[dataCount - 1]
        }
        var minX: Double
        var maxX = minXdata
        var x: Double
        var i = 0
        while (i < dataCount && maxX < maxXdata) {
            minX = maxX
            maxX += nextInc(minX, this.periodStyle and 0xFF, cal)
            if (maxX > maxXdata)
            // last group of points is smaller than period
                maxX = maxXdata

            // find num pts in the range minX to maxX
            numPts = 0
            run {
                var j = 0
                while (i + j < dataCount) {
                    if (getVal(this.series, i + j, ChartData.xIndex, values) >= maxX)
                        break
                    numPts++
                    j++
                }
            }

            /* goes with a change to > maxX above
      if( i == 0 && numPts > 0 )  // first group inclusive of minX and maxX points
        numPts--;                 // so decrease by one to not include maxX point
        */
            if (numPts > 0) {
                if (ChartData.xIndex >= 0) {
                    for (j in 0 until numPts) {
                        x = ddata.getValue(this.series, i + j, ChartData.xIndex)
                        if (j == 0) {
                            maxX = x
                            minX = maxX
                        } else {
                            if (x < minX)
                                minX = x
                            if (x > maxX)
                                maxX = x
                        }
                    }
                }
                log.d { "i: $i numPts: $numPts" }
                log.d { "  min: $minX max: $maxX valueRange: $valueRange" }
                if (value == null || value.size != numPts)
                    value = DoubleArray(numPts)
                System.arraycopy(values, i, value, 0, numPts)
                /*
        System.out.log.d {  "  x: " + getAlignedValue( minX, maxX ) + " y: " + calculate( val ) + " val.length: " + val.length  };
        for( int n = 0; n < val.length; n++ )
          System.out.log.d {  "   n: " + n + " val: " + val[n] + " values: " + values[i + n]  };
        */
                setDouble(0, dataPt, 0, getAlignedValue(minX, maxX))  // set X value
                setDouble(0, dataPt, 1, calculate(value))               // set Y value
                dataPt++
                i += numPts
            }
        }
        // truncate number of data points to the exact amount
        dataCount = dataPt
    }

    private fun calculate(value: DoubleArray): Double {
        when (functionType) {
            AVERAGE -> return Statistics.average(value)
            COUNT -> return value.size.toDouble()
            MEDIAN -> return Statistics.median(value)
            PRODUCT -> return Statistics.product(value)
            STDEV -> return Statistics.stdev(value)
            STDEVP -> return Statistics.stdevp(value)
            SUM -> return Statistics.sum(value)
            SUMSQ -> return Statistics.sumSquares(value)
            VAR -> return Statistics.`var`(value)
            VARP -> return Statistics.varp(value)
            LOW -> return Statistics.min(value)
            HIGH -> return Statistics.max(value)
        }
        return 0.0
    }

    override fun toString(): String {
        return "CalculatedData[fn=" + functionType + "," + toStringParam() + "]"
    }

    companion object {
        private val log = moduleLogging()

        // functionType
        val AVERAGE = 40
        val COUNT = 41
        val MEDIAN = 44
        val PRODUCT = 45
        val STDEV = 47
        val STDEVP = 48
        val SUM = 49
        val SUMSQ = 50
        val VAR = 51
        val VARP = 52
        val LOW = 53
        val HIGH = 54

        // periodAlign
        val LEFT = 1
        val CENTER = 2
        val RIGHT = 3

        // periodStyle
        val ALL_POINTS = 0
        val NUM_POINTS = 1
        val MOVING_PERIOD = 2
        internal val RANGE = 0x100
        val NUMERIC_RANGE = RANGE or 0
        val SECOND = RANGE or Calendar.SECOND
        val MINUTE = RANGE or Calendar.MINUTE
        val HOUR = RANGE or Calendar.HOUR
        val DAY = RANGE or Calendar.DAY_OF_MONTH
        val WEEK = RANGE or Calendar.WEEK_OF_YEAR
        val MONTH = RANGE or Calendar.MONTH
        val YEAR = RANGE or Calendar.YEAR

        private var symbol = SymbolType.SQUARE_SMALL
        private val cal = Calendar.getInstance()

        internal fun nextInc(pos: Double, range: Int, cal: Calendar): Double {
            if (range == WEEK || range == MONTH || range == YEAR) {
                synchronized(cal) {
                    DateUtil.setCalendar(cal, pos)
                    if (range == YEAR)
                        cal.add(Calendar.YEAR, 1)
                    else if (range == MONTH)
                        cal.add(Calendar.MONTH, 1)
                    else
                        cal.add(Calendar.WEEK_OF_YEAR, 1)
                    DateUtil.clearCalBelow(cal, range and 0xFF)
                    return DateUtil.toDouble(cal) - pos
                }
            } else if (range == MINUTE) {
                return DateUtil.ONE_MINUTE
            } else {
                return DateUtil.ONE_SECOND
            }
        }

    }
}
