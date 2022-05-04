/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

class OthersData(private val otherData: ChartData, val seriesNumber: Int = 0) : DefaultData("Others", SIMPLE_SERIES) {
    var othersPercent = 5
    private var dataPointNumbers = ArrayList<Int>()

    init {
        if (!otherData.isEmpty()) {
            seriesCount = 1
            recalc(false)
            val label = otherData.getSeriesLabel(seriesNumber)
            if (label != null)
                setSeriesLabel(0, label)
        }
    }

    override fun recalc(combineSeries: Boolean) {
        if (otherData.isEmpty())
            return

        otherData.recalc(combineSeries)
        val dataCount = otherData.dataCount
        var total = 0.0
        var value: Double
        var percent: Double
        var othersTotal = 0.0
        dataPointNumbers.clear()
        var dataPtNum = 0
        seriesCount = 1

        for (i in 0 until dataCount) {
            total += otherData.getValue(seriesNumber, i)
        }

        for (i in 0 until dataCount) {
            value = otherData.getValue(seriesNumber, i)
            percent = value / total * 100
            if (percent < othersPercent) {
                othersTotal += value
                dataPointNumbers.add(i)
            } else {
                setDouble(0, dataPtNum, value)
                val label = otherData.getDataLabel(i)
                if (label != null)
                    setDataLabel(dataPtNum, label)
                dataPtNum++
            }
        }

        if (othersTotal > 0) {
            if (dataPointNumbers.size > 1) {
                setDouble(0, dataPtNum, othersTotal)
                setDataLabel(dataPtNum, "Others")
            } else {
                val i = dataPointNumbers[0]
                setDouble(0, dataPtNum, otherData.getValue(seriesNumber, i))
                val label = otherData.getDataLabel(i)
                if (label != null)
                    setDataLabel(dataPtNum, label)
            }
        }
        super.recalc(combineSeries)
    }

    override fun toString(): String {
        return "OthersData[%=" + othersPercent + "," + toStringParam() + "]"
    }
}
