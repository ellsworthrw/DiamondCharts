/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

/**
 * `LineGraph` provides functionality to display data as a line graph.
 * The horizontal axis is a label axis.
 *
 * @param data The [ChartData] object containing the data to be displayed.
 * @param drawLine Determines whether to draw the line connecting data points (default is true).
 * @param fillArea Determines whether to fill the area under the line (default is false).
 * @param showBubble Determines whether to display bubbles at data points (default is false).
 * @param curveSmothing Determines whether to apply curve smoothing to the line (default is false).
 * @param fillAreaToMinimum Determines whether to fill the area between the line and the minimum y value (default is false).
 */
open class LineGraph(
    data: ChartData,
    val drawLine: Boolean = true,
    val fillArea: Boolean = false,
    val showBubble: Boolean = false,
    val curveSmothing: Boolean = false,
    val fillAreaToMinimum: Boolean = false,
) :
    Chart(data) {

    /** The size that the symbol on each data point is drawn.
     */
    var symbolSize = 4f
    private val isBubble3D = true

    var lineWidth = 2f

    override fun createHorizontalAxis(): Axis {
        return LabelAxis()
    }

    open fun getX(series: Int, dataPt: Int): Double {
        return dataPt.toDouble()
    }

    open fun getY(series: Int, dataPt: Int): Double {
        return data.getValue(series, dataPt)
    }

    override fun draw(g: GraphicsContext) {
        val dsCount = data.seriesCount
        val dataCount = data.dataCount
        var bubbleIndex = -1
        val xPts = IntArray(dataCount + 2)
        val yPts = IntArray(dataCount + 2)
        if (data.valueCount == 3)
            bubbleIndex = ChartData.zIndex
        var gattr: GraphicAttributes
        var x: Int
        var y: Int
        var lastX = 0
        // center the bars in the area
        val offset = 0 //unitWidth / 2;
        hotspots?.clear()

        val origStroke = g.stroke
        val stroke = g.createStroke(lineWidth, curveSmoothing = curveSmothing, cornerRadius = cornerRadius)
        val symbolStroke = g.createStroke(0.5f)
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
                xPts[i + 1] = x
                yPts[i + 1] = y
                var ptSize = g.dpToPixel(symbolSize)
                if (bubbleIndex >= 0 && showBubble) {
                    val bubble = vertAxis!!.scaleData(data.getValue(series, i, bubbleIndex))

                    if (isBubble3D) {
                        g.applyGradient(
                            Gradient.create(
                                listOf(gattr.color, gattr.color.brighter),
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
            }
            if (drawLine) {
                g.stroke = stroke
                g.drawPolyline(xPts, yPts, startIndex = 1, xPts.size - 2)
            }
            if (fillArea) {
                val y0 = horAxis!!.yOrigin
                xPts[0] = xPts[1]
                yPts[0] = y0
                xPts[dataCount + 1] = lastX
                yPts[dataCount + 1] = y0
                if (gradient == null)
                    g.color = gattr.color.transparent
                else
                    g.applyGradient(gradient, 0.2f)
                g.fillPolygon(xPts, yPts, dataCount + 2)
            } else if (fillAreaToMinimum) {
                val yMin = vertAxis!!.convertToPixel(data.minValue)
                xPts[0] = xPts[1]
                yPts[0] = yMin
                xPts[dataCount + 1] = lastX
                yPts[dataCount + 1] = yMin
                if (gradient == null) {
                    val yMax = vertAxis!!.convertToPixel(data.maxValue)
                    val gradBounds = RectangleF(xPts[0].toFloat(), yMax.toFloat(), (lastX - xPts[0]).toFloat(), (yMin - yMax).toFloat())
                    val grad = Gradient(listOf(0f to gattr.color, 1f to (gattr.color and 0xffffff)), gradBounds, GradientType.TopToBottom)
                    g.applyGradient(grad, 0.3f)
                } else {
                    g.applyGradient(gradient, 0.2f)
                }
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

    companion object {
        /**
         * Corner radius for [PathEffect.cornerPathEffect] used when curveSmothing is turned on
         */
        var cornerRadius = 1f
    }
}
