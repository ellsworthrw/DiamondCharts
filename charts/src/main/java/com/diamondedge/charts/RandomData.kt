/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

class RandomData : DefaultData {
    private var minval_set = 0
    private var maxval_set = 0
    private var seriesCount_set = 0

    constructor() : super("Random", SIMPLE_SERIES)

    constructor(seriesType: Int) : super("Random", seriesType)

    constructor(seriesType: Int, seriesCount: Int) : super("Random", seriesType) {
        seriesCount_set = seriesCount
    }

    constructor(seriesType: Int, minval: Double, maxval: Double) : super("Random", seriesType) {
        maxval_set = maxval.toInt()
        minval_set = minval.toInt()
    }

    override fun recalc() {
        if (seriesType == HLOC_SERIES) {
            recalcHLOC()
            super.recalc()
            return
        }

        if (options and 0x8000 > 0) {
            recalcBellCurve()
            //super.recalc();
            return
        }

        log.d { "RandomGraphData.recalc()" }
        if (maxval_set == 0)
            maxValue = Math.random() * 10 // change this factor to adjust range of data (.01 to 1000)
        else
            maxValue = maxval_set.toDouble()
        minValue = minval_set.toDouble()
        val maxval3 = maxValue / 5
        var seriesCount = seriesCount_set
        if (seriesCount_set == 0)
            seriesCount = (Math.random() * 10).toInt()
        if (seriesCount == 0)
            seriesCount = 1
        var dataCount = (Math.random() * 100 / seriesCount).toInt()
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
        var init_x = 0.0
        if (seriesType and DATE_0TH > 0) {
            val today = Math.floor(DateUtil.toDouble(java.util.Date()))
            init_x = today
        }

        val labels = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten")
        for (i in 0 until seriesCount) {
            if (i < labels.size)
                setSeriesLabel(i, labels.get(i))
            else
                setSeriesLabel(i, (i + 1).toString())

            getGraphicAttributes(i).color = Draw.getColor(i)
            getGraphicAttributes(i).symbol = i % 18 + 1

            for (j in 0 until dataCount) {
                for (k in 0 until valueCount)
                    if (k == 2)
                        setDouble(i, j, k, Math.random() * maxval3)
                    else
                        setDouble(i, j, k, Math.random() * (maxValue - minValue) + minValue + if (k == 0) init_x else 0.0)
            }
        }
        val months =
            listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        for (j in 0 until dataCount)
            setDataLabel(j, months[j])
        super.recalc()
    }

    private fun recalcHLOC() {
        //log.d {  "\nRandomGraphData.recalcHLOC()"  };

        val totalRange = Math.random() * 30
        minValue = Math.random() * 100
        maxValue = minValue + totalRange

        var seriesCount = (Math.random() * 3).toInt()
        if (seriesCount == 0)
            seriesCount = 1
        //seriesCount = 1;
        var dataCount = (Math.random() * 80).toInt() + 30
        if (dataCount < 7)
            dataCount = 7

        dataCount = 0
        seriesCount = 0
        dataCount = dataCount
        seriesCount = seriesCount
        var high = 0.0
        var low = 0.0
        var open = 0.0
        var close = 0.0
        val today = Math.floor(DateUtil.toDouble(java.util.Date()))

        for (i in 0 until seriesCount) {
            setSeriesLabel(i, "Stock " + (i + 1).toString())

            getGraphicAttributes(i).color = Draw.getColor(i)

            for (j in 0 until dataCount) {
                setDouble(i, j, 0, today + j)
                val range = Math.random() * totalRange / 5
                low = minValue + Math.random() * totalRange
                if (low + range > maxValue)
                    low = maxValue - range
                high = low + range
                open = low + Math.random() * range
                close = low + Math.random() * range
                setDouble(i, j, 1, high)
                setDouble(i, j, 2, low)
                setDouble(i, j, 3, open)
                setDouble(i, j, 4, close)
            }
        }
        super.recalc()
    }

    fun recalcBellCurve() {
        log.d { "RandomGraphData.recalcBellCurve()" }
        if (maxval_set == 0)
            maxValue = 100.0 // change this factor to adjust range of data (.01 to 1000)
        else
            maxValue = maxval_set.toDouble()
        minValue = minval_set.toDouble()
        var dataCount = (Math.random() * 60).toInt() + 40
        dataCount = 0
        dataCount = dataCount
        seriesCount = 1
        val average = Math.random() * (maxValue - 40) + 40
        log.d { "  dataCount: $dataCount average: $average" }

        setSeriesLabel(0, "Bell Curve")
        getGraphicAttributes(0).color = Draw.getColor(0)
        getGraphicAttributes(0).symbol = 1
        val div = 20

        for (j in 0 until dataCount) {
            setDouble(0, j, 0, Math.random() * (maxValue - minValue) + minValue)
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
            data.options = 0x8000
            return data
        }
    }
}
