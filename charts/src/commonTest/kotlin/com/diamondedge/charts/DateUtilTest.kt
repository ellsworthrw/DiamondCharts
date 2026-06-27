/**
 * Copyright 2004-2026 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

/**
 * Tests for [DateUtil], which models date/time as a `Double` number of days since the Unix epoch
 * (the fractional part being the time of day relative to GMT). Runs in `commonTest`, so the same
 * assertions execute on every platform. The calendar-aware [DateUtil.periodIncrement] tests assert
 * only time-zone-independent properties, since it resolves the current system zone at runtime.
 */
class DateUtilTest {

    private val millisPerDay = 86_400_000L
    private val jan1_2021Days = 18628.0
    private val jan1_2021Millis: Long = Instant.parse("2021-01-01T00:00:00Z").toEpochMilliseconds()

    @Test
    fun timeUnitConstantsAreInternallyConsistent() {
        assertEquals(1.0, DateUtil.ONE_DAY, 0.0)
        assertEquals(7.0, DateUtil.ONE_WEEK, 0.0)
        assertEquals(365.25, DateUtil.ONE_YEAR, 0.0)
        assertEquals(30.417, DateUtil.ONE_MONTH, 0.0)
        // A day is 24 hours, 1440 minutes, 86400 seconds.
        assertEquals(1.0, 24 * DateUtil.ONE_HOUR, 1e-12)
        assertEquals(1.0, 1440 * DateUtil.ONE_MINUTE, 1e-12)
        assertEquals(1.0, 86_400 * DateUtil.ONE_SECOND, 1e-12)
        // And the smaller units nest correctly.
        assertEquals(DateUtil.ONE_HOUR, 60 * DateUtil.ONE_MINUTE, 1e-15)
        assertEquals(DateUtil.ONE_MINUTE, 60 * DateUtil.ONE_SECOND, 1e-15)
        assertEquals(DateUtil.ONE_HOUR, 2 * DateUtil.HALF_HOUR, 1e-15)
    }

    @Test
    fun toDouble_convertsMillisToFractionalDays() {
        assertEquals(0.0, DateUtil.toDouble(0L), 0.0)
        assertEquals(1.0, DateUtil.toDouble(millisPerDay), 0.0)
        assertEquals(0.5, DateUtil.toDouble(millisPerDay / 2), 0.0)
        assertEquals(7.0, DateUtil.toDouble(7 * millisPerDay), 0.0)
        assertEquals(-1.0, DateUtil.toDouble(-millisPerDay), 0.0)
    }

    @Test
    fun toDoubleFromMillis() {
        assertEquals(0.0, DateUtil.toDouble(0L), 0.0)
        assertEquals(1.0, DateUtil.toDouble(86_400_000L), 0.0)
        assertEquals(0.5, DateUtil.toDouble(43_200_000L), 0.0)
        assertEquals(jan1_2021Days, DateUtil.toDouble(jan1_2021Millis), 0.0)
    }

    @Test
    fun toMillis() {
        assertEquals(0L, DateUtil.toMillis(0.0))
        assertEquals(86_400_000L, DateUtil.toMillis(1.0))
        assertEquals(jan1_2021Millis, DateUtil.toMillis(jan1_2021Days))
    }

    @Test
    fun millisDaysRoundTrips() {
        assertEquals(jan1_2021Millis, DateUtil.toMillis(DateUtil.toDouble(jan1_2021Millis)))
        assertEquals(jan1_2021Millis, DateUtil.toDate(DateUtil.toDouble(jan1_2021Millis)))
        assertEquals(jan1_2021Days, DateUtil.toDouble(DateUtil.toMillis(jan1_2021Days)), 0.0)
        assertEquals(jan1_2021Days, DateUtil.toDouble(DateUtil.toDate(jan1_2021Days)), 0.0)
    }

    @Test
    fun now() {
        val expected = DateUtil.toDouble(kotlin.time.Clock.System.now().toEpochMilliseconds())
        // now() reads the clock independently; allow up to a minute of slack.
        assertTrue(
            abs(DateUtil.now() - expected) < DateUtil.ONE_MINUTE,
            "now() should be within a minute of the current time",
        )
    }

    @Test
    fun toDate_and_toMillis_convertDaysToMillis() {
        assertEquals(0L, DateUtil.toDate(0.0))
        assertEquals(millisPerDay, DateUtil.toDate(1.0))
        assertEquals(millisPerDay / 2, DateUtil.toDate(0.5))

        assertEquals(0L, DateUtil.toMillis(0.0))
        assertEquals(millisPerDay, DateUtil.toMillis(1.0))
        assertEquals(millisPerDay / 2, DateUtil.toMillis(0.5))
    }

    @Test
    fun toDate_rounds_whileToMillis_truncates() {
        // 2.6 ms expressed in days: toDate rounds to nearest (3), toMillis truncates toward zero (2).
        val twoPointSix = 2.6 / millisPerDay
        assertEquals(3L, DateUtil.toDate(twoPointSix))
        assertEquals(2L, DateUtil.toMillis(twoPointSix))

        // 2.4 ms rounds down to 2 either way.
        val twoPointFour = 2.4 / millisPerDay
        assertEquals(2L, DateUtil.toDate(twoPointFour))
        assertEquals(2L, DateUtil.toMillis(twoPointFour))
    }

    @Test
    fun toDate_isInverseOf_toDouble() {
        for (millis in listOf(0L, millisPerDay, 5 * millisPerDay + 12_345L, 1_700_000_000_000L, -987_654_321L)) {
            assertEquals(millis, DateUtil.toDate(DateUtil.toDouble(millis)), "round trip for $millis ms")
        }
    }

    @Test
    fun now_isWithinASaneRange() {
        // Days since 1970-01-01: ~18262 at 2020, ~84000 around 2200. Just a sanity bound.
        val now = DateUtil.now()
        assertTrue(now in 18_000.0..90_000.0, "now()=$now days is outside the expected range")
    }

    @Test
    fun durationString_rendersDays() {
        assertEquals("0s", DateUtil.durationString(0.0))
        assertEquals("1d", DateUtil.durationString(1.0))
        assertEquals("2d", DateUtil.durationString(2.0))
        assertEquals("7d", DateUtil.durationString(7.0))
        assertEquals("12h", DateUtil.durationString(0.5))
        assertEquals("1d 12h", DateUtil.durationString(1.5))
        // equivalently, it renders a day-count the same way kotlin.time.Duration does
        assertEquals(1.0.days.toString(), DateUtil.durationString(1.0))
    }

    @Test
    fun periodIncrement_fixedFieldsIgnoreTheDateAndReturnFixedUnits() {
        // Anything that isn't WEEK/MONTH/YEAR is a fixed step: a minute for MINUTE, a second otherwise.
        for (days in listOf(0.0, 100.5, 19_000.123)) {
            assertEquals(DateUtil.ONE_MINUTE, DateUtil.periodIncrement(days, DateUtil.MINUTE_FIELD), 0.0)
            assertEquals(DateUtil.ONE_SECOND, DateUtil.periodIncrement(days, DateUtil.SECOND_FIELD), 0.0)
            assertEquals(DateUtil.ONE_SECOND, DateUtil.periodIncrement(days, DateUtil.HOUR_FIELD), 0.0)
            assertEquals(DateUtil.ONE_SECOND, DateUtil.periodIncrement(days, DateUtil.DAY_FIELD), 0.0)
        }
    }

    @Test
    fun periodIncrement_calendarFieldsAdvanceForwardWithinOnePeriod() {
        // For any date, the distance to the next week/month/year boundary is strictly positive and no
        // larger than the length of that period (allowing a little slack for a DST hour). These bounds
        // hold regardless of the system time zone. Sweep a few years of dates at sub-day resolution.
        var days = 18_000.0
        while (days < 20_000.0) {
            val week = DateUtil.periodIncrement(days, DateUtil.WEEK_FIELD)
            val month = DateUtil.periodIncrement(days, DateUtil.MONTH_FIELD)
            val year = DateUtil.periodIncrement(days, DateUtil.YEAR_FIELD)

            assertTrue(week > 0.0 && week <= DateUtil.ONE_WEEK + 0.05, "week increment $week at day $days")
            assertTrue(month > 0.0 && month <= 31.0 + 0.05, "month increment $month at day $days")
            assertTrue(year > 0.0 && year <= 366.0 + 0.05, "year increment $year at day $days")

            days += 0.37
        }
    }

    @Test
    fun periodIncrement_landsOnAStableBoundaryForDatesInTheSamePeriod() {
        // Two instants a few minutes apart fall in the same month and year, so advancing each to the
        // next month/year boundary must arrive at the exact same target (time-zone independent).
        val base = 19_000.25
        val nearby = base + 5 * DateUtil.ONE_MINUTE

        for (field in listOf(DateUtil.MONTH_FIELD, DateUtil.YEAR_FIELD)) {
            val targetFromBase = base + DateUtil.periodIncrement(base, field)
            val targetFromNearby = nearby + DateUtil.periodIncrement(nearby, field)
            assertEquals(targetFromBase, targetFromNearby, 1e-9, "field=$field boundary should match")
        }
    }
}