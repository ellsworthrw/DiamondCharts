/**
 * Copyright 2004-2026 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

internal class Hotspot(
    var graph: Chart, /* Returns the data set associated with this hotspot */
    var graphData: ChartData, /* Returns the series number associated with this hotspot */
    var series: Int, /* Returns the data point number associated with this hotspot */
    var dataPointNumber: Int, /* Returns the shape for which this hotspot is active. */
    var shape: Rectangle
) {

    override fun equals(other: Any?): Boolean {
        if (other is Hotspot) {
            if (other.graph === graph && other.graphData === graphData && other.series == series && other.dataPointNumber == dataPointNumber)
                return true
        }
        return false
    }
}
