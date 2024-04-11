/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

class DateXYGraph(
    data: ChartData,
    drawLine: Boolean = true,
    fillArea: Boolean = false,
    showBubble: Boolean = false,
    curveSmothing: Boolean = false
) :
    XYGraph(data, drawLine, fillArea, showBubble, curveSmothing) {

    override fun createHorizontalAxis(): Axis {
        return DateAxis()
    }

    override fun toString(): String {
        return "DateXYGraph[" + toStringParam() + "]"
    }
}
