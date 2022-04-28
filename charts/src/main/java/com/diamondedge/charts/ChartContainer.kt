/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

abstract class ChartContainer {

    val objectCount: Int
        get() = charts.size

    internal val firstWithAxis: Chart?
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
    // setLegendAutoPlacement

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

    abstract val size: Dimension

    val chartBounds = Rectangle()

    operator fun get(objIndex: Int): ChartObject {
        return charts[objIndex]
    }

    fun add(obj: ChartObject): ChartObject {
        charts.add(obj)
        if (obj is Chart)
            setupGraph(obj)

        //todo remove
        if (obj is Chart) {
            //            setupHotspots();
            obj.isHotspotsAvailable = true
        }
        return obj
    }

    fun remove(obj: ChartObject) {
        charts.remove(obj)
        if (obj is Chart) {
            releaseVertAxis(obj.verticalAxis)
            if (vertAxis === obj.verticalAxis)
                vertAxis = null
            if (horizontalAxis === obj.horizontalAxis)
                horizontalAxis = null
            if (horizontalAxis == null || vertAxis == null)
                setupGraph(firstWithAxis)
        }
    }

    fun remove(objIndex: Int) {
        remove(charts[objIndex])
    }

    internal open fun releaseVertAxis(axis: Axis?) {}

    private fun setupGraph(obj: Chart?) {
        var obj = obj
        if (obj != null && obj.usesAxis()) {
            var vert: Axis?
            var hor: Axis?

            vert = obj.verticalAxis
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
                obj.verticalAxis = vert
            }

            hor = obj.horizontalAxis
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
                obj.horizontalAxis = hor
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
                setupGraph(obj)
            }
        }
    }

    open fun recalcAxis() {}

    fun draw(g: GraphicsContext, afterCalculations: ((GraphicsContext) -> Unit)? = null) {
        val nGraphs = charts.size
        if (nGraphs == 0)
            return

        //todo: add an event so CalculatedData can get updated when dependent data changes
        // then remove the following
        for (i in 0 until nGraphs) {
            val data = charts[i].data
            if (data is CalculatedData)
                data.recalc()
        }

        horizontalAxis?.isVertical = false

        recalcAxis()

        val d = size
        val vertAxisWidth = vertAxis?.getThickness(g) ?: 0
        val vertAxis2Width = 0 //vertAxis2?.getThickness(g) ?: 0
        val horAxisHeight = horizontalAxis?.getThickness(g) ?: 0
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

        gridLines.draw(g, chartBounds.x, chartBounds.bottom, chartBounds.width, chartBounds.height)

        for (obj in charts) {
            if (obj is Chart) {
                if (vertAxis == null && horizontalAxis == null)
                    obj.setBounds(chartBounds.x, chartBounds.y, chartBounds.width, chartBounds.height)
            }
            obj.draw(g)
        }

        if (rolloverSpot != null) {
            rolloverLabel!!.draw(g)
        }

        legend?.draw(g)

    }

    open fun addVerticalAxis(axis: Axis) {}

    /*
     public void mouseMoved(MouseEvent e) {
        // this generates a lot of repaints
        Point p = e.getPoint();
        int nGraphs = graphs.size();
        for (int i = 0; i < nGraphs; i++) {
            GraphObject obj = (GraphObject) graphs.get(i);
            Hotspot h = obj.hitTest(p.x, p.y);
            if (h != null) {
                if (h.graph.getLabelType() != Graph.LABEL_ROLL_OVER)
                    continue;   // skip graphs that are using rollovers

                if (!h.equals(rolloverSpot)) {
                    int x0 = 0, y0 = 0, w0 = 0, h0 = 0;
                    if (rolloverLabel == null) {
                        rolloverLabel = new Annotation();
                    } else  // calc bounding box of last label
                    {
                        x0 = rolloverLabel.getX();
                        y0 = rolloverLabel.getY();
                        w0 = rolloverLabel.getWidth();
                        h0 = rolloverLabel.getHeight();
                    }
                    double val = h.data.getDouble(h.series, h.dataPtNum);
                    Rectangle r = h.shape.getBounds();
                    rolloverLabel.setX(r.x);
                    rolloverLabel.setY(r.y - rolloverLabel.getHeight() - 1);
                    rolloverLabel.setText(val + " series:" + h.series + " datapt:" + h.dataPtNum);

                    // calculate repaint region which is a union of bounding box of the last label
                    // and the new one
                    int x = rolloverLabel.getX();
                    int y = rolloverLabel.getY();
                    int width = rolloverLabel.getWidth();
                    int height = rolloverLabel.getHeight();
                    if (rolloverSpot != null) {
                        int x1 = Math.min(x, x0);
                        int x2 = Math.max(x + width, x0 + w0);
                        int y1 = Math.min(y, y0);
                        int y2 = Math.max(y + height, y0 + h0);
                        x = x1;
                        y = y1;
                        width = x2 - x1;
                        height = y2 - y1;
                    }
                    repaint(x, y, width + 1, height + 1);

                    rolloverSpot = h;
                }
                return;
            }
        }
        // not over any hotspot
        if (rolloverSpot != null && rolloverLabel != null) {
            // repaint area of last label
            repaint(rolloverLabel.getX(), rolloverLabel.getY(), rolloverLabel.getWidth() + 1, rolloverLabel.getHeight() + 1);
        }
        rolloverSpot = null;
    }
    */

    private fun repaint(x: Int, y: Int, w: Int, h: Int) {}

    companion object {
        private val log = moduleLogging()

        val LEGEND_NONE = 0
        val LEGEND_RIGHT = 1
        val LEGEND_BOTTOM = 2
        val LEGEND_MANUAL = 3
    }
}
