/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

class LogarithmicAxis : DecimalAxis() {
    init {
        minorTickIncNum = 10
    }

    override fun calcMetrics(rangePix: Int, g: GraphicsContext, font: Font?) {
        super.calcMetrics(rangePix, g, font)

        if (isAutoScaling) {
            // make minVal be an exact multiple of majorTickInc just smaller than minVal
            var tickInc = nextMajorIncVal(minValue, 1.0)
            minValue = Math.floor(minValue / tickInc) * tickInc

            // make maxVal be an exact multiple of majorTickInc just larger than maxVal
            tickInc = nextMajorIncVal(maxValue, tickInc)
            maxValue = Math.ceil(maxValue / tickInc) * tickInc

            adjustMinMax()
            calcScale(rangePix)
        }
    }

    override fun calcScale(rangePix: Int): Double {
        val rangeVal = log10(maxValue) - log10(minValue)
        scale = rangeVal / rangePix
        if (scale == 0.0)
            scale = 1.0
        //System.out.println( "calcScale: " + rangePix + " scale: " + scale );
        return scale
    }

    override fun nextMajorIncVal(pos: Double, incVal: Double): Double {
        var incVal = incVal
        incVal = Math.pow(10.0, (Math.log(pos) / LOG10).toInt().toDouble())
        if (incVal == 0.0)
            incVal = 1.0
        return incVal
    }

    override fun adjustMinMax() {
        // cannot have log scales with negative numbers
        if (minValue < 0)
            minValue = 0.0
    }

    /** Return data value scaled to be in pixels
     */
    override fun scaleData(dataValue: Double): Int {
        return (log10(dataValue) / scale).toInt()
    }

    override fun scalePixel(pixelValue: Int): Double {
        return Math.pow(10.0, pixelValue * scale)
    }

    override fun toString(): String {
        return "LogarithmicAxis[" + toStringParam() + "]"
    }

    companion object {
        private val LOG10 = Math.log(10.0)

        private fun log10(value: Double): Double {
            var value = value
            val sign = if (value < 0) -1 else 1
            value = Math.abs(value)
            if (value < 10)
                value += (10 - value) / 10   // make 0 correspond to 0
            return Math.log(value) / LOG10 * sign
        }
    }
}
