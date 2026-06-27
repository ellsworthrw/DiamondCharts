/**
 * Copyright 2004-2026 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import kotlin.math.abs
import kotlin.math.floor
import kotlin.random.Random

class RandomData : DefaultData {
    private var minValSet: Double? = null
    private var maxValSet: Double? = null
    private var minXSet: Double? = null
    private var maxXSet: Double? = null
    private var seriesCount_set = 0

    constructor() : super("Random", SIMPLE_SERIES)

    constructor(seriesType: Int) : super("Random", seriesType)

    constructor(seriesType: Int, seriesCount: Int) : super("Random", seriesType) {
        seriesCount_set = seriesCount
    }

    constructor(seriesType: Int, seriesCount: Int, minval: Double, maxval: Double) : super("Random", seriesType) {
        seriesCount_set = seriesCount
        maxValSet = maxval
        minValSet = minval
    }

    constructor(seriesType: Int, seriesCount: Int, minX: Double, maxX: Double, minval: Double, maxval: Double) : super(
        "Random",
        seriesType
    ) {
        seriesCount_set = seriesCount
        minXSet = minX
        maxXSet = maxX
        maxValSet = maxval
        minValSet = minval
    }

    override fun recalc(combineSeries: Boolean) {
        if (seriesType == HLOC_SERIES) {
            recalcHLOC()
            super.recalc(combineSeries)
            return
        }

/*
        if (options and 0x8000 > 0) {
            recalcBellCurve()
            return
        }
*/

        log.d { "RandomGraphData.recalc()" }
        minValue = minValSet ?: 0.0
        maxValue = maxValSet ?: (Random.nextDouble() * 10) // change this factor to adjust range of data (.01 to 1000)
        floor(DateUtil.now())
        val now = DateUtil.now()
        val minX = minXSet ?: if (seriesType and DATE_0TH > 0) now - 3 * DateUtil.ONE_MINUTE else 0.0
        val maxX = maxXSet ?: if (seriesType and DATE_0TH > 0) now + 2 * DateUtil.ONE_MINUTE else 100.0
        val maxval3 = maxValue / 5
        var seriesCount = seriesCount_set
        if (seriesCount_set == 0)
            seriesCount = (Random.nextDouble() * 10).toInt()
        if (seriesCount == 0)
            seriesCount = 1
        var dataCount = (Random.nextDouble() * 100 / seriesCount).toInt()
        if (dataCount < 3)
            dataCount = 3
        else if (dataCount > 10)
            dataCount = 10
        val valueCount = valueCount
        log.d { "valueCount: $valueCount  maxValue: $maxValue  dataCount: $dataCount" }
        this.seriesCount = 0

        this.dataCount = dataCount
        this.seriesCount = seriesCount
        log.d { "getColumnCount: $columnCount" }
        val xInc = (maxX - minX) / dataCount
        var x = minX

        val labels = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten")
        for (i in 0 until seriesCount) {
            if (seriesType and DATE_0TH == 0) {
                if (i < labels.size)
                    setSeriesLabel(i, labels.get(i))
                else
                    setSeriesLabel(i, (i + 1).toString())
            }
            getGraphicAttributes(i).color = Draw.getColor(i)
            getGraphicAttributes(i).symbol = getSymbolFor(i)

            for (j in 0 until dataCount) {
                for (k in 0 until valueCount)
                    if (k == 2) {
                        setDouble(i, j, k, Random.nextDouble() * maxval3)
                    } else {
                        if (seriesType and DATE_0TH > 0 && k == 1) {
                            if (i == 0) { // first series, so calc date for x axis
                                setDouble(i, j, k, x)
                                x += xInc
                            } else { // use same x values as first series
                                setDouble(i, j, k, getValue(0, j, k))
                            }
                        } else {
                            setDouble(i, j, k, Random.nextDouble() * (maxValue - minValue) + minValue)
                        }
                    }
            }
        }
        val months =
            listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        for (j in 0 until dataCount)
            setDataLabel(j, months[j])
        super.recalc(combineSeries)
    }

    private fun getSymbolFor(i: Int): SymbolType {
        val values = SymbolType.values()
        // skip the 0th symbol type which is NONE
        return values[i % (values.size - 1) + 1]
    }

    private fun recalcHLOC() {
        //log.d {  "\nRandomGraphData.recalcHLOC()"  };

        var seriesCount = seriesCount_set
        if (seriesCount_set == 0)
            seriesCount = (Random.nextDouble() * 3).toInt()
        if (seriesCount == 0)
            seriesCount = 1

        val dataCount = (Random.nextDouble() * 40).toInt() + 10
        this.dataCount = 0
        this.seriesCount = 0
        this.dataCount = dataCount
        this.seriesCount = seriesCount
        val dateMin = DateUtil.now() - dataCount
        val totalRange = Random.nextDouble() * 50
        minValue = Random.nextDouble() * 100
        maxValue = minValue + totalRange

        for (i in 0 until seriesCount) {
            setSeriesLabel(i, "Stock " + (i + 1).toString())

            getGraphicAttributes(i).color = Draw.getColor(i)
            var lastY = minValue
            var lastBasis = minValue
            var trend = 1.0
            for (j in 0 until dataCount) {
                setDouble(i, j, ChartData.dateIndex, dateMin + j)
                val range = Random.nextDouble() * totalRange / 5
                val valRange = Random.nextDouble() * totalRange / 20
                var base = lastY + Random.nextDouble() * range * trend
                if (abs(base - lastBasis) > range) {
                    trend *= -1
                    lastBasis = base
                }
                if (base + range > maxValue)
                    base = maxValue - range
                if (base < minValue)
                    base = minValue
                lastY = base
                val low = base
                val high = base + valRange
                val open = base + Random.nextDouble() * valRange
                val close = base + Random.nextDouble() * valRange
                setDouble(i, j, ChartData.highIndex, high)
                setDouble(i, j, ChartData.lowIndex, low)
                setDouble(i, j, ChartData.openIndex, open)
                setDouble(i, j, ChartData.closeIndex, close)
            }
        }
    }

    fun recalcBellCurve() {
        log.d { "RandomGraphData.recalcBellCurve()" }
        maxValue = maxValSet ?: 100.0 // change this factor to adjust range of data (.01 to 1000)
        minValue = minValSet ?: 0.0
        val dataCount = (Random.nextDouble() * 60).toInt() + 40
        this.dataCount = 0
        this.dataCount = dataCount
        seriesCount = 1
        val average = Random.nextDouble() * (maxValue - 40) + 40
        log.d { "  dataCount: $dataCount average: $average" }

        setSeriesLabel(0, "Bell Curve")
        getGraphicAttributes(0).color = Draw.getColor(0)
        getGraphicAttributes(0).symbol = SymbolType.SQUARE
        val div = 20

        for (j in 0 until dataCount) {
            setDouble(0, j, 0, Random.nextDouble() * (maxValue - minValue) + minValue)
            minValue += (average - minValue) / div
            maxValue -= (maxValue - average) / div
        }
    }

    companion object {
        private val log = moduleLogging()

        /** Creates a set of data from 0 to 100 with a somewhat bell curve
         * distribution ie most of the data near an average value. The average value is picked at random.
         */
        fun createBellCurveDistribution(): RandomData {
            val data = RandomData()
//            data.options = 0x8000
            return data
        }
    }
}
