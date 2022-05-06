/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import kotlin.math.ceil
import kotlin.math.floor

class GridLines {

    private var vertAxis: Axis? = null
    private var horAxis: Axis? = null
    var background = Color.none

    /** Show horizontal strips that fills the region between 2 consecutive major tick marks
     * on the vertical axis. The colors alternate between background color and a shade
     * lighter than the major horizontal lines color.
     */
    var isHorizontalStripesShowing = false

    //--------------------------------------------------------------------------
    //                            vertical lines
    //--------------------------------------------------------------------------

    /** The attributes associated with drawing the vertical lines that
     * line up with the major tick marks on the horizontal axis.
     */
    val majorVerticalLines = LineAttributes()

    /** The attributes associated with drawing the vertical lines that
     * line up with the minor tick marks on the horizontal axis.
     */
    val minorVerticalLines = LineAttributes(color = Color.gray20)

    /** The attributes associated with drawing the vertical line that
     * is at the left of the graph.
     */
    val leftLine = LineAttributes(visible = false)

    /** The attributes associated with drawing the vertical line that
     * is at the right of the graph.
     */
    val rightLine = LineAttributes(visible = false)

    //--------------------------------------------------------------------------
    //                          horizontal lines
    //--------------------------------------------------------------------------

    /**
     * Draw horizontal gridlines to the edge of the chart's container space including the Charts.rightMargin amount.
     */
    var extendHorizontalLinesIntoMargin = false

    /** The attributes associated with drawing the horizontal lines that
     * line up with the major tick marks on the vertical axis.
     */
    val majorHorizontalLines = LineAttributes()

    /** The attributes associated with drawing the horizontal lines that
     * line up with the minor tick marks on the vertical axis.
     */
    val minorHorizontalLines = LineAttributes(color = Color.gray20)

    /** The attributes associated with drawing the horizontal line that
     * is at the top of the graph.
     */
    val topLine = LineAttributes(visible = false)

    /** The attributes associated with drawing the horizontal line that
     * is at the bottom of the graph.
     */
    val bottomLine = LineAttributes(visible = false)

    init {
//        majorVerticalLines.style = StrokeStyle.DOT
//        majorHorizontalLines.style = StrokeStyle.DASH_DOT

        minorVerticalLines.visible = false
        minorHorizontalLines.visible = false
//         minorHorizontalLines.style = StrokeStyle.DASH_DOT_DOT
    }

    internal fun setVerticalAxis(vert: Axis?) {
        vertAxis = vert
    }

    internal fun setHorizontalAxis(hor: Axis?) {
        horAxis = hor
    }

    fun draw(g: GraphicsContext, left: Int, bottom: Int, width: Int, height: Int, rightMargin: Int) {
        if (background >= 0) {
            g.color = background
            g.fillRect(left + 1, bottom - height + 1, width, height - 1)
        }
        val origStroke = g.stroke
        val vertAxis = this.vertAxis
        val horAxis = this.horAxis
        if (vertAxis == null || horAxis == null)
            return

        val zeroVertAxis = vertAxis.convertToPixel(0.0)
        val zeroHorizAxis = horAxis.convertToPixel(0.0)
        val right = if (extendHorizontalLinesIntoMargin) left + width + rightMargin else left + width

        // draw horizontal axis line
        if (vertAxis.minValue < 0 && vertAxis.maxValue > 0) {
            drawLine(g, horAxis.axisLineAttributes, left, zeroVertAxis, right, zeroVertAxis)
        } else if (horAxis.axisLineAttributes.visible) {
            drawLine(g, horAxis.axisLineAttributes, left, bottom, right, bottom)
        }

        // draw vertical axis line
        if (horAxis.minValue < 0 && horAxis.maxValue > 0) {
            drawLine(g, vertAxis.axisLineAttributes, zeroHorizAxis, bottom, zeroHorizAxis, bottom - height)
        } else if (vertAxis.axisLineAttributes.visible) {
            drawLine(g, vertAxis.axisLineAttributes, left, bottom, left, bottom - height)
        }

        // horizontal grid lines
        if (majorHorizontalLines.visible || isHorizontalStripesShowing || minorHorizontalLines.visible) {
            val maxVal = vertAxis.maxValue
            val roundoff = Math.min(maxVal * .0001, .000001)
            var majorTickInc = vertAxis.majorTickInc
            var stripeDrawn = false
            val stripeColor = Color.brighter(majorHorizontalLines.color)
            var tickPos = vertAxis.minValue
            while (tickPos <= maxVal) {
                majorTickInc = vertAxis.nextMajorIncVal(tickPos, majorTickInc)
                var y = vertAxis.convertToPixel(tickPos)

                if (isHorizontalStripesShowing && !stripeDrawn) {
                    stripeDrawn = true
                    g.color = stripeColor
                    var hval = tickPos + majorTickInc
                    if (hval > maxVal)
                        hval = maxVal
                    val h = y - vertAxis.convertToPixel(hval)
                    g.fillRect(left + 1, y - h, width, h)
                } else {
                    stripeDrawn = false
                }
                if (tickPos != vertAxis.minValue && y != zeroVertAxis) {     // axis is already drawn
                    if (majorHorizontalLines.visible)
                        drawLine(g, majorHorizontalLines, left, y, right, y)
                    else                    // paint as a minor line if it is visible
                        drawLine(g, minorHorizontalLines, left, y, right, y)
                }
                if (minorHorizontalLines.visible) {
                    g.color = minorHorizontalLines.color
                    g.setStroke(minorHorizontalLines.width, minorHorizontalLines.style)
                    val minorTickNum = vertAxis.minorTickIncNum
                    var minorTickInc = vertAxis.nextMinorIncVal(tickPos, vertAxis.majorTickInc / minorTickNum)
                    var minorTickPos = tickPos + minorTickInc
                    val nextMajorTickPos = Math.min(tickPos + majorTickInc, maxVal) - roundoff
                    while (minorTickPos < nextMajorTickPos) {
                        minorTickInc = vertAxis.nextMinorIncVal(minorTickPos, minorTickInc)
                        y = vertAxis.convertToPixel(minorTickPos)
                        g.drawLine(left, y, right, y)
                        minorTickPos += minorTickInc
                    }
                }
                tickPos += majorTickInc
            }
        }

        // vertical grid lines
        if (majorVerticalLines.visible || minorVerticalLines.visible) {
            val top = bottom - height
            val maxVal = horAxis.maxValue
            val minDistanceToAxis = g.dpToPixel(minDistanceToNextLineDp)
            val roundoff = Math.min(maxVal * .0001, .000001)
            var majorTickInc = horAxis.majorTickInc
            var tickPos = horAxis.minValue
            if (horAxis.startAtMinValue) {
                // make tickPos be an exact multiple of majorTickInc just larger than minVal
                val minorTickInc = horAxis.majorTickInc / horAxis.minorTickIncNum
                tickPos = ceil(horAxis.minValue / majorTickInc) * majorTickInc
                if ((tickPos - horAxis.minValue) > minorTickInc) {
                    // have space to draw minor ticks before first major tick is drown
                    val majorTickPosBeforeOrigin = floor(horAxis.minValue / majorTickInc) * majorTickInc
                    drawMinorLines(majorTickPosBeforeOrigin, tickPos, horAxis.minValue, horAxis.maxValue, top, bottom, g)
                }
            }

            while (tickPos <= maxVal) {
                majorTickInc = horAxis.nextMajorIncVal(tickPos, majorTickInc)
                val x = horAxis.convertToPixel(tickPos)
                if (tickPos != horAxis.minValue && x != zeroHorizAxis) {     // axis is already drawn
                    if ((x - horAxis.x) > minDistanceToAxis) {
                        if (majorVerticalLines.visible)
                            drawLine(g, majorVerticalLines, x, bottom, x, top)
                        else                    // paint as a minor line if it is visible
                            drawLine(g, minorVerticalLines, x, bottom, x, top)
                    }
                }
                drawMinorLines(tickPos, tickPos + majorTickInc - roundoff, tickPos, maxVal, top, bottom, g)

                tickPos += majorTickInc
            }
        }

        // draw border around the graph
        drawLine(g, leftLine, left, bottom, left, bottom - height)
        drawLine(g, rightLine, left + width, bottom, left + width, bottom - height)
        drawLine(g, topLine, left, bottom - height, left + width, bottom - height)
        drawLine(g, bottomLine, left, bottom, left + width, bottom)

        g.stroke = origStroke
    }

    private fun drawMinorLines(
        startPos: Double,
        endPos: Double,
        minVal: Double,
        maxVal: Double,
        top: Int,
        bottom: Int,
        g: GraphicsContext,
    ) {
        val horAxis = this.horAxis
        if (minorVerticalLines.visible && horAxis != null) {
            g.color = minorVerticalLines.color
            g.setStroke(minorVerticalLines.width, minorVerticalLines.style)
            val minDistanceToAxis = g.dpToPixel(minDistanceToNextLineDp)
            var minorTickInc = horAxis.nextMinorIncVal(startPos, horAxis.majorTickInc / horAxis.minorTickIncNum)
            var minorTickPos = startPos + minorTickInc
            while (minorTickPos < endPos && minorTickPos <= maxVal) {
                minorTickInc = horAxis.nextMinorIncVal(minorTickPos, minorTickInc)
                if (minorTickPos >= minVal) {
                    val x = horAxis.convertToPixel(minorTickPos)
                    if ((x - horAxis.x) > minDistanceToAxis)
                        g.drawLine(x, bottom, x, top)
                }
                minorTickPos += minorTickInc
            }
        }

    }

    companion object {
        var minDistanceToNextLineDp = 12f

        fun drawLine(g: GraphicsContext, attr: LineAttributes, x1: Int, y1: Int, x2: Int, y2: Int) {
            if (attr.visible) {
                g.color = attr.color
                g.setStroke(attr.width, attr.style)
                g.drawLine(x1, y1, x2, y2)
            }
        }
    }
}
