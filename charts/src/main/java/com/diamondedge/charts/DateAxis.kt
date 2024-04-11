/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import java.text.DateFormat
import java.text.SimpleDateFormat
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.time.Duration.Companion.days

class DateAxis : Axis() {

    var tickLabelDateFormat: DateFormat = dateFormat
        internal set

    /** Applies the given pattern to the SimpleDateFormat object used in formatting the
     * labels next to each major tick mark.
     * A pattern is a short-hand specification for the various formatting properties.
     * These properties can also be changed individually through the various setter methods.
     * @see DateFormat.applyPattern
     *
     * @see .getTickLabelDecimalFormat
     */
    fun setTickLabelFormatPattern(pattern: String) {
        if (tickLabelDateFormat is SimpleDateFormat)
            (tickLabelDateFormat as SimpleDateFormat).applyPattern(pattern)
        else
            tickLabelDateFormat = SimpleDateFormat(pattern)
    }

    override fun calcMetrics(rangePix: Int, g: GraphicsContext, font: Font?) {
        super.calcMetrics(rangePix, g, font)

        if (isAutoScaling) {
            val range = maxValue - minValue
            log.v { "calcMetrics($rangePix) range: ${range.days} min: $minValue max: $maxValue" }
            val isSmall = rangePix < 500

            when {
                range > 1000 * DateUtil.ONE_YEAR -> {
                    log.v { "> 1000y inc: 500y" }
                    majorTickInc = 500 * DateUtil.ONE_YEAR
                    tickLabelDateFormat = yearFormat
                    minorTickIncNum = 5
                }
                range > 500 * DateUtil.ONE_YEAR -> {
                    log.v { "> 500y inc: 200y" }
                    majorTickInc = 200 * DateUtil.ONE_YEAR
                    tickLabelDateFormat = yearFormat
                    minorTickIncNum = 2
                }
                range > 250 * DateUtil.ONE_YEAR -> {
                    log.v { "> 250y inc: 100y" }
                    majorTickInc = 100 * DateUtil.ONE_YEAR
                    tickLabelDateFormat = yearFormat
                    minorTickIncNum = 5
                }
                range > 100 * DateUtil.ONE_YEAR -> {
                    log.v { "> 100y inc: 50y" }
                    majorTickInc = 50 * DateUtil.ONE_YEAR
                    tickLabelDateFormat = yearFormat
                    minorTickIncNum = 5
                }
                range > 50 * DateUtil.ONE_YEAR -> {
                    log.v { "> 50y inc: 20y" }
                    majorTickInc = 20 * DateUtil.ONE_YEAR
                    tickLabelDateFormat = yearFormat
                    minorTickIncNum = 4
                }
                range > 30 * DateUtil.ONE_YEAR -> {
                    log.v { "> 30y inc: 10y" }
                    majorTickInc = 10 * DateUtil.ONE_YEAR
                    tickLabelDateFormat = yearFormat
                    minorTickIncNum = 2
                }
                range > 10 * DateUtil.ONE_YEAR -> {
                    log.v { "> 10y inc: 5y" }
                    majorTickInc = 5 * DateUtil.ONE_YEAR
                    tickLabelDateFormat = yearFormat
                    minorTickIncNum = 5
                }
                range > 5 * DateUtil.ONE_YEAR -> {
                    log.v { "> 5y inc: 2y" }
                    majorTickInc = 2 * DateUtil.ONE_YEAR
                    tickLabelDateFormat = yearFormat
                    minorTickIncNum = 2
                }
                range > 2 * DateUtil.ONE_YEAR -> {
                    log.v { "> 2y inc: year" }
                    majorTickInc = DateUtil.ONE_YEAR
                    tickLabelDateFormat = yearFormat
                    minorTickIncNum = 4
                }
                range > DateUtil.ONE_YEAR -> {
                    log.v { "> 1y inc: 6mon" }
                    majorTickInc = 6 * DateUtil.ONE_MONTH
                    tickLabelDateFormat = majorMonthFormat
                    minorTickIncNum = 6
                }
                range > 5 * DateUtil.ONE_MONTH -> {
                    log.v { "> 5mon inc: 2mon" }
                    majorTickInc = 2 * DateUtil.ONE_MONTH
                    tickLabelDateFormat = majorMonthFormat
                    minorTickIncNum = 2
                }
                range > DateUtil.ONE_MONTH -> {
                    log.v { "> 1mon inc: mon" }
                    majorTickInc = DateUtil.ONE_MONTH
                    tickLabelDateFormat = majorMonthFormat
                    minorTickIncNum = 4
                }
                range > 8 * DateUtil.ONE_DAY -> {
                    log.v { "> 8d inc: week" }
                    majorTickInc = DateUtil.ONE_WEEK
                    tickLabelDateFormat = majorDayFormat
                    minorTickIncNum = 7
                }
                range > DateUtil.ONE_DAY -> {
                    log.v { "> 1d inc: day" }
                    majorTickInc = DateUtil.ONE_DAY
                    tickLabelDateFormat = majorDayFormat
                    minorTickIncNum = 4
                }
                range > 12 * DateUtil.ONE_HOUR -> {
                    log.v { "> 12h inc: 6h" }
                    majorTickInc = 6 * DateUtil.ONE_HOUR
                    tickLabelDateFormat = hourFormat
                    minorTickIncNum = 3
                }
                range > 8 * DateUtil.ONE_HOUR -> {
                    log.v { "> 8h inc: 3h" }
                    majorTickInc = 3 * DateUtil.ONE_HOUR
                    tickLabelDateFormat = hourFormat
                    minorTickIncNum = 3
                }
                range > 4 * DateUtil.ONE_HOUR -> {
                    log.v { "> 4h inc: 2h" }
                    majorTickInc = 2 * DateUtil.ONE_HOUR
                    tickLabelDateFormat = hourFormat
                    minorTickIncNum = 2
                }
                range > 2 * DateUtil.ONE_HOUR -> {
                    log.v { "> 2h inc: 1h" }
                    majorTickInc = DateUtil.ONE_HOUR
                    tickLabelDateFormat = hourFormat
                    minorTickIncNum = 2
                }
                range > DateUtil.ONE_HOUR -> {
                    log.v { "> 1h inc: 30m" }
                    majorTickInc = DateUtil.HALF_HOUR
                    tickLabelDateFormat = hourMinuteFormat
                    minorTickIncNum = 3
                }
                range > 50 * DateUtil.ONE_MINUTE -> {
                    log.v { "> 50m inc: 15/20m" }
                    majorTickInc = (if (isSmall) 20 else 15) * DateUtil.ONE_MINUTE
                    tickLabelDateFormat = hourMinuteFormat
                    minorTickIncNum = if (isSmall) 4 else 3
                }
                range > 30 * DateUtil.ONE_MINUTE -> {
                    log.v { "> 30m inc: 15m" }
                    majorTickInc = 15 * DateUtil.ONE_MINUTE
                    tickLabelDateFormat = hourMinuteFormat
                    minorTickIncNum = 3
                }
                range > 16 * DateUtil.ONE_MINUTE -> {
                    log.v { "> 16m inc: 10m" }
                    majorTickInc = 10 * DateUtil.ONE_MINUTE
                    tickLabelDateFormat = hourMinuteFormat
                    minorTickIncNum = 5
                }
                range > 10 * DateUtil.ONE_MINUTE -> {
                    log.v { "> 10m inc: 5m" }
                    majorTickInc = 5 * DateUtil.ONE_MINUTE
                    tickLabelDateFormat = hourMinuteFormat
                    minorTickIncNum = 5
                }
                range > 6 * DateUtil.ONE_MINUTE -> {
                    log.v { "> 6m inc: 3m" }
                    majorTickInc = 3 * DateUtil.ONE_MINUTE
                    tickLabelDateFormat = hourMinuteFormat
                    minorTickIncNum = 3
                }
                range > 4 * DateUtil.ONE_MINUTE && isSmall -> {
                    log.v { "> 4m inc: 2m" }
                    majorTickInc = 2 * DateUtil.ONE_MINUTE
                    tickLabelDateFormat = hourMinuteFormat
                    minorTickIncNum = 2
                }
                range > 90 * DateUtil.ONE_SECOND -> {
                    log.v { "> 90s inc: 1m" }
                    majorTickInc = DateUtil.ONE_MINUTE
                    tickLabelDateFormat = hourMinuteFormat
                    minorTickIncNum = 4
                }
                range > 30 * DateUtil.ONE_SECOND -> {
                    log.v { "> 30s inc: 20s" }
                    majorTickInc = 20 * DateUtil.ONE_SECOND
                    tickLabelDateFormat = hourMinuteSecondFormat
                    minorTickIncNum = 4
                }
                range > 4 * DateUtil.ONE_SECOND -> {
                    log.v { "> 4s inc: 10s" }
                    majorTickInc = 10 * DateUtil.ONE_SECOND
                    tickLabelDateFormat = hourMinuteSecondFormat
                    minorTickIncNum = 2
                }
                else -> {
                    log.v { "inc: 1s" }
                    majorTickInc = DateUtil.ONE_SECOND
                    tickLabelDateFormat = secondFormat
                    minorTickIncNum = 4
                }
            }
            if (numberMinorIncrements > 0) {
                minorTickIncNum = numberMinorIncrements
            }
            if (majorTickIncrement > 0) {
                majorTickInc = majorTickIncrement
            }

            if (!startAtMinValue) {
                // make minVal be an exact multiple of majorTickInc just smaller than minVal
                minValue = floor(minValue / majorTickInc) * majorTickInc
            }
            if (!endAtMaxValue) {
                // make maxVal be an exact multiple of majorTickInc just larger than maxVal
                maxValue = ceil(maxValue / majorTickInc) * majorTickInc
            }

            calcScale(rangePix)
        }
    }

    override fun tickLabel(value: Double): String {
        return numberFormatter?.invoke(value) ?: tickLabelDateFormat.format(DateUtil.toDate(value))
    }

    override fun toString(): String {
        return "DateAxis[" + toStringParam() + "]"
    }

    companion object {
        private val log = moduleLogging()

        private val dateFormat = DateFormat.getDateInstance()
        private val timeFormat = DateFormat.getTimeInstance()
        private val yearFormat = SimpleDateFormat("yyyy")
        private val debugMonthFormat = SimpleDateFormat("M-d")
        private val majorMonthFormat = SimpleDateFormat("MMM yyyy")
        private val minorMonthFormat = SimpleDateFormat("MMM")
        private val majorDayFormat = dateFormat
        private val minorDayFormat = SimpleDateFormat("d")
        private val hourFormat = SimpleDateFormat("ha")
        private val hourMinuteFormat = SimpleDateFormat("h:mma")
        private val hourMinuteSecondFormat = SimpleDateFormat("h:mm:ssa")
        private val secondFormat = SimpleDateFormat("s")
    }
}
