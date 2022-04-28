/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

open class LineGraph(data: ChartData, val drawLine: Boolean = true, val fillArea: Boolean = false, val showBubble: Boolean = false) :
    Chart(data) {

    /** The size that the symbol on each data point is drawn.
     */
    var symbolSize = 4f
    private val isBubble3D = true

    override fun createHorizontalAxis(): Axis {
        return LabelAxis()
    }

    open fun getX(series: Int, dataPt: Int): Double {
        return when (data.valueCount) {
            1 -> dataPt.toDouble()   // use the dataPtNum as the value on the x axis when only one value is supplied in the dataset
            5 -> data.getDouble(series, dataPt, ChartData.dateIndex)
            else -> data.getDouble(series, dataPt, ChartData.xIndex)
        }
    }

    open fun getY(series: Int, dataPt: Int): Double {
        return when (data.valueCount) {
            5 -> data.getDouble(series, dataPt, ChartData.closeIndex)
            else -> data.getDouble(series, dataPt, ChartData.yIndex)
        }
    }

    var lineWidth = 2f

    override fun draw(g: GraphicsContext) {
        val dsCount = data.seriesCount
        val dataCount = data.dataCount
        var bubbleIndex = -1
        var xPts: IntArray? = null
        var yPts: IntArray? = null
        if (data.valueCount == 3)
            bubbleIndex = ChartData.zIndex
        var gattr: GraphicAttributes
        var x: Int
        var y: Int
        var lastX = 0
        var lastY = 0
        // center the bars in the area
        val offset = 0 //unitWidth / 2;
        hotspots?.clear()

        val origStroke = g.stroke
        val stroke = g.createStroke(lineWidth)
        val symbolStroke = g.createStroke(0.5f)
        if (fillArea) {
            xPts = IntArray(dataCount + 2)
            yPts = IntArray(dataCount + 2)
        }
        g.stroke = stroke

        for (series in 0 until dsCount) {
            gattr = data.getGraphicAttributes(series)

            val gradient = gattr.gradient
            if (gradient == null)
                g.color = gattr.color
            else
                g.applyGradient(gradient)

            for (i in 0 until dataCount) {
                x = horAxis!!.convertToPixel(getX(series, i)) + offset
                y = vertAxis!!.convertToPixel(getY(series, i))
                if (i > 0 && drawLine) {
                    g.stroke = stroke
                    g.drawLine(lastX, lastY, x, y)
                }
                if (xPts != null && yPts != null) {
                    xPts[i + 1] = x
                    yPts[i + 1] = y
                }
                var ptSize = g.dpToPixel(symbolSize)
                if (bubbleIndex >= 0 && showBubble) {
                    val bubble = vertAxis!!.scaleData(data.getDouble(series, i, bubbleIndex))

                    if (isBubble3D) {
                        g.applyGradient(
                            Gradient.create(
                                listOf(gattr.color, Color.brighter(gattr.color)),
                                RectangleF(x.toFloat(), y.toFloat(), bubble.toFloat(), bubble.toFloat()),
                                GradientType.Radial
                            )
                        )
                        g.fillOval(x - bubble / 2, y - bubble / 2, bubble, bubble)
                    } else {
                        g.stroke = symbolStroke
                        Draw.drawSymbol(g, x, y, bubble.toFloat(), SymbolType.CIRCLE, gattr.color)
                    }
                    ptSize = bubble
                } else if (gattr.symbol != SymbolType.NONE) {
                    g.stroke = symbolStroke
                    Draw.drawSymbol(g, x, y, symbolSize, gattr.symbol, gattr.color)
                }
                if (hotspots != null) {
                    if (ptSize < 5)
                        ptSize = 5
                    val rect = Rectangle(x - ptSize / 2, y - ptSize / 2, ptSize, ptSize)
                    hotspots!!.add(Hotspot(this, data, series, i, rect))
                }
                lastX = x
                lastY = y
            }
            if (xPts != null && yPts != null) {
                val y0 = horAxis!!.yOrigin
                xPts[0] = xPts[1]
                yPts[0] = y0
                xPts[dataCount + 1] = lastX
                yPts[dataCount + 1] = y0
                if (gradient == null)
                    g.color = Color.transparent(gattr.color)
                else
                    g.applyGradient(gradient, 0.2f)
                g.fillPolygon(xPts, yPts, dataCount + 2)
            }
        }
        g.stroke = origStroke
        g.clearGradient()
    }

    override fun drawLegendSymbol(g: GraphicsContext, x: Int, y: Int, width: Int, height: Int, series: Int, dataPtNum: Int): Boolean {
        val gattr = data.getGraphicAttributes(series)
        if ((gattr.symbol != SymbolType.NONE || drawLine) && symbolSize > 0 && !showBubble) {
            val yc = y + height / 2
            g.color = gattr.color
            if (gattr.symbol == SymbolType.NONE)
                g.drawLine(x + 1, yc, x + width - 2, yc)
            Draw.drawSymbol(g, x + width / 2, yc, 8f, gattr.symbol, gattr.color)
        } else
            super.drawLegendSymbol(g, x, y, width, height, series, dataPtNum)
        return true
    }

    override fun toString(): String {
        return "LineGraph[" + toStringParam() + "]"
    }

}
