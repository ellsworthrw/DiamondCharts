/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

class BarChart(
    data: ChartData,
    override val isVertical: Boolean = true,
    val isStacked: Boolean = false,
    val is100Percent: Boolean = false
) : Chart(data) {

    /** The space in between bars in a group as a percentage of the bar width.
     * This is not used for stacked bars or data with only one data series.
     */
    var gap = 0

    /** The total width of the bars in a group as a percentage of the width allotted to the group.
     * Setting to 80 will allow 10% space on each side or 20% in between groups of bars.
     */
    var barWidth = 80

    init {
        if (isVertical && vertAxis != null)
            vertAxis!!.isZeroRequired = true
        else if (horAxis != null)
            horAxis!!.isZeroRequired = true

        println("hor: " + horAxis)
        println("vert: " + vertAxis)
    }

    override fun setup(combineSeries: Boolean) {
        super.setup(isStacked || is100Percent)
        if (isStacked || is100Percent) {
            data.minValue = 0.0
            if (is100Percent) {
                data.maxValue = 100.0
            }
        }
    }

    override fun createVerticalAxis(): Axis {
        if (isVertical) {
            return super.createVerticalAxis()
        }
        val axis = LabelAxis()
        axis.setLabelPosition(TickLabelPosition.GroupCenter)
        return axis
    }

    override fun createHorizontalAxis(): Axis {
        if (isVertical) {
            val axis = LabelAxis()
            axis.setLabelPosition(TickLabelPosition.GroupCenter)
            return axis
        }
        return super.createHorizontalAxis()
    }

    override fun draw(g: GraphicsContext) {
        if (data == null)
            return
        val dsCount = data.seriesCount
        val dataCount = data.dataCount
        var valueAxis = vertAxis
        var catAxis = horAxis
        if (!isVertical) {
            valueAxis = horAxis
            catAxis = vertAxis
        }
        var gattr: GraphicAttributes
        val unitWidth = catAxis!!.scaleData(2.0) - catAxis.scaleData(1.0)
        var barWidth = unitWidth * this.barWidth / 100
        val gap = barWidth / dsCount * this.gap / 100
        var iBarWidth = barWidth
        var catPos: Int
        var valPos: Int
        if (!isStacked && dsCount > 1) {
            iBarWidth = (barWidth - gap * (dsCount - 1)) / dsCount
            // recalc amount used by all bars to account for roundoff errors
            barWidth = iBarWidth * dsCount + gap * (dsCount - 1)
        }
        // center the bars in the area
        val offset = (unitWidth - barWidth) / 2
        val zero = valueAxis!!.convertToPixel(0.0)

        var total: DoubleArray? = null
        if (is100Percent) {
            total = DoubleArray(dataCount)
            for (i in 0 until dataCount) {
                var value = 0.0
                for (series in 0 until dsCount) {
                    value += data.getDouble(series, i)
                }
                total[i] = value
            }
        }

        //System.out.println( "hor: " + catAxis );
        //System.out.println( "vert: " + valueAxis );

        println("bar: $barWidth iBarWidth: $iBarWidth gap: $gap")
        println("offset: $offset unitWidth: $unitWidth")
        hotspots?.clear()

        for (i in 0 until dataCount) {
            if (isVertical) {
                catPos = catAxis.convertToPixel(i.toDouble()) + offset
                valPos = zero - 1
            } else {
                catPos = catAxis.convertToPixel((i + 1).toDouble()) + offset
                valPos = zero
            }

            for (series in 0 until dsCount) {
                var dataValue = data.getDouble(series, i)
                if (total != null) {                            // then using 100 percent fill
                    dataValue = dataValue / total[i] * 100      // convert to percent of total
                }
                val value = valueAxis.scaleData(dataValue)

                gattr = data.getGraphicAttributes(series)
                if (isVertical)
                    Draw.drawRect(g, catPos, valPos - value, iBarWidth, value, gattr)
                else
                    Draw.drawRect(g, valPos, catPos, value, iBarWidth, gattr)

                if (hotspots != null) {
                    var rect: Rectangle? = null
                    if (isVertical)
                        rect = Rectangle(catPos, valPos - value, iBarWidth, value)
                    else
                        rect = Rectangle(valPos, catPos, value, iBarWidth)
                    hotspots!!.add(Hotspot(this, data, series, i, rect))
                }

                if (isStacked) {
                    if (isVertical)
                        valPos -= value
                    else
                        valPos += value
                } else
                    catPos += gap + iBarWidth
            }
        }
    }

    override fun toString(): String {
        return "BarChart[gap%=" + gap + ",bar%=" + barWidth + "," + toStringParam() + "]"
    }
}
