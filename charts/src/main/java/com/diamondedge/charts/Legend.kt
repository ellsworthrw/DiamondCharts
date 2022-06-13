/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import kotlin.math.ceil

class Legend(private val charts: Charts) {
    var font = Draw.defaultFont

    var orientation = VERTICAL
    var borderStyle = BORDER_SINGLE
    internal var size: Dimension? = null
    internal var width: Int = 0
    internal var height: Int = 0
    private var symbolWidth = 5
    private val symbolGap = 2
    private val margin = 5 // on all sides and in between columns
    private var left = 0
    private var top = 0
    private val rowGap = 3

    private val itemCount: Int
        get() {
            var count = 0
            for (i in 0 until charts.count) {
                val obj = charts[i] as? Chart ?: continue
                val showInLegend = obj.showInLegend
                if (showInLegend != DONT_SHOW) {
                    val dsCount = obj.data.seriesCount
                    val dataCount = obj.data.dataCount
                    if (showInLegend and DATA_PT > 0) {
                        for (series in 0 until dsCount) {
                            for (j in 0 until dataCount) {
                                count++
                            }
                        }
                    } else
                        count += dsCount
                }
            }
            return count
        }

    fun draw(g: GraphicsContext) {
        val count = itemCount
        val rows: Int
        var cols: Int

        cols = 1
        if (orientation == HORIZONTAL) {
            cols = count
            rows = 1
        } else if (orientation == VERTICAL)
            rows = count
        else
            rows = ceil(count.toDouble() / cols).toInt()

        var label: String?
        g.font = font
        val fm = g.fontMetrics
        var strWidth = 0
        var x = left
        var y = 0
        val fontHeight = fm.height
        symbolWidth = fontHeight
        val fontY: Int = fm.baseline
        val rowHeight = fontHeight + fm.leading
        var row = 0
        var col = 0
        var i = 0
        while (i < charts.count) {
            val obj = charts[i]
            if (obj !is Chart) {
                i++
                continue
            }
            val data = obj.data
            if (data.isEmpty()) {
                i++
                continue
            }

            val dsCount = data.seriesCount
            var dataCount = data.dataCount
            var showDataPoint = false
            if (obj.showInLegend and DATA_PT > 0)
                showDataPoint = true
            else
                dataCount = 1  // only showing label for series, so 1 row per series

            for (series in 0 until dsCount) {
                val seriesLabel = data.getSeriesLabel(series)
                label = seriesLabel
                for (j in 0 until dataCount) {
                    if (row == 0) {
                        x += margin
                        strWidth = 0
                        y = top + margin
                    }

                    val showLegendItem: Boolean
                    if (showDataPoint) {
                        label = seriesLabel + " " + data.getDataLabel(j)
                        showLegendItem = obj.drawLegendSymbol(g, x, y, symbolWidth, fontHeight, series, j)
                    } else {
                        showLegendItem = obj.drawLegendSymbol(g, x, y, symbolWidth, fontHeight, series, -1)
                    }
                    if (!showLegendItem) {
                        if (row == 0)
                            x -= margin  // retract the margin added above
                        continue
                    }

                    if (label != null) {
                        val w = g.stringWidth(label)
                        if (w > strWidth)
                            strWidth = w
                        g.color = Color.defaultTextColor
                        g.drawString(label, x + symbolWidth + symbolGap, y + fontY)
                    }

                    y += rowHeight + rowGap

                    if (++row >= rows) {
                        row = 0
                        col++
                        if (col < cols)
                            x += symbolWidth + symbolGap + strWidth
                    }
                }
            }
            i++
        }
    }

    fun setLocation(left: Int, top: Int) {
        this.left = left
        this.top = top
    }

    fun getSize(g: GraphicsContext): Dimension? {
        if (size == null) {
            // estimate max size
            var maxLabel = 0
            var aveLabel = 0
            val labelWidth: Int
            for (i in 0 until charts.count) {
                val obj = charts[i] as? Chart ?: continue
                if (!obj.data.isEmpty()) {
                    var width: Int2D? = null
                    val showInLegend = obj.showInLegend
                    if (showInLegend and SERIES > 0)
                        width = DefaultData.getTitleWidth(obj.data, g)
                    if (showInLegend and DATA_PT > 0) {
                        val width2 = DefaultData.getDataLabelWidth(obj.data, g)
                        if (width == null)
                            width = width2
                        else {
                            width.val1 += width2.val1
                            width.val2 += width2.val2
                        }
                    }
                    if (width!!.val1 > aveLabel)
                        aveLabel = width.val1
                    if (width.val2 > maxLabel)
                        maxLabel = width.val2
                }
            }
            if (orientation == VERTICAL)
                labelWidth = maxLabel
            else
                labelWidth = aveLabel

            val fm = g.getFontMetrics(font)
            val count = itemCount
            val cols = if (orientation == VERTICAL) 1 else count
            val rows = Math.ceil(count.toDouble() / cols).toInt()
            val w = cols * (symbolWidth + symbolGap + labelWidth + margin) + margin
            val h = rows * fm.height + 2 * margin
            size = Dimension(w, h)
        }
        return size
    }

    fun setSize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    companion object {

        val HORIZONTAL = 0
        val VERTICAL = 1
        val BORDER_NONE = 0
        val BORDER_SINGLE = 1
        val BORDER_SHADOW = 2

        val DONT_SHOW = 0x00
        val SERIES_SYMBOL = 0x01
        val SERIES_LABEL = 0x02
        val SERIES = SERIES_SYMBOL or SERIES_LABEL
        val DATA_PT_SYMBOL = 0x04
        val DATA_PT_LABEL = 0x08
        val DATA_PT_VALUE = 0x10
        val DATA_PT = DATA_PT_SYMBOL or DATA_PT_LABEL or DATA_PT_VALUE
    }
}
