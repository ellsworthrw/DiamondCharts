/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

open class XYGraph(data: ChartData, drawLine: Boolean = true, fillArea: Boolean = false, showBubble: Boolean = false) :
    LineGraph(data, drawLine, fillArea, showBubble) {

    override fun createHorizontalAxis(): Axis {
        return DecimalAxis()
    }

    override fun getX(series: Int, dataPt: Int): Double {
        return (data as? XYData)?.getX(dataPt) ?: when (data.valueCount) {
            1 -> dataPt.toDouble()   // use the dataPtNum as the value on the x axis when only one value is supplied in the dataset
            5 -> data.getValue(series, dataPt, ChartData.dateIndex)
            else -> data.getValue(series, dataPt, ChartData.xIndex)
        }
    }

    override fun getY(series: Int, dataPt: Int): Double {
        return (data as? XYData)?.getY(dataPt) ?: when (data.valueCount) {
            5 -> data.getValue(series, dataPt, ChartData.closeIndex)
            else -> data.getValue(series, dataPt, ChartData.yIndex)
        }
    }

    override fun toString(): String {
        return "XYGraph[" + toStringParam() + "]"
    }
}
