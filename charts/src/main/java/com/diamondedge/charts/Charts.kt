/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

open class Charts(width: Float, height: Float, legendPosition: Int = LEGEND_NONE) {

    internal val objectCount: Int
        get() = charts.size

    private val firstWithAxis: Chart?
        get() {
            val nGraphs = charts.size
            for (i in 0 until nGraphs) {
                val obj = charts[i]
                if (obj is Chart && obj.usesAxis())
                    return obj
            }
            return null
        }

    var backgroundColor = Color.defaultBackgroundColor
    var legend: Legend? = null

    var legendPosition: Int = 0
    var vertAxis: Axis? = null
    var horizontalAxis: Axis? = null
    var charts = ArrayList<ChartObject>()
    var gridLines: GridLines = GridLines()
    private val rolloverLabel: Annotation? = null
    private val rolloverSpot: Hotspot? = null

    var leftMargin = 5f
    var bottomMargin = 5f
    var topMargin = 5f
    var rightMargin = 5f

    val size = Dimension(width.toInt(), height.toInt())

    val chartBounds = Rectangle()

    private val firstVerticalAxis: Axis?
        get() {
            return if (vertAxisList.isEmpty()) null else vertAxisList[0]
        }

    private val vertAxisList = ArrayList<Axis>(1)

    init {
        this.legendPosition = legendPosition
    }

    operator fun get(objIndex: Int): ChartObject {
        return charts[objIndex]
    }

    fun add(obj: ChartObject): ChartObject {
        charts.add(obj)
        if (obj is Chart) {
            obj.setupData()
            installAxes(obj)
            obj.setupAxis()
        }
        //todo remove
        if (obj is Chart) {
            //            setupHotspots();
            obj.isHotspotsAvailable = true
        }
        return obj
    }

    private fun installAxes(chart: Chart?) {
        var obj = chart
        if (obj != null && obj.usesAxis()) {
            var vert: Axis?
            var hor: Axis?

            vert = obj.vertAxis
            if (vert == null) {
                if (vertAxis == null) {
                    vert = obj.createVerticalAxis()
                    if (vert is LabelAxis) {
                        vert.isMinorTickShowing = false
                        gridLines.minorHorizontalLines.visible = false
                    }
                } else {
                    vert = vertAxis
                }
                obj.vertAxis = vert
            }

            hor = obj.horAxis
            if (hor == null) {
                if (horizontalAxis == null) {
                    hor = obj.createHorizontalAxis()
                    if (hor is LabelAxis) {
                        hor.isMinorTickShowing = false
                        gridLines.minorVerticalLines.visible = false
                    }
                } else {
                    hor = horizontalAxis
                }
                obj.horAxis = hor
            }

            // if obj has both axiis then make it the main graph object
            if (vert != null && hor != null) {
                addVerticalAxis(vert)
                horizontalAxis = hor
                gridLines.setVerticalAxis(vert)
                gridLines.setHorizontalAxis(hor)
            }
        } else {
            obj = firstWithAxis
            if (obj == null) {
                horizontalAxis = null
                vertAxis = null
                gridLines.setVerticalAxis(null)
                gridLines.setHorizontalAxis(null)
            } else {
                installAxes(obj)
            }
        }
    }

    open fun addVerticalAxis(axis: Axis) {
        vertAxisList.add(axis)
        vertAxis = firstVerticalAxis
    }

    fun recalcAxis() {
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

                        if (graph.vertAxis === axis) {
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

    fun draw(g: GraphicsContext, afterCalculations: ((GraphicsContext) -> Unit)? = null) {
        val nGraphs = charts.size
        if (nGraphs == 0)
            return

        for (chart in charts) {
            //todo: add an event so CalculatedData can get updated when dependent data changes
            // then remove the following
            val data = chart.data
            if (data is CalculatedData)
                data.recalc(false)
        }

        horizontalAxis?.isVertical = false

        recalcAxis()

        horizontalAxis?.majorTickFont?.let { g.font = it }
        val d = size
        val vertAxisWidth = vertAxis?.getThickness(g) ?: 0
        val vertAxis2Width = 0 //vertAxis2?.getThickness(g) ?: 0
        val horAxisHeight = horizontalAxis?.getThickness(g)?.plus(horizontalAxis?.extraHorLabelHeight(g, size.width) ?: 0) ?: 0
        val leftMargin = g.dpToPixel(this.leftMargin)
        val bottomMargin = g.dpToPixel(this.bottomMargin)
        val topMargin = g.dpToPixel(this.topMargin)
        val rightMargin = g.dpToPixel(this.rightMargin)
        val chartTitleHeight = 0

        chartBounds.width = d.width - leftMargin - rightMargin - vertAxisWidth - vertAxis2Width
        chartBounds.height = d.height - topMargin - bottomMargin - horAxisHeight - chartTitleHeight
        log.d { "chartWidth: ${chartBounds.width} contWidth: ${d.width} leftMargin: $leftMargin rightMargin: $rightMargin vertAxisWidth: $vertAxisWidth vertAxis2Width: $vertAxis2Width" }
        if (legendPosition != LEGEND_NONE) {
            if (legend == null)
                legend = Legend()
            legend!!.setGraph(this)
            val legendSize = legend!!.getSize(g)
            if (legendPosition == LEGEND_RIGHT)
                chartBounds.width -= legendSize!!.width + 5
            else if (legendPosition == LEGEND_BOTTOM) {
                legend!!.orientation = Legend.HORIZONTAL
                chartBounds.height -= legendSize!!.height + 5
            }
        }
        chartBounds.x = leftMargin + vertAxisWidth
        chartBounds.y = topMargin + chartTitleHeight
        // draw axis first since it sets up scaling for charts
        vertAxis?.apply {
            setBounds(chartBounds.x, chartBounds.bottom, vertAxisWidth, chartBounds.height)
            calcMetrics(height, g, majorTickFont)
        }
        horizontalAxis?.apply {
            setBounds(chartBounds.x, chartBounds.bottom, chartBounds.width, getThickness(g))
            calcMetrics(width, g, majorTickFont)
            if (minValue < 0) {
                vertAxis?.adjustOriginPosition(convertToPixel(0.0))
            }
        }
        vertAxis?.apply {
            if (minValue < 0) {
                horizontalAxis?.adjustOriginPosition(convertToPixel(0.0))
            }
        }
        log.d { "vert $vertAxis" }
        log.d { "hor $horizontalAxis" }

        legend?.let { legend ->
            val legendSize = legend.getSize(g)
            //log.d {  "legend " + legendSize  };

            if (legendPosition == LEGEND_RIGHT) {
                var y = chartBounds.y + (chartBounds.height - legendSize!!.height) / 2
                if (legendSize.height > chartBounds.height)
                    y = chartBounds.y
                legend.setLocation(chartBounds.x + chartBounds.width + 5, y)
            } else if (legendPosition == LEGEND_BOTTOM)
                legend.setLocation(chartBounds.x + (chartBounds.width - legendSize!!.width) / 2, chartBounds.bottom + horAxisHeight + 5)
        }

        afterCalculations?.invoke(g)

        if (backgroundColor >= 0) {
            g.color = backgroundColor
            g.fillRect(0, 0, d.width, d.height)
        }

        //todo for each chartobj check needAxis()
        // if any does then draw axis

        // draw axis first since it sets up scaling for charts
        vertAxis?.draw(g)
        horizontalAxis?.draw(g)

        gridLines.draw(g, chartBounds.x, chartBounds.bottom, chartBounds.width, chartBounds.height, rightMargin)

        for (obj in charts) {
            if (obj is Chart) {
                if (vertAxis == null && horizontalAxis == null)
                    obj.setBounds(chartBounds.x, chartBounds.y, chartBounds.width, chartBounds.height)
            }
            obj.draw(g)
        }

        rolloverLabel?.draw(g)
        legend?.draw(g)
    }

    companion object {
        private val log = moduleLogging()

        val LEGEND_NONE = 0
        val LEGEND_RIGHT = 1
        val LEGEND_BOTTOM = 2
        val LEGEND_MANUAL = 3
    }
}
