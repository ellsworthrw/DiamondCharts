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

    override fun tickLabel(value: Double): String {
        if (majorTickLabelPosition == TickLabelPosition.GroupCenter && value == maxValue)
            return ""    // extra label when centering labels in between ticks
        var label: String? = null
        val labels = labels
        if (labels != null && value < labels.size) {
            val l = labels.get(value.toInt())
            if (l != null)
                label = l.toString()
        }
        if (label == null)
            label = value.toString()
        return label
    }

    fun setDataCount(value: Int) {
        log.d { "==>LabelAxis.setDataCount: $value" }
        dataCount = value
    }

    override fun toString(): String {
        return "LabelAxis[dataCount=" + dataCount + "," + toStringParam() + "]"
    }

    companion object {
        private val log = moduleLogging()
    }
}
