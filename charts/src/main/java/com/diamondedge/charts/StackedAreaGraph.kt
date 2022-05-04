/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

class StackedAreaGraph(data: ChartData, val is100Percent: Boolean = false) : Chart(data) {

    override fun setupData(combineSeries: Boolean) {
        super.setupData(true)
    }

    override fun setupAxis() {
        super.setupAxis()
        if (is100Percent) {
            vertAxis?.minValueOverride = 0.0
            vertAxis?.maxValueOverride = 100.0
        }
    }

    override fun createHorizontalAxis(): Axis {
        return LabelAxis()
    }

    override fun draw(g: GraphicsContext) {
        val dsCount = data.seriesCount
        val dataCount = data.dataCount
        var ptNum: Int
        val ptCount = dataCount * 2
        val xPts = IntArray(ptCount)
        val yPts = IntArray(ptCount)
        var gattr: GraphicAttributes
        var x = 0
        var y: Int
        var lastY: Int
        // center the bars in the area
        val offset = 0 //unitWidth / 2;
        var useFirst = true
        if (hotspots != null)
            hotspots!!.clear()

        var total: DoubleArray? = null
        if (is100Percent) {
            total = DoubleArray(dataCount)
            for (i in 0 until dataCount) {
                var value = 0.0
                for (series in 0 until dsCount) {
                    value += data.getValue(series, i)
                }
                total[i] = value
            }
        }
        // y values for 2 sets of data are being kept
        // this is the polygon that being drawn
        // if useFirst = true then current data is in yPts[0 to dataCount-1]
        // otherwise current data is in yPts[ptCount-1 down to dataCount]
        //           note: points are in reverse order ie. 1st point in yPts[ptCount-1]
        //           this is so the pts in the polygon go around the perimeter of the
        //           area between the 2 sets of data

        y = vertAxis!!.y
        for (i in 0 until ptCount)
            yPts[i] = y

        for (series in 0 until dsCount) {
            gattr = data.getGraphicAttributes(series)
            if (useFirst)
                ptNum = 0
            else
                ptNum = ptCount - 1

            for (i in 0 until dataCount) {
                // get the y value from the last series
                y = yPts[ptCount - ptNum - 1]
                if (series == 0)
                    y = vertAxis!!.convertToPixel(0.0)
                lastY = y
                var value = data.getValue(series, i)
                if (total != null) {                    // then using 100 percent fill
                    value = value / total[i] * 100      // convert to percent of total
                }
                // subtract current y value
                y -= vertAxis!!.scaleData(value)
                x = horAxis!!.convertToPixel(i.toDouble()) + offset
                if (i == 0)
                    x++
                xPts[ptNum] = x
                yPts[ptNum] = y
                if (useFirst)
                    ptNum++
                else
                    ptNum--

                if (hotspots != null) { // add hotspot for data point
                    val rect = Rectangle(x - hotspotWidth / 2, y - hotspotWidth / 2, hotspotWidth, lastY - y)
                    log.d { rect }
                    hotspots!!.add(Hotspot(this, data, series, i, rect))
                }
            }
            g.color = gattr.color
            if (series == 0) {
                xPts[ptNum] = x
                yPts[ptNum] = vertAxis!!.y
                xPts[ptNum + 1] = vertAxis!!.x + 1
                yPts[ptNum + 1] = vertAxis!!.y
                g.fillPolygon(xPts, yPts, ptNum + 2)
            } else
                g.fillPolygon(xPts, yPts, ptCount)
            useFirst = !useFirst
        }
    }

    override fun toString(): String {
        return "StackedAreaGraph[" + toStringParam() + "]"
    }

    companion object {
        private val log = moduleLogging()

        private val hotspotWidth = 7
    }
}
