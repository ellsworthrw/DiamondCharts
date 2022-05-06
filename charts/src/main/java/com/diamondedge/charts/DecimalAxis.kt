/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import java.text.DecimalFormat
import kotlin.math.max

open class DecimalAxis : Axis() {

    private val majorTickDecimalFormat = DecimalFormat()

    /** Applies the given pattern to this DecimalFormat object used in formatting the
     * labels next to each major tick mark.
     * A pattern is a short-hand specification for the various formatting properties.
     * These properties can also be changed individually through the various setter methods.
     * @see DecimalFormat.applyPattern
     *
     * @see .getMajorTickDecimalFormat
     */
    var majorTickFormat: String? = null
        set(value) {
            field = value
            if (value != null)
                majorTickDecimalFormat.applyPattern(value)
        }

    init {
        numberFormatter = majorTickDecimalFormat::format
    }

    override fun calcMetrics(rangePix: Int, g: GraphicsContext, font: Font?) {
        super.calcMetrics(rangePix, g, font)

        majorTickInc = 1.0
        val tickDistance = g.dpToPixel(40f)  // make ticks about this many pixels apart (1/4 inch)
        if (majorTickIncrement > 0) {
            majorTickInc = majorTickIncrement
        } else if (isAutoScaling) {
            // calculate best fit for data
            val fm = g.getFontMetrics(font)
            var minTick = if (isVertical)
                (fm.height * 1.5).toInt()
            else
                getTickLabelMaxWidth(g) * 2
            minTick = maxOf(minTick, tickDistance)
            val maxTick = max(g.dpToPixel(160f), 2 * minTick)  // max of about an inch

            log.v { "incval = $majorTickInc  incvalPix = ${scaleData(majorTickInc)}  minTick = $minTick maxTick = $maxTick" }

            if (scaleData(majorTickInc) < minTick) {
                majorTickInc *= when {
                    scaleData(majorTickInc * 2) >= minTick -> 2.0
                    scaleData(majorTickInc * 5) >= minTick -> 5.0
                    else -> 10.0
                }
            } else if (scaleData(majorTickInc) >= maxTick) {
                majorTickInc /= when {
                    scaleData(majorTickInc / 2) <= maxTick -> 2.0
                    scaleData(majorTickInc / 5) <= maxTick -> 5.0
                    else -> 10.0
                }
            }

            // make minVal be an exact multiple of majorTickInc just smaller than minVal
            minValue = Math.floor(minValue / majorTickInc) * majorTickInc
            // make maxVal be an exact multiple of majorTickInc just larger than maxVal
            maxValue = Math.ceil(maxValue / majorTickInc) * majorTickInc

            adjustMinMax()
            calcScale(rangePix)

            log.v { " incval = $majorTickInc incvalPix = ${scaleData(majorTickInc)}" }
            log.v { " minVal = $minValue maxVal = $maxValue rangePix = $rangePix" }
        }

        if (this.majorTickFormat == null) {
            val exp = Math.log(scalePixel(tickDistance)) / Math.log(10.0) // log base 10
            if (exp < 0 || majorTickInc < 1) {
                val str = StringBuffer()
                str.append("#.#")
                for (i in exp.toInt()..-1)
                    str.append("#")
                majorTickDecimalFormat.applyPattern(str.toString())
            } else
                majorTickDecimalFormat.applyPattern("#")
        }
    }

    override fun toStringParam(): String {
        return super.toStringParam() + ",majorTickFormat=" + this.majorTickFormat
    }

    override fun toString(): String {
        return "DecimalAxis[" + toStringParam() + "]"
    }

    companion object {
        private val log = moduleLogging()
    }
}
