/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

class StockChart(data: ChartData) : Chart(data) {
    private var drawLine = false
    private var drawCandle = true
    private var drawHighLow = true
    private var drawOpenClose = false
    private val symbolWidth = 8
    private val upColor = Color.green
    private val downColor = Color.red
    private val upCandleFill = Color.white
    private val downCandleFill = Color.red
    private val upCandleColor = upColor
    private val downCandleColor = downColor

    var style: Int
        get() = if (drawCandle)
            CANDLE_STICK
        else if (drawOpenClose)
            HIGH_LOW_OPEN_CLOSE
        else if (drawHighLow)
            HIGH_LOW
        else
            LINE
        set(style) {
            drawCandle = false
            drawOpenClose = false
            drawHighLow = true
            when (style) {
                HIGH_LOW_OPEN_CLOSE -> drawOpenClose = true
                CANDLE_STICK -> drawCandle = true
                LINE -> {
                    drawLine = true
                    drawHighLow = false
                }
            }
        }

    init {
        if (data.valueCount != 5) {
            println("StockChart can only accept ChartData with 5 values (date,high,low,open,close")
        }
    }

    override fun createHorizontalAxis(): Axis {
        return DateAxis()
    }

    override fun draw(g: GraphicsContext) {
        val dsCount = data.seriesCount
        val dataCount = data.dataCount
        println("StockChart.draw: $dataCount")
        var gattr: GraphicAttributes
        var x: Int
        var lastX = 0
        var lastY = 0
        var high = 0
        var low = 0
        var open = 0
        var close = 0
        var goingUp = false
        // make local variables so when they are adjusted they don't change the user settings
        var drawLine = this.drawLine
        var drawCandle = this.drawCandle
        var drawHighLow = this.drawHighLow
        var drawOpenClose = this.drawOpenClose
        var upColor = this.upColor
        var upCandleFill = this.upCandleFill
        var upCandleColor = this.upCandleColor
        var downColor = this.downColor
        var downCandleFill = this.downCandleFill
        var downCandleColor = this.downCandleColor
        if (hotspots != null)
            hotspots!!.clear()

        var width = symbolWidth
        if (dataCount > 1)
        // check to see if there is room to display requested style
        {                     // if not switch to a style that takes less space
            var w = horAxis!!.convertToPixel(data.getDouble(0, 1, ChartData.xIndex)) - horAxis!!.convertToPixel(
                data.getDouble(
                    0,
                    0,
                    ChartData.xIndex
                )
            )
            w -= 2
            if (drawCandle)
            // want min of 2 pixels in between candles
                w -= 2
            if (width > w) {
                if (w < 4) {
                    if (drawCandle) {
                        drawOpenClose = true
                        w += 2       // 1 pixel in between open_close bar ok
                    }
                    drawCandle = false
                }
                if (w < 3)
                    drawOpenClose = false
                if (w < 2) {
                    drawHighLow = false
                    drawLine = true
                }
                width = w
            }
        }
        val width2 = width / 2
        width = 2 * width2   // make the candle symmetric

        for (series in 0 until dsCount) {
            gattr = data.getGraphicAttributes(series)

            if (!drawLine && dsCount > 1)
            // for multiple sets of data when line is not drawn
            {                               // draw with the color of the data series
                upColor = gattr.color
                upCandleColor = gattr.color
                upCandleFill = Color.white
                downColor = gattr.color
                downCandleColor = gattr.color
                downCandleFill = gattr.color
            }

            for (i in 0 until dataCount) {
                x = horAxis!!.convertToPixel(data.getDouble(series, i, ChartData.xIndex))
                high = vertAxis!!.convertToPixel(data.getDouble(series, i, ChartData.highIndex))
                low = vertAxis!!.convertToPixel(data.getDouble(series, i, ChartData.lowIndex))
                open = vertAxis!!.convertToPixel(data.getDouble(series, i, ChartData.openIndex))
                close = vertAxis!!.convertToPixel(data.getDouble(series, i, ChartData.closeIndex))

                g.color = gattr.color
                if (i > 0 && drawLine)
                // draw line from last point to this one
                {
                    if (drawCandle)
                        g.drawLine(lastX + width / 2, lastY, x - width2, close)
                    else
                        g.drawLine(lastX, lastY, x, close)
                }
                goingUp = close < open
                if (goingUp) {
                    g.color = upColor
                } else
                    g.color = downColor

                if (drawHighLow)
                    g.drawLine(x, high, x, low)

                if (drawCandle) {
                    if (goingUp) {
                        g.color = upCandleFill
                        g.fillRect(x - width2, close, width, open - close)
                        g.color = upCandleColor
                        g.drawRect(x - width2, close, width, open - close)
                    } else {
                        g.color = downCandleFill
                        g.fillRect(x - width2, open, width, close - open)
                        g.color = downCandleColor
                        g.drawRect(x - width2, open, width, close - open)
                    }
                } else if (drawOpenClose) {
                    g.drawLine(x, open, x - width2, open)
                    g.drawLine(x, close, x + width2, close)
                }

                if (hotspots != null) {
                    val w = if (drawCandle || drawOpenClose) width else 3
                    val rect = Rectangle(x - w / 2, Math.min(high, low), w, Math.abs(high - low))
                    hotspots!!.add(Hotspot(this, data, series, i, rect))
                }

                lastX = x
                lastY = close
            }
        }
    }

    override fun toStringParam(): String {
        return "style=" + style + "," + super.toStringParam()
    }

    override fun toString(): String {
        return "StockChart[" + toStringParam() + "]"
    }

    companion object {
        val LINE = 0
        val HIGH_LOW = 1
        val HIGH_LOW_OPEN_CLOSE = 2
        val CANDLE_STICK = 3

        fun createHighLowStockChart(data: ChartData): StockChart {
            val graph = StockChart(data)
            graph.drawCandle = false
            graph.drawHighLow = true
            graph.drawOpenClose = false
            return graph
        }

        fun createHighLowOpenCloseStockChart(data: ChartData): StockChart {
            val graph = StockChart(data)
            graph.drawCandle = false
            graph.drawHighLow = true
            graph.drawOpenClose = true
            return graph
        }

        fun createCandleStickStockChart(data: ChartData): StockChart {
            val graph = StockChart(data)
            graph.drawCandle = true
            graph.drawHighLow = true
            graph.drawOpenClose = false
            return graph
        }
    }
}
