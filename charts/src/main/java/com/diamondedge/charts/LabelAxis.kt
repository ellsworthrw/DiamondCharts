/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

class LabelAxis : Axis() {

    private var dataCount: Int = 0

    internal var labels: ArrayList<Any?>? = null

    override fun calcMetrics(rangePix: Int, g: GraphicsContext, font: Font?) {
        minValue = 0.0
        maxValue = (dataCount - 1).toDouble()
        if (majorTickLabelPosition == TickLabelPosition.GroupCenter)
            maxValue++
        super.calcMetrics(rangePix, g, font)
        majorTickInc = 1.0
    }

    override fun tickLabel(index: Double): String {
        if (majorTickLabelPosition == TickLabelPosition.GroupCenter && index == maxValue)
            return ""    // extra label when centering labels in between ticks
        var label: String? = null
        val labels = labels
        if (labels != null && index < labels.size) {
            val l = labels.get(index.toInt())
            if (l != null)
                label = l.toString()
        }
        if (label == null)
            label = index.toString()
        return label
    }

    fun setDataCount(value: Int) {
        println("==>LabelAxis.setDataCount: $value")
        dataCount = value
    }

    override fun toString(): String {
        return "LabelAxis[dataCount=" + dataCount + "," + toStringParam() + "]"
    }
}
