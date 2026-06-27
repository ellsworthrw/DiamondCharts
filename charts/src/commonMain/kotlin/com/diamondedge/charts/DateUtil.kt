/**
 * Copyright 2004-2026 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToLong
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

object DateUtil {

    const val ONE_MINUTE = 1.0 / 1440   // .00069
    const val ONE_SECOND = ONE_MINUTE / 60
    const val HALF_HOUR = 30 * ONE_MINUTE
    const val ONE_HOUR = 1.0 / 24   // .0417
    const val ONE_DAY = 1.0
    const val ONE_WEEK = 7.0
    const val ONE_MONTH = 30.417
    const val ONE_YEAR = 365.25

    private const val millisPerDay = (24 * 60 * 60 * 1000).toDouble()

    // Calendar-field codes, used as the low byte of CalculatedData's period-style constants.
    // These replace the values that were previously borrowed from java.util.Calendar.
    internal const val SECOND_FIELD = 1
    internal const val MINUTE_FIELD = 2
    internal const val HOUR_FIELD = 3
    internal const val DAY_FIELD = 4
    internal const val WEEK_FIELD = 5
    internal const val MONTH_FIELD = 6
    internal const val YEAR_FIELD = 7

    fun now(): Double {
        return toDouble(Clock.System.now().toEpochMilliseconds())
    }

    /** returns a double representing the number of days since Jan 1, 1970
     * Note: the fractional part represents the time of day in relation to GMT.
     */
    fun toDouble(timeMillis: Long): Double {
        return timeMillis / millisPerDay
    }

    /** returns the epoch milliseconds (since Jan 1, 1970, 00:00:00 GMT) for the given number of days.
     * Note: the fractional part represents the time of day in relation to GMT.
     */
    fun toDate(days: Double): Long {
        return (days * millisPerDay).roundToLong()
    }

    /** returns number of milliseconds since the standard base time known as "the epoch", namely January 1, 1970, 00:00:00 GMT.
     */
    fun toMillis(date: Double): Long {
        return (date * millisPerDay).toLong()
    }

    fun durationString(duration: Double): String {
        return duration.days.toString()
    }

    /**
     * Returns the distance, in days, from [days] to the next boundary of the given calendar [field]
     * (one of the `*_FIELD` codes). Calendar-aware for WEEK/MONTH/YEAR (honouring month lengths and
     * leap years in the current system time zone); a fixed increment is used otherwise.
     */
    internal fun periodIncrement(days: Double, field: Int): Double {
        if (field != WEEK_FIELD && field != MONTH_FIELD && field != YEAR_FIELD) {
            return if (field == MINUTE_FIELD) ONE_MINUTE else ONE_SECOND
        }
        val tz = TimeZone.currentSystemDefault()
        val instant = Instant.fromEpochMilliseconds(toMillis(days))
        val advanced = when (field) {
            YEAR_FIELD -> instant.plus(1, DateTimeUnit.YEAR, tz)
            MONTH_FIELD -> instant.plus(1, DateTimeUnit.MONTH, tz)
            else -> instant.plus(1, DateTimeUnit.WEEK, tz)
        }
        val truncated = truncateToField(advanced, field, tz)
        return toDouble(truncated.toEpochMilliseconds()) - days
    }

    private fun truncateToField(instant: Instant, field: Int, tz: TimeZone): Instant {
        val ldt = instant.toLocalDateTime(tz)
        return when (field) {
            YEAR_FIELD -> LocalDateTime(ldt.year, 1, 1, 0, 0, 0).toInstant(tz)
            MONTH_FIELD -> LocalDateTime(ldt.year, ldt.month.number, 1, 0, 0, 0).toInstant(tz)
            WEEK_FIELD -> {
                val startOfDay = LocalDateTime(ldt.year, ldt.month.number, ldt.day, 0, 0, 0).toInstant(tz)
                // back up to Sunday to match java.util.Calendar's default first-day-of-week
                val daysFromSunday = ldt.dayOfWeek.isoDayNumber % 7
                startOfDay.minus(daysFromSunday, DateTimeUnit.DAY, tz)
            }
            else -> instant
        }
    }
}
