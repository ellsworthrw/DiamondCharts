/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import kotlin.time.Duration.Companion.days

abstract class Chart(override val data: ChartData) : ChartObject() {

    open val showInLegend: Int
        get() = Legend.SERIES

    /** @return true if the vertical axis displays the data values for the chart.
     * override if the horizontal axis is the axis which scales the data and return false
     */
    open val isVertical: Boolean
        get() = true

    /** If set to true cause the graph to create Hotspot areas on the graph.
     * This is required for the method hitTest to work and is required
     * for roll over labels to be displayed.
     * @see .hitTest
     *
     * @see .setLabelType
     */
    var isHotspotsAvailable: Boolean
        get() = hotspots != null
        set(createHotspots) = if (createHotspots)
            hotspots = ArrayList()
        else
            hotspots = null

    internal var vertAxis: Axis? = null
    internal var horAxis: Axis? = null
    internal var hotspots: ArrayList<Hotspot>? = null

    abstract override fun draw(g: GraphicsContext)

    open fun drawLegendSymbol(g: GraphicsContext, x: Int, y: Int, width: Int, height: Int, series: Int, dataPtNum: Int): Boolean {
        val gattr = data.getGraphicAttributes(series)
        g.color = gattr.color
        g.fillRect(x, y, width, height)
        return true
    }

    open fun setBounds(x: Int, y: Int, width: Int, height: Int) {}

    open fun usesAxis(): Boolean {
        return true
    }

    open fun createHorizontalAxis(): Axis {
        return DecimalAxis()
    }

    open fun createVerticalAxis(): Axis {
        return DecimalAxis()
    }

    open fun setupData(combineSeries: Boolean = false) {
        data.recalc(combineSeries)
        log.d { "setupData(combine=$combineSeries) $data" }
    }

    open fun setupAxis() {
        log.d { "setupAxis: isVertical=$isVertical" }
        val dataCount = data.dataCount
        vertAxis?.apply {
            if (this is LabelAxis) {
                labels = createLabels(dataCount)
                setDataCount(dataCount)
            }
        }
        horAxis?.apply {
            if (this is LabelAxis) {
                labels = createLabels(dataCount)
                setDataCount(dataCount)
            } else if (this is DateAxis) {
                log.d { "   hor data duration ${(data.maxValue2 - data.minValue2).days}" }
            }
        }
        log.d { "   hor: $horAxis" }
        log.d { "   vert: $vertAxis" }
    }

    private fun createLabels(dataCount: Int): ArrayList<Any?> {
        val labels = ArrayList<Any?>(dataCount)
        for (i in 0 until dataCount) {
            labels.add(data.getDataLabel(i))
        }
        return labels
    }

    override fun hitTest(x: Int, y: Int): Hotspot? {
        val num = if (hotspots == null) 0 else hotspots!!.size
        for (i in 0 until num) {
            val h = hotspots!![i]
            if (h.shape.contains(x, y)) {
                return h
            }
        }
        return null
    }

    protected open fun toStringParam(): String {
        return "data=$data,vert=$vertAxis,hor=$horAxis"
    }

    companion object {
        private val log = moduleLogging()
    }
}
