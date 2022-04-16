/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.chart

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.time.Duration.Companion.days

class DateAxis : Axis() {

    private val dateFormat = DateFormat.getDateInstance()
    private val timeFormat = DateFormat.getTimeInstance()
    private val yearFormat = SimpleDateFormat("yyyy")
    private val majorMonthFormat = SimpleDateFormat("MMM yyyy")
    private val minorMonthFormat = SimpleDateFormat("MMM")
    private val majorDayFormat = dateFormat
    private val minorDayFormat = SimpleDateFormat("d")
    private val hourFormat = SimpleDateFormat("ha")
    private val hourMinuteFormat = SimpleDateFormat("h:mma")
    private val minuteFormat = SimpleDateFormat("m")
    private val secondFormat = SimpleDateFormat("s")

    var tickLabelDateFormat: DateFormat = dateFormat

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
            log.v { "calcMetrics($rangePix) range: ${range.days}" }

            when {
                range > DateUtil.ONE_YEAR -> {
                    log.v { "> 1y inc: year" }
                    majorTickInc = DateUtil.ONE_YEAR
                    tickLabelDateFormat = yearFormat
                    minorTickIncNum = 12
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
                range > 15 * DateUtil.ONE_HOUR -> {
                    log.v { "> 15h inc: 3h" }
                    majorTickInc = 3 * DateUtil.ONE_HOUR
                    tickLabelDateFormat = hourFormat
                    minorTickIncNum = 3
                }
                range > 7 * DateUtil.ONE_HOUR -> {
                    log.v { "> 7h inc: 2h" }
                    majorTickInc = 2 * DateUtil.ONE_HOUR
                    tickLabelDateFormat = hourFormat
                    minorTickIncNum = 4
                }
                range > 2 * DateUtil.ONE_HOUR -> {
                    log.v { "> 2h inc: 1h" }
                    majorTickInc = DateUtil.ONE_HOUR
                    tickLabelDateFormat = hourFormat
                    minorTickIncNum = 2
                }
                range > 45 * DateUtil.ONE_MINUTE -> {
                    log.v { "> 45m inc: 30m" }
                    majorTickInc = DateUtil.HALF_HOUR
                    tickLabelDateFormat = hourMinuteFormat
                    minorTickIncNum = 3
                }
                range > 20 * DateUtil.ONE_MINUTE -> {
                    log.v { "> 20m inc: 10m" }
                    majorTickInc = 10 * DateUtil.ONE_MINUTE
                    tickLabelDateFormat = hourMinuteFormat
                    minorTickIncNum = 2
                }
                range > 9 * DateUtil.ONE_MINUTE -> {
                    log.v { "> 9m inc: 5m" }
                    majorTickInc = 5 * DateUtil.ONE_MINUTE
                    tickLabelDateFormat = hourMinuteFormat
                    minorTickIncNum = 5
                }
                range > DateUtil.ONE_MINUTE -> {
                    log.v { "> 1m inc: 1m" }
                    majorTickInc = DateUtil.ONE_MINUTE
                    tickLabelDateFormat = hourMinuteFormat
                    minorTickIncNum = 6
                }
                range > 9 * DateUtil.ONE_SECOND -> {
                    log.v { "> 9s inc: 5s" }
                    majorTickInc = 5 * DateUtil.ONE_SECOND
                    tickLabelDateFormat = hourMinuteFormat
                    minorTickIncNum = 5
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

            // make minVal be an exact multiple of majorTickInc just smaller than minVal
            minValue = Math.floor(minValue / majorTickInc) * majorTickInc
            // make maxVal be an exact multiple of majorTickInc just larger than maxVal
            maxValue = Math.ceil(maxValue / majorTickInc) * majorTickInc

/*
            // make minVal be an exact multiple of minorTickInc just smaller than minVal
            var tickInc = nextMinorIncVal(minValue, 1.0)
            minValue = Math.floor(minValue / tickInc) * tickInc

            // make maxVal be an exact multiple of minorTickInc just larger than maxVal
            tickInc = nextMinorIncVal(maxValue, 1.0)
            maxValue = Math.ceil(maxValue / tickInc) * tickInc
*/

            calcScale(rangePix)
        }
    }

    override fun nextMinorIncVal(pos: Double, incVal: Double): Double {
        return nextInc(pos, incVal)
    }

    override fun nextMajorIncVal(pos: Double, incVal: Double): Double {
        return nextInc(pos, incVal)
    }

    override fun tickLabel(value: Double): String {
        return numberFormatter?.invoke(value) ?: tickLabelDateFormat.format(DateUtil.toDate(value))
    }

    override fun toString(): String {
        return "DateAxis[" + toStringParam() + "]"
    }

    companion object {
        private val cal = Calendar.getInstance()
        private val log = moduleLogging()

        internal fun nextInc(pos: Double, incVal: Double): Double {
            if (incVal == DateUtil.ONE_MONTH) {
                DateUtil.setCalendar(cal, pos)
                cal.add(Calendar.MONTH, 1)
                DateUtil.clearCalBelow(cal, Calendar.MONTH)
                return DateUtil.toDouble(cal) - pos
            }
            return incVal
        }
    }
}
