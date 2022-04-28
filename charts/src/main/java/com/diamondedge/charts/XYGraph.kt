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

    override fun toString(): String {
        return "XYGraph[" + toStringParam() + "]"
    }
}
