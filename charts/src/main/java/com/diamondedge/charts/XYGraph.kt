/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

/**
 * `XYGraph` provides functionality to display data as a line graph.
 * The horizontal axis is a decimal axis.
 *
 * @param data The [ChartData] object containing the data to be displayed.
 * @param drawLine Determines whether to draw the line connecting data points (default is true).
 * @param fillArea Determines whether to fill the area under the line (default is false).
 * @param showBubble Determines whether to display bubbles at data points (default is false).
 * @param curveSmothing Determines whether to apply curve smoothing to the line (default is false).
 * @param fillAreaToMinimum Determines whether to fill the area between the line and the minimum y value (default is false).
 */
open class XYGraph(
    data: ChartData,
    drawLine: Boolean = true,
    fillArea: Boolean = false,
    showBubble: Boolean = false,
    curveSmothing: Boolean = false,
    fillAreaToMinimum: Boolean = false,
) :
    LineGraph(data, drawLine, fillArea, showBubble, curveSmothing, fillAreaToMinimum) {

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
