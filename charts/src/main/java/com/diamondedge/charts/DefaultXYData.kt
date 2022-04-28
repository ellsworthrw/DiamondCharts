/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import java.util.Vector

open class DefaultXYData(override val id: Any = "") : XYData() {

    override var maxX = 100.0
    override var minX = 0.0
    override var maxY = 100.0
    override var minY = 0.0

    protected var data = Vector<Pair<Double, Double>>()

    override var dataCount: Int
        get() = data.size
        set(value) {
            updateDataCount(value)
        }

    override fun getX(dataPtNum: Int): Double {
        return data[dataPtNum].first
    }

    override fun getY(dataPtNum: Int): Double {
        return data[dataPtNum].second
    }

    fun setValue(dataPtNum: Int, x: Double, y: Double) {
        if (dataPtNum >= dataCount) {
            updateDataCount(dataPtNum + 1)
        }
        data[dataPtNum] = Pair(x, y)
    }

    override fun recalc() {
        maxX = Double.MIN_VALUE
        minX = Double.MAX_VALUE
        maxY = Double.MIN_VALUE
        minY = Double.MAX_VALUE
        var value: Double

        for (i in 0 until dataCount) {
            value = getX(i)
            if (value < minX)
                minX = value
            if (value > maxX)
                maxX = value

            value = getY(i)
            if (value < minY)
                minY = value
            if (value > maxY)
                maxY = value
        }

        log.d { "Data X min: $minX max: $maxX" }
        log.d { "     Y min: $minY max: $maxY" }
    }

    private fun updateDataCount(newCount: Int) {
        log.d { "updateDataCount($newCount)" }
        if (dataCount != newCount) {
            justifyData(newCount)
        }
    }

    private fun justifyData(newCount: Int) {
        val oldCount = dataCount

        data.setSize(newCount)

        if (newCount >= oldCount) {
            for (i in oldCount until newCount) {
                data[i] = Pair(0.0, 0.0)
            }
        }
    }

    protected fun toStringParam(): String {
        return "size=$dataCount,min=$minValue,max=$maxValue,min2=$minValue2,max2=$maxValue2"
    }

    override fun toString(): String {
        return "DefaultXYData[" + toStringParam() + "]"
    }

    companion object {
        private val log = moduleLogging()
    }
}
