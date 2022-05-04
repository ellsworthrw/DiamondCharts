/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

class PieChart(data: ChartData) : Chart(data) {
    private var x: Int = 0
    private var y: Int = 0
    private var width: Int = 0
    private var height: Int = 0
    var margin = 10
    private val startAngle = 0
/*
    var explodeAmount = 12
        set(value) {
            field = value
            if (margin < this.explodeAmount + 5)
                margin = this.explodeAmount + 5
        }
*/

    override val showInLegend: Int
        get() = Legend.SERIES or Legend.DATA_PT

    override fun usesAxis(): Boolean {
        return false
    }

/*
    fun isExploded(dataPtNum: Int): Boolean {
        if (data != null) {
            val dataPt = data.getDataPoint(0, dataPtNum, false)
            if (dataPt != null && dataPt.flags and EXPLODED > 0)
                return true
        }
        return false
    }

    fun setExploded(dataPtNum: Int, explodeSlice: Boolean) {
        setExploded(0, dataPtNum, explodeSlice)
    }

    fun setExploded(series: Int, dataPtNum: Int, explodeSlice: Boolean) {
        val dataPt = data.getDataPoint(series, dataPtNum, true)
        if (dataPt != null) {
            dataPt.flags = dataPt.flags and EXPLODED.inv()
            if (explodeSlice)
                dataPt.flags = dataPt.flags or EXPLODED
            if (margin < this.explodeAmount + 5)
                margin = this.explodeAmount + 5
        }
    }
*/

    override fun setBounds(x: Int, y: Int, width: Int, height: Int) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }

    override fun drawLegendSymbol(g: GraphicsContext, x: Int, y: Int, width: Int, height: Int, series: Int, dataPtNum: Int): Boolean {
        val dataCount = data.dataCount
        g.color = Draw.getColor(series * dataCount + dataPtNum)
        g.fillRect(x, y, width, height)
        return true
    }

    override fun draw(g: GraphicsContext) {
        if (data.isEmpty())
            return
        val dsCount = data.seriesCount
        val dataCount = data.dataCount
        var x = this.x
        var y = this.y
        var rows = 1
        if (dsCount > 8)
            rows = 3
        else if (dsCount > 2)
            rows = 2
        val cols = Math.ceil(dsCount.toDouble() / rows).toInt()
        var w = (width - margin) / cols - margin
        if ((height - margin) / rows - margin < w)
            w = height / rows - margin
        val r = w / 2.0   // radius

        val hpts_max = 30   // max number of points in polygon for hotspot
        var xPts: IntArray? = null
        var yPts: IntArray? = null
        if (hotspots != null) {
            hotspots!!.clear()
            xPts = IntArray(hpts_max)
            yPts = IntArray(hpts_max)
        }

        var row = 0
        var col = 0
        for (series in 0 until dsCount) {
            var total = 0.0
            var value: Double
            var angle: Double
            var startAngle = this.startAngle.toDouble()
            if (col == 0) {
                x = this.x + margin
                y += margin
            }
            var c: Long
            for (i in 0 until dataCount) {
                total += data.getValue(series, i)
            }

            log.d { "x=" + x + " end=" + (x + w) }
            for (i in 0 until dataCount) {
                value = data.getValue(series, i) / total
                if (i == dataCount - 1)
                // last angle
                    angle = 360 - startAngle   // make sure it finishes circle
                else
                    angle = (value * 360).toInt().toDouble()
                var offsetX = 0
                var offsetY = 0
/*
                val dataPt = data.getDataPoint(series, i, false)
                if (dataPt != null && dataPt.flags and EXPLODED > 0) {
                    val theta = (startAngle + angle / 2) * Math.PI / 180
                    offsetX = (this.explodeAmount * Math.cos(theta)).toInt()
                    offsetY = (this.explodeAmount * Math.sin(theta)).toInt()
                }
*/
                log.d { "arc $i: $value angle=$angle start=$startAngle" }
                //g.setColor( gattr.color );
                c = Draw.getColor(series * dataCount + i)
                g.color = c
                g.fillArc(x + offsetX, y - offsetY, w, w, startAngle.toInt(), angle.toInt())

                if (xPts != null && yPts != null) {
                    val xc = x + w / 2 + offsetX
                    val yc = y + w / 2 - offsetY
                    xPts[0] = xc
                    yPts[0] = yc
                    // point every 12 degrees, except more at smaller angles
                    var hpts = (angle + 26).toInt() / 12 + 1
                    if (hpts > hpts_max)
                        hpts = hpts_max
                    val angleRadians = angle * Math.PI / 180
                    var theta = startAngle * Math.PI / 180
                    val angleIncr = angleRadians / (hpts - 2)
                    for (j in 1 until hpts) {
                        xPts[j] = xc + (r * Math.cos(theta)).toInt()
                        yPts[j] = yc - (r * Math.sin(theta)).toInt()
                        theta += angleIncr
                    }
                    //          hotspots.add( new Hotspot( this, data, series, i, new Polygon( xPts, yPts, hpts ) ) );
                }
                startAngle += angle
            }

            x += w + margin

            if (++col >= cols) {
                col = 0
                row++
                if (row < rows)
                    y += w
            }
        }
    }

    override fun toString(): String {
        return "PieChart[margin=" + margin + "," + toStringParam() + "]"
    }

    companion object {
        private val log = moduleLogging()
    }
}
