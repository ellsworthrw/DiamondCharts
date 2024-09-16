/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

/**
 * `DateXYGraph` provides functionality to display data as a line graph.
 * The horizontal axis is a decimal value representing a date (number of days since 1/1/1970).
 *
 * @param data The [ChartData] object containing the data to be displayed.
 * @param drawLine Determines whether to draw the line connecting data points (default is true).
 * @param fillArea Determines whether to fill the area under the line (default is false).
 * @param showBubble Determines whether to display bubbles at data points (default is false).
 * @param curveSmothing Determines whether to apply curve smoothing to the line (default is false).
 * @param fillAreaToMinimum Determines whether to fill the area between the line and the minimum y value (default is false).
 */
class DateXYGraph(
    data: ChartData,
    drawLine: Boolean = true,
    fillArea: Boolean = false,
    showBubble: Boolean = false,
    curveSmothing: Boolean = false,
    fillAreaToMinimum: Boolean = false,
) :
    XYGraph(data, drawLine, fillArea, showBubble, curveSmothing, fillAreaToMinimum) {

    override fun createHorizontalAxis(): Axis {
        return DateAxis()
    }

    override fun toString(): String {
        return "DateXYGraph[" + toStringParam() + "]"
    }
}
