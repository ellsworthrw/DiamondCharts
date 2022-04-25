/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

open class Charts(width: Float, height: Float, legendPosition: Int = LEGEND_NONE) : ChartContainer() {

    init {
        this.legendPosition = legendPosition
    }

    override val size = Dimension(width.toInt(), height.toInt())

    private val firstVerticalAxis: Axis?
        get() {
            return if (vertAxisList.isEmpty()) null else vertAxisList[0]
        }

    private val vertAxisList = ArrayList<Axis>(1)

    override fun addVerticalAxis(axis: Axis) {
        vertAxisList.add(axis)
        vertAxis = firstVerticalAxis
    }

    override fun releaseVertAxis(axis: Axis?) {
        if (axis == null)
            return
        val nGraphs = charts.size
        for (i in 0 until nGraphs) {
            val obj = charts[i]
            if (obj is Chart && obj.verticalAxis === axis)
                return    // axis still in use
        }
        vertAxisList.remove(axis)
    }

    override fun recalcAxis() {
        //System.out.println( "\nMultiGraph.recalcAxis() " + vertAxisList.size() );
        if (vertAxis is LabelAxis && vertAxis!!.isAutoScaling) {
            // only one chart allowed
            val chart = firstWithAxis
            if (chart != null) {
                val data = chart.data
                vertAxis!!.minValue = data.minValue2
                vertAxis!!.maxValue = data.maxValue2
                if (horizontalAxis != null) {
                    horizontalAxis!!.minValue = data.minValue
                    horizontalAxis!!.maxValue = data.maxValue
                }
            }
        } else {
            var horMin = Double.MAX_VALUE
            var horMax = Double.MIN_VALUE
            var graph: Chart
            val nGraphs = charts.size
            for (axis in vertAxisList) {
                var vertMin = Double.MAX_VALUE
                var vertMax = Double.MIN_VALUE
                if (axis.isAutoScaling || horizontalAxis != null && horizontalAxis!!.isAutoScaling) {
                    for (g in 0 until nGraphs) {
                        graph = charts[g] as? Chart ?: continue

                        if (graph.verticalAxis === axis) {
                            val data = graph.data
                            var min = data.minValue
                            var max = data.maxValue
                            if (min < vertMin)
                                vertMin = min
                            if (max > vertMax)
                                vertMax = max

                            min = data.minValue2
                            max = data.maxValue2
                            if (min < horMin)
                                horMin = min
                            if (max > horMax)
                                horMax = max
                        }
                    }
                }
                //System.out.println( "Graph min: " + vertMin + " max: " + vertMax );

                if (axis.isAutoScaling) {
                    axis.minValue = vertMin
                    axis.maxValue = vertMax
                }
            }
            if (horizontalAxis != null && horizontalAxis!!.isAutoScaling) {
                horizontalAxis!!.minValue = horMin
                horizontalAxis!!.maxValue = horMax
            }
        }
    }
}
