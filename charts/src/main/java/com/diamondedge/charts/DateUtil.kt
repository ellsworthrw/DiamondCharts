/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import java.util.Calendar
import java.util.Date
import kotlin.time.Duration.Companion.days

object DateUtil {

    const val ONE_MINUTE = 1.0 / 1440   // .00069
    const val ONE_SECOND = ONE_MINUTE / 60
    const val HALF_HOUR = 30 * ONE_MINUTE
    const val ONE_HOUR = 1.0 / 24   // .0417
    const val ONE_DAY = 1.0
    const val ONE_WEEK = 7.0
    const val ONE_MONTH = 30.417
    const val ONE_YEAR = 365.0

    private var zoneOffset = 0
    private val cal = Calendar.getInstance()
    private const val millisPerDay = (24 * 60 * 60 * 1000).toDouble()

    fun now(): Double {
        return toDouble(Date())
    }

    /** returns a double representing the number of days since Jan 1, 1970
     * Note: the fractional part represents the time of day in relation to
     * current timezone.
     */
    fun toDouble(date: Date?): Double {
        return if (date == null) 0.0 else (date.time + zoneOffset) / millisPerDay
        // Jan 1, 1970 = 0
    }

    /** returns a double representing the number of days since Jan 1, 1970
     * Note: the fractional part represents the time of day in relation to
     * current timezone.
     */
    fun toDouble(c: Calendar?): Double {
        return if (c == null) 0.0 else toDouble(c.time)
        // Jan 1, 1970 = 25569 in VB and 0 in Java
    }

    /** returns a double representing the number of days since Jan 1, 1970
     * Note: the fractional part represents the time of day in relation to
     * current timezone.
     */
    fun toDouble(timeMillis: Long): Double {
        return (timeMillis + zoneOffset) / millisPerDay
    }

    /** returns a date for the given day since Jan 1, 1970
     * Note: the fractional part represents the time of day in relation to
     * current timezone.
     */
    fun toDate(days: Double): Date {
        // date = 0 days since Jan 1, 1970
        return Date(Math.round(days * millisPerDay - zoneOffset))
    }

    /** returns a date for the given day since Jan 1, 1970
     * Note: the fractional part represents the time of day in relation to
     * current timezone.
     */
    fun toMillis(date: Double): Long {
        // date = 0 days since Jan 1, 1970
        return (date * millisPerDay).toLong()
    }

    fun setCalendar(cal: Calendar, days: Double) {
        cal.timeInMillis = Math.round(days * millisPerDay - zoneOffset)
    }

    fun clearCalBelow(cal: Calendar, range: Int) {
        var range = range
        if (range == Calendar.WEEK_OF_YEAR) {
            cal.set(Calendar.DAY_OF_WEEK, 1)
            range = Calendar.DAY_OF_MONTH
        }
        when (range) {
            Calendar.YEAR -> {
                cal.set(Calendar.MONTH, 0)
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR, 0)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
            }
            Calendar.MONTH -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR, 0)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
            }
            Calendar.DAY_OF_MONTH -> {
                cal.set(Calendar.HOUR, 0)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
            }
            Calendar.HOUR -> {
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
            }
            Calendar.MINUTE -> cal.set(Calendar.SECOND, 0)
        }
    }

    fun durationString(duration: Double): String {
//        val millis = toMillis(duration)
        return duration.days.toString()
    }

    init {
        zoneOffset = cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET)
    }
}
