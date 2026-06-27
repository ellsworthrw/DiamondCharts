/**
 * Copyright 2004-2026 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min

open class DecimalAxis : Axis() {

    /** Maximum number of fractional digits emitted by the tick label formatter (trailing zeros dropped). */
    private var fractionDigits = 0

    /** A `java.text.DecimalFormat`-style pattern (e.g. `"#"` or `"#.##"`) controlling the number of
     * fractional digits in the labels next to each major tick mark. Only the count of `#`/`0` symbols
     * after the decimal point is significant; grouping separators are not supported.
     *
     * Set to `null` (the default) to let the axis pick the precision automatically.
     */
    var majorTickFormat: String? = null
        set(value) {
            field = value
            if (value != null)
                fractionDigits = fractionDigitsFromPattern(value)
        }

    init {
        numberFormatter = { formatDecimal(it, fractionDigits) }
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
        }

        if (!startAtMinValue) {
            // make minVal be an exact multiple of majorTickInc just smaller than minVal
            minValue = floor(min(minValue, minDataVal - lowerDataMargin) / majorTickInc) * majorTickInc
        }
        if (!endAtMaxValue) {
            // make maxVal be an exact multiple of majorTickInc just larger than maxVal
            maxValue = ceil(max(maxValue, maxDataVal + upperDataMargin) / majorTickInc) * majorTickInc
        }
        adjustMinMax()
        calcScale(rangePix)

        log.v { " incval = $majorTickInc incvalPix = ${scaleData(majorTickInc)}" }
        log.v { " minVal = $minValue maxVal = $maxValue rangePix = $rangePix" }

        if (this.majorTickFormat == null) {
            val exp = log10(scalePixel(tickDistance)) // log base 10
            fractionDigits = if (exp < 0 || majorTickInc < 1) {
                // mirrors the old "#.#" + one extra "#" per integer in exp.toInt()..-1
                var digits = 1
                var i = exp.toInt()
                while (i <= -1) {
                    digits++
                    i++
                }
                digits
            } else {
                0
            }
        }
    }

    private fun fractionDigitsFromPattern(pattern: String): Int {
        val dot = pattern.indexOf('.')
        if (dot < 0) return 0
        var n = 0
        for (i in dot + 1 until pattern.length) {
            if (pattern[i] == '#' || pattern[i] == '0') n++
        }
        return n
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
