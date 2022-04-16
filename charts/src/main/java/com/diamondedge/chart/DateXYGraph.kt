/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.chart

class DateXYGraph(data: ChartData, drawLine: Boolean = true, fillArea: Boolean = false, showBubble: Boolean = false) :
    XYGraph(data, drawLine, fillArea, showBubble) {

    override fun createHorizontalAxis(): Axis {
        return DateAxis()
    }

    override fun toString(): String {
        return "DateGraph[" + toStringParam() + "]"
    }
}
