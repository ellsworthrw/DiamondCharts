/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.days

/**
 * Regression tests pinning the behavior of the original (pre-KMP) [DateUtil].
 *
 * These exist to validate the KMP migration: the migrated DateUtil must produce
 * identical results. The test bodies are written in `kotlin.time` / `kotlinx.datetime`
 * types (epoch millis, [Instant], [LocalDateTime], Duration) so they can move to
 * `commonTest` largely unchanged. The only place `java.util` appears is inside the
 * private bridge helpers below, which adapt those values to the current Date/Calendar
 * API; when DateUtil is migrated, only those helpers need to change.
 */
class DateUtilTest {

    // Jan 1, 2021 00:00:00 UTC == exactly 18628 days since the epoch.
    private val jan1_2021Millis: Long = Instant.parse("2021-01-01T00:00:00Z").toEpochMilliseconds()
    private val jan1_2021Days = 18628.0

    private val sample = LocalDateTime(2021, 3, 15, 13, 45, 30) // a Monday

    private enum class Field { MINUTE, HOUR, DAY, MONTH, YEAR, WEEK }

    // ---- java.util bridges: the ONLY code here that touches java.util ----

    private fun utcCalendar(epochMillis: Long): java.util.Calendar =
        java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
            .apply { timeInMillis = epochMillis }

    private fun toDoubleOfDate(epochMillis: Long?): Double =
        DateUtil.toDouble(epochMillis?.let { java.util.Date(it) })

    private fun toDoubleOfCalendar(epochMillis: Long?): Double =
        DateUtil.toDouble(epochMillis?.let { utcCalendar(it) })

    private fun toDateEpochMillis(days: Double): Long =
        DateUtil.toDate(days).time

    private fun setCalendarEpochMillis(days: Double): Long =
        utcCalendar(0L).also { DateUtil.setCalendar(it, days) }.timeInMillis

    private fun clearBelow(start: LocalDateTime, field: Field): LocalDateTime {
        val cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
        cal.clear()
        cal.set(start.year, start.monthNumber - 1, start.dayOfMonth, start.hour, start.minute, start.second)
        val calField = when (field) {
            Field.MINUTE -> java.util.Calendar.MINUTE
            Field.HOUR -> java.util.Calendar.HOUR
            Field.DAY -> java.util.Calendar.DAY_OF_MONTH
            Field.MONTH -> java.util.Calendar.MONTH
            Field.YEAR -> java.util.Calendar.YEAR
            Field.WEEK -> java.util.Calendar.WEEK_OF_YEAR
        }
        DateUtil.clearCalBelow(cal, calField)
        return Instant.fromEpochMilliseconds(cal.timeInMillis).toLocalDateTime(TimeZone.UTC)
    }

    // ---- constants ----

    @Test
    fun constants() {
        assertEquals(1.0 / 1440, DateUtil.ONE_MINUTE, 0.0)
        assertEquals(DateUtil.ONE_MINUTE / 60, DateUtil.ONE_SECOND, 0.0)
        assertEquals(30 * DateUtil.ONE_MINUTE, DateUtil.HALF_HOUR, 0.0)
        assertEquals(1.0 / 24, DateUtil.ONE_HOUR, 0.0)
        assertEquals(1.0, DateUtil.ONE_DAY, 0.0)
        assertEquals(7.0, DateUtil.ONE_WEEK, 0.0)
        assertEquals(30.417, DateUtil.ONE_MONTH, 0.0)
        assertEquals(365.25, DateUtil.ONE_YEAR, 0.0)
    }

    // ---- toDouble ----

    @Test
    fun toDoubleFromMillis() {
        assertEquals(0.0, DateUtil.toDouble(0L), 0.0)
        assertEquals(1.0, DateUtil.toDouble(86_400_000L), 0.0)
        assertEquals(0.5, DateUtil.toDouble(43_200_000L), 0.0)
        assertEquals(jan1_2021Days, DateUtil.toDouble(jan1_2021Millis), 0.0)
    }

    @Test
    fun toDoubleFromDate() {
        assertEquals(0.0, toDoubleOfDate(null), 0.0)
        assertEquals(1.0, toDoubleOfDate(86_400_000L), 0.0)
        assertEquals(jan1_2021Days, toDoubleOfDate(jan1_2021Millis), 0.0)
    }

    @Test
    fun toDoubleFromCalendar() {
        assertEquals(0.0, toDoubleOfCalendar(null), 0.0)
        assertEquals(jan1_2021Days, toDoubleOfCalendar(jan1_2021Millis), 0.0)
    }

    // ---- toMillis / toDate ----

    @Test
    fun toMillis() {
        assertEquals(0L, DateUtil.toMillis(0.0))
        assertEquals(86_400_000L, DateUtil.toMillis(1.0))
        assertEquals(jan1_2021Millis, DateUtil.toMillis(jan1_2021Days))
    }

    @Test
    fun toDate() {
        assertEquals(0L, toDateEpochMillis(0.0))
        assertEquals(86_400_000L, toDateEpochMillis(1.0))
        assertEquals(jan1_2021Millis, toDateEpochMillis(jan1_2021Days))
        // sub-day fractions round to the nearest millisecond
        assertEquals(1000L, toDateEpochMillis(DateUtil.ONE_SECOND))
        assertEquals(60_000L, toDateEpochMillis(DateUtil.ONE_MINUTE))
    }

    @Test
    fun millisDaysRoundTrips() {
        assertEquals(jan1_2021Millis, DateUtil.toMillis(DateUtil.toDouble(jan1_2021Millis)))
        assertEquals(jan1_2021Millis, toDateEpochMillis(DateUtil.toDouble(jan1_2021Millis)))
        assertEquals(jan1_2021Days, DateUtil.toDouble(DateUtil.toMillis(jan1_2021Days)), 0.0)
        assertEquals(jan1_2021Days, toDoubleOfDate(toDateEpochMillis(jan1_2021Days)), 0.0)
    }

    // ---- now ----

    @Test
    fun now() {
        val expected = DateUtil.toDouble(Clock.System.now().toEpochMilliseconds())
        // now() reads the clock independently; allow up to a minute of slack.
        assertTrue(
            kotlin.math.abs(DateUtil.now() - expected) < DateUtil.ONE_MINUTE,
            "now() should be within a minute of the current time",
        )
    }

    // ---- setCalendar ----

    @Test
    fun setCalendar() {
        assertEquals(86_400_000L, setCalendarEpochMillis(1.0))
        assertEquals(jan1_2021Millis, setCalendarEpochMillis(jan1_2021Days))
        assertEquals(jan1_2021Days, toDoubleOfCalendar(setCalendarEpochMillis(jan1_2021Days)), 0.0)
    }

    // ---- clearCalBelow ----

    @Test
    fun clearBelowMinute() {
        val r = clearBelow(sample, Field.MINUTE)
        assertEquals(0, r.second)
        assertEquals(45, r.minute)
        assertEquals(13, r.hour)
    }

    @Test
    fun clearBelowHour() {
        val r = clearBelow(sample, Field.HOUR)
        assertEquals(0, r.second)
        assertEquals(0, r.minute)
        assertEquals(13, r.hour)
    }

    @Test
    fun clearBelowDayOfMonth() {
        val r = clearBelow(sample, Field.DAY)
        assertEquals(0, r.second)
        assertEquals(0, r.minute)
        assertEquals(0, r.hour)
        assertEquals(15, r.dayOfMonth)
    }

    @Test
    fun clearBelowMonth() {
        val r = clearBelow(sample, Field.MONTH)
        assertEquals(0, r.second)
        assertEquals(0, r.minute)
        assertEquals(0, r.hour)
        assertEquals(1, r.dayOfMonth)
        assertEquals(3, r.monthNumber)
    }

    @Test
    fun clearBelowYear() {
        val r = clearBelow(sample, Field.YEAR)
        assertEquals(0, r.second)
        assertEquals(0, r.minute)
        assertEquals(0, r.hour)
        assertEquals(1, r.dayOfMonth)
        assertEquals(1, r.monthNumber)
        assertEquals(2021, r.year)
    }

    @Test
    fun clearBelowWeekOfYear() {
        // Clearing to the week sets DAY_OF_WEEK to 1 and zeroes the time of day. The
        // exact resolved day is locale-dependent (firstDayOfWeek), so pin only the
        // stable guarantees: midnight, on the week start at or before the original day.
        val r = clearBelow(sample, Field.WEEK)
        assertEquals(0, r.second)
        assertEquals(0, r.minute)
        assertEquals(0, r.hour)
        assertEquals(3, r.monthNumber)
        assertTrue(r.dayOfMonth <= 15, "should not move forward past the original day")
    }

    // ---- durationString ----

    @Test
    fun durationString() {
        assertEquals("0s", DateUtil.durationString(0.0))
        assertEquals("1d", DateUtil.durationString(1.0))
        assertEquals("7d", DateUtil.durationString(7.0))
        assertEquals("12h", DateUtil.durationString(0.5))
        // equivalently, it renders a day-count the same way kotlin.time.Duration does
        assertEquals(1.0.days.toString(), DateUtil.durationString(1.0))
    }
}