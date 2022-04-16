/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.chart

class Hotspot(
    var graph: Chart, /* Returns the data set associated with this hotspot */
    var graphData: ChartData, /* Returns the series number associated with this hotspot */
    var series: Int, /* Returns the data point number associated with this hotspot */
    var dataPointNumber: Int, /* Returns the shape for which this hotspot is active. */
    var shape: Rectangle
) {

    /* Returns the DataPoint object associated with this hotspot
     */
    val dataPoint: DataPoint? by lazy {
        graphData.getDataPoint(series, dataPointNumber, false)
    }

    override fun equals(o: Any?): Boolean {
        if (o is Hotspot) {
            val h = o as Hotspot?
            if (h!!.graph === graph && h!!.graphData === graphData && h!!.series == series && h.dataPointNumber == dataPointNumber)
                return true
        }
        return false
    }
}
