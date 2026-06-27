/**
 * Copyright 2004-2026 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import com.diamondedge.charts.DateAxis.Companion.largeYearIncrement
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

/** Formats a date/time tick label from `epochMillis` (milliseconds since the Unix epoch, UTC). */
fun interface DateLabelFormatter {
    fun format(epochMillis: Long): String
}

class DateAxis : Axis() {
    /** Cache of compiled formatters keyed by their pattern string. */
    private val dateFormats = mutableMapOf<String, DateLabelFormatter>()

    /** Locale-aware platform formatter for the SHORT/MEDIUM time and MEDIUM date labels. */
    private val localized = LocalizedDateFormatter()

    var useFewerLabels: Boolean = false

    var tickLabelDateFormat: DateLabelFormatter = dateFormat(DateLabelFormat.DAY.pattern)
        internal set

    /** Applies the given [pattern] to the formatter used for the labels next to each major tick mark.
     * The pattern is a Unicode date/time pattern (the same shorthand accepted by `SimpleDateFormat`,
     * e.g. `"yyyy"` or `"MMM yyyy"`); the well-known patterns used internally are recognized directly.
     */
    fun setTickLabelFormatPattern(pattern: String) {
        tickLabelDateFormat = dateFormat(pattern)
    }

    override fun calcMetrics(rangePix: Int, g: GraphicsContext, font: Font?) {
        super.calcMetrics(rangePix, g, font)

        if (isAutoScaling) {
            val range = maxValue - minValue
            log.v { "calcMetrics($rangePix) range: ${range.days} min: $minValue max: $maxValue" }

            val scale = autoScale(range, useFewerLabels)
            majorTickInc = scale.majorTickInc
            tickLabelDateFormat = dateFormat(scale.format.pattern)
            minorTickIncNum = scale.minorTickIncNum

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
        numberFormatter?.let { return it.invoke(value) }
        return tickLabelDateFormat.format(DateUtil.toDate(value))
    }

    override fun toString(): String {
        return "DateAxis[" + toStringParam() + "]"
    }

    /** Returns (building and caching as needed) the formatter for the given [pattern]. The three
     * localized styles delegate to the platform [localized] formatter; everything else is rendered
     * in common code via kotlinx-datetime. */
    private fun dateFormat(pattern: String): DateLabelFormatter {
        return dateFormats.getOrPut(pattern) {
            when (pattern) {
                DateLabelFormat.HOUR_MINUTE.pattern -> DateLabelFormatter { localized.shortTime(it) }
                DateLabelFormat.HOUR_MINUTE_SECOND.pattern -> DateLabelFormatter { localized.mediumTime(it) }
                DateLabelFormat.DAY.pattern -> DateLabelFormatter { localized.mediumDate(it) }
                DateLabelFormat.MONTH.pattern -> DateLabelFormatter { localized.monthYear(it) }
                else -> kotlinxFormatter(buildDateFormat(pattern))
            }
        }
    }

    companion object {
        private val log = moduleLogging()

        /**
         * Auto-scaling decision, factored out of [calcMetrics] as a pure function so it can be unit
         * tested without a [GraphicsContext] or axis state. Given the visible [range] (in days) it
         * picks the major-tick increment, the label format, and the number of minor increments.
         *
         * The branches are arranged so that the number of major ticks (`range / majorTickInc`) stays
         * around 5 and **never exceeds 9** for any range: each branch's upper bound (the threshold of
         * the next-coarser branch) is roughly 5x its increment, so as a range grows and would push a
         * finer increment past ~5-8 ticks, the next branch — a coarser increment — takes over instead.
         * Above the largest fixed branch the increment is computed along the 1-2-5 sequence of years
         * ([largeYearIncrement]) so the bound continues to hold for arbitrarily large ranges.
         *
         * [useFewerLabels] enables a few extra sub-hour branches that thin out the minute-scale ticks.
         */
        internal fun autoScale(range: Double, useFewerLabels: Boolean): DateTickScale = when {
            range > 1000 * DateUtil.ONE_YEAR -> DateTickScale(largeYearIncrement(range), DateLabelFormat.YEAR, 5)
            range > 500 * DateUtil.ONE_YEAR -> DateTickScale(200 * DateUtil.ONE_YEAR, DateLabelFormat.YEAR, 2)
            range > 250 * DateUtil.ONE_YEAR -> DateTickScale(100 * DateUtil.ONE_YEAR, DateLabelFormat.YEAR, 5)
            range > 100 * DateUtil.ONE_YEAR -> DateTickScale(50 * DateUtil.ONE_YEAR, DateLabelFormat.YEAR, 5)
            range > 50 * DateUtil.ONE_YEAR -> DateTickScale(20 * DateUtil.ONE_YEAR, DateLabelFormat.YEAR, 4)
            range > 30 * DateUtil.ONE_YEAR -> DateTickScale(10 * DateUtil.ONE_YEAR, DateLabelFormat.YEAR, 2)
            range > 10 * DateUtil.ONE_YEAR -> DateTickScale(5 * DateUtil.ONE_YEAR, DateLabelFormat.YEAR, 5)
            range > 5 * DateUtil.ONE_YEAR -> DateTickScale(2 * DateUtil.ONE_YEAR, DateLabelFormat.YEAR, 2)
            range > 2 * DateUtil.ONE_YEAR -> DateTickScale(DateUtil.ONE_YEAR, DateLabelFormat.YEAR, 4)
            range > DateUtil.ONE_YEAR -> DateTickScale(6 * DateUtil.ONE_MONTH, DateLabelFormat.MONTH, 6)
            range > 5 * DateUtil.ONE_MONTH -> DateTickScale(2 * DateUtil.ONE_MONTH, DateLabelFormat.MONTH, 2)
            range > DateUtil.ONE_MONTH -> DateTickScale(DateUtil.ONE_MONTH, DateLabelFormat.MONTH, 4)
            range > 8 * DateUtil.ONE_DAY -> DateTickScale(DateUtil.ONE_WEEK, DateLabelFormat.DAY, 7)
            range > DateUtil.ONE_DAY -> DateTickScale(DateUtil.ONE_DAY, DateLabelFormat.DAY, 4)
            range > 12 * DateUtil.ONE_HOUR -> DateTickScale(6 * DateUtil.ONE_HOUR, DateLabelFormat.HOUR_MINUTE, 3)
            range > 8 * DateUtil.ONE_HOUR -> DateTickScale(3 * DateUtil.ONE_HOUR, DateLabelFormat.HOUR_MINUTE, 3)
            range > 4 * DateUtil.ONE_HOUR -> DateTickScale(2 * DateUtil.ONE_HOUR, DateLabelFormat.HOUR_MINUTE, 2)
            range > 2 * DateUtil.ONE_HOUR -> DateTickScale(DateUtil.ONE_HOUR, DateLabelFormat.HOUR_MINUTE, 2)
            range > DateUtil.ONE_HOUR -> DateTickScale(DateUtil.HALF_HOUR, DateLabelFormat.HOUR_MINUTE, 3)
            range > 50 * DateUtil.ONE_MINUTE ->
                DateTickScale(
                    (if (useFewerLabels) 20 else 15) * DateUtil.ONE_MINUTE,
                    DateLabelFormat.HOUR_MINUTE,
                    if (useFewerLabels) 4 else 3
                )
            range > 30 * DateUtil.ONE_MINUTE && useFewerLabels ->
                DateTickScale(15 * DateUtil.ONE_MINUTE, DateLabelFormat.HOUR_MINUTE, 3)
            range > 16 * DateUtil.ONE_MINUTE -> DateTickScale(10 * DateUtil.ONE_MINUTE, DateLabelFormat.HOUR_MINUTE, 5)
            range > 10 * DateUtil.ONE_MINUTE && useFewerLabels ->
                DateTickScale(5 * DateUtil.ONE_MINUTE, DateLabelFormat.HOUR_MINUTE, 5)
            range > 6 * DateUtil.ONE_MINUTE -> DateTickScale(3 * DateUtil.ONE_MINUTE, DateLabelFormat.HOUR_MINUTE, 3)
            range > 4 * DateUtil.ONE_MINUTE && useFewerLabels ->
                DateTickScale(2 * DateUtil.ONE_MINUTE, DateLabelFormat.HOUR_MINUTE, 2)
            range > 90 * DateUtil.ONE_SECOND -> DateTickScale(DateUtil.ONE_MINUTE, DateLabelFormat.HOUR_MINUTE, 4)
            range > 30 * DateUtil.ONE_SECOND -> DateTickScale(20 * DateUtil.ONE_SECOND, DateLabelFormat.HOUR_MINUTE_SECOND, 4)
            range > 4 * DateUtil.ONE_SECOND -> DateTickScale(10 * DateUtil.ONE_SECOND, DateLabelFormat.HOUR_MINUTE_SECOND, 2)
            else -> DateTickScale(DateUtil.ONE_SECOND, DateLabelFormat.SECOND, 4)
        }

        /**
         * Major-tick increment (in days) for ranges larger than the largest fixed branch: the
         * smallest "nice" year value from the 1-2-5 sequence (…, 200, 500, 1000, 2000, 5000, …) that
         * keeps `range / increment` at about 5 (and so never above 9). At the bottom of its domain
         * (just over 1000 years) this returns 500 years, matching the previous fixed top branch.
         */
        private fun largeYearIncrement(range: Double): Double =
            niceCeil125((range / DateUtil.ONE_YEAR) / 5.0) * DateUtil.ONE_YEAR

        /** Smallest value of the form {1,2,5} x 10^k that is >= [value] (for [value] > 0). */
        private fun niceCeil125(value: Double): Double {
            var pow = 1.0
            while (pow * 10 <= value) pow *= 10
            while (pow > value) pow /= 10
            val normalized = value / pow
            val nice = when {
                normalized <= 1.0 -> 1.0
                normalized <= 2.0 -> 2.0
                normalized <= 5.0 -> 5.0
                else -> 10.0
            }
            return nice * pow
        }

        /** Wraps a kotlinx-datetime format, applying the current system time zone at format time. */
        private fun kotlinxFormatter(format: DateTimeFormat<LocalDateTime>) = DateLabelFormatter { epochMillis ->
            val dateTime = Instant.fromEpochMilliseconds(epochMillis)
                .toLocalDateTime(TimeZone.currentSystemDefault())
            format.format(dateTime)
        }

        @OptIn(FormatStringsInDatetimeFormats::class)
        private fun buildDateFormat(pattern: String): DateTimeFormat<LocalDateTime> = when (pattern) {
            DateLabelFormat.YEAR.pattern -> LocalDateTime.Format { year() }

            DateLabelFormat.SECOND.pattern -> LocalDateTime.Format { second(Padding.NONE) }

            // Fall back to interpreting an arbitrary Unicode pattern supplied via setTickLabelFormatPattern.
            else -> LocalDateTime.Format { byUnicodePattern(pattern) }
        }
    }
}

/**
 * The label format an auto-scaled [DateAxis] uses for a given range. [pattern] is the Unicode
 * date/time pattern fed to the formatter; `HOUR_MINUTE`/`HOUR_MINUTE_SECOND`/`DAY`/`MONTH` are
 * rendered by the locale-aware platform formatter, while `YEAR`/`SECOND` go through kotlinx-datetime.
 */
internal enum class DateLabelFormat(val pattern: String) {
    YEAR("yyyy"),
    MONTH("MMM yyyy"),                  // localized month + year
    DAY("MMM d, yyyy"),                 // localized MEDIUM date
    HOUR_MINUTE("h:mm"),                // localized SHORT time (also used for the hour-scale ticks)
    HOUR_MINUTE_SECOND("h:mm:ss"),      // localized MEDIUM time
    SECOND("s"),
}

/** The auto-scaling result for a [DateAxis]: the major-tick increment (in days), the label format,
 * and the number of minor increments per major tick. Returned by [DateAxis.autoScale]. */
internal data class DateTickScale(
    val majorTickInc: Double,
    val format: DateLabelFormat,
    val minorTickIncNum: Int,
)