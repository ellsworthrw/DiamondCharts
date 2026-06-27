/**
 * Copyright 2004-2026 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import kotlin.math.exp
import kotlin.math.ln
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for the auto-scaling logic extracted from `DateAxis.calcMetrics` into the pure
 * [DateAxis.autoScale] function. The function maps a visible axis [range] (in days) to a major-tick
 * increment, label format, and minor-increment count.
 *
 * Design goal being verified, for **all** ranges (sub-second up to a million years):
 *  - the number of major ticks (`range / majorTickInc`) is around 5 and **never exceeds 9**; and
 *  - whenever a range grows enough to push a finer increment past that budget, a coarser increment
 *    (a different `when` branch) takes over — i.e. the chosen increment is monotonic in the range.
 */
class DateAxisAutoScaleTest {

    private val year = DateUtil.ONE_YEAR
    private val month = DateUtil.ONE_MONTH
    private val week = DateUtil.ONE_WEEK
    private val day = DateUtil.ONE_DAY
    private val hour = DateUtil.ONE_HOUR
    private val minute = DateUtil.ONE_MINUTE
    private val second = DateUtil.ONE_SECOND

    /** The full domain swept by the "all ranges" tests: a quarter-second up to a million years. */
    private val rangeSweep: Sequence<Double>
        get() = sequence {
            val lo = ln(second / 4)
            val hi = ln(1_000_000.0 * year)
            val steps = 200_000
            for (i in 0..steps) yield(exp(lo + (hi - lo) * i / steps))
        }

    private fun majorTicks(range: Double, useFewerLabels: Boolean): Double =
        range / DateAxis.autoScale(range, useFewerLabels).majorTickInc

    @Test
    fun neverMoreThanNineMajorTicks_acrossAllRanges() {
        for (useFewerLabels in listOf(false, true)) {
            var maxCount = 0.0
            var maxRange = 0.0
            for (range in rangeSweep) {
                val count = majorTicks(range, useFewerLabels)
                if (count > maxCount) {
                    maxCount = count
                    maxRange = range
                }
                assertTrue(
                    count <= 9.0 + 1e-9,
                    "range=$range days (useFewerLabels=$useFewerLabels) produced $count major ticks (> 9); " +
                            "inc=${DateAxis.autoScale(range, useFewerLabels).majorTickInc}",
                )
            }
            // The design should actually use the label budget (centered ~5), not cap everything at 2-3.
            assertTrue(
                maxCount in 5.0..9.0,
                "max major ticks over all ranges should be ~5-9 but was $maxCount (at range=$maxRange days)",
            )
        }
    }

    @Test
    fun increment_isMonotonicInRange_soLargerRangesGetCoarserClauses() {
        // A larger range must never yield a finer (smaller) increment: this is exactly the
        // "if it would exceed the budget, force a different (coarser) when clause" guarantee.
        for (useFewerLabels in listOf(false, true)) {
            var prevInc = 0.0
            var prevRange = 0.0
            for (range in rangeSweep) {
                val inc = DateAxis.autoScale(range, useFewerLabels).majorTickInc
                assertTrue(
                    inc >= prevInc - 1e-12,
                    "increment shrank from $prevInc to $inc as range grew from $prevRange to $range " +
                            "(useFewerLabels=$useFewerLabels)",
                )
                prevInc = inc
                prevRange = range
            }
        }
    }

    @Test
    fun minorIncrementCount_isAlwaysPositive() {
        for (useFewerLabels in listOf(false, true)) {
            for (range in rangeSweep) {
                val minor = DateAxis.autoScale(range, useFewerLabels).minorTickIncNum
                assertTrue(minor > 0, "minorTickIncNum was $minor for range=$range (useFewerLabels=$useFewerLabels)")
            }
        }
    }

    @Test
    fun useFewerLabels_neverIncreasesTheTickCount() {
        // The flag only ever picks an equal-or-coarser increment, so it can only reduce label count.
        for (range in rangeSweep) {
            val incFewer = DateAxis.autoScale(range, useFewerLabels = true).majorTickInc
            val incNormal = DateAxis.autoScale(range, useFewerLabels = false).majorTickInc
            assertTrue(
                incFewer >= incNormal - 1e-12,
                "useFewerLabels gave a finer increment ($incFewer < $incNormal) at range=$range days",
            )
        }
    }

    @Test
    fun representativeRanges_chooseExpectedIncrementAndFormat() {
        // Locks the range -> (increment, format) table and shows each branch lands near ~5 ticks.
        data class Case(val range: Double, val expectedInc: Double, val expectedFormat: DateLabelFormat)

        val cases = listOf(
            Case(2.0 * second, second, DateLabelFormat.SECOND),
            Case(10.0 * second, 10 * second, DateLabelFormat.HOUR_MINUTE_SECOND),
            Case(45.0 * second, 20 * second, DateLabelFormat.HOUR_MINUTE_SECOND),
            Case(2.0 * minute, minute, DateLabelFormat.HOUR_MINUTE),
            Case(10.0 * minute, 3 * minute, DateLabelFormat.HOUR_MINUTE),
            Case(30.0 * minute, 10 * minute, DateLabelFormat.HOUR_MINUTE),
            Case(55.0 * minute, 15 * minute, DateLabelFormat.HOUR_MINUTE),
            Case(90.0 * minute, DateUtil.HALF_HOUR, DateLabelFormat.HOUR_MINUTE),
            Case(3.0 * hour, hour, DateLabelFormat.HOUR_MINUTE),
            Case(6.0 * hour, 2 * hour, DateLabelFormat.HOUR_MINUTE),
            Case(10.0 * hour, 3 * hour, DateLabelFormat.HOUR_MINUTE),
            Case(18.0 * hour, 6 * hour, DateLabelFormat.HOUR_MINUTE),
            Case(3.0 * day, day, DateLabelFormat.DAY),
            Case(14.0 * day, week, DateLabelFormat.DAY),
            Case(2.0 * month, month, DateLabelFormat.MONTH),
            Case(8.0 * month, 2 * month, DateLabelFormat.MONTH),
            Case(1.5 * year, 6 * month, DateLabelFormat.MONTH),
            Case(3.0 * year, year, DateLabelFormat.YEAR),
            Case(8.0 * year, 2 * year, DateLabelFormat.YEAR),
            Case(20.0 * year, 5 * year, DateLabelFormat.YEAR),
            Case(80.0 * year, 20 * year, DateLabelFormat.YEAR),
            Case(400.0 * year, 100 * year, DateLabelFormat.YEAR),
            Case(1500.0 * year, 500 * year, DateLabelFormat.YEAR),
        )

        for (case in cases) {
            val scale = DateAxis.autoScale(case.range, useFewerLabels = false)
            assertEquals(case.expectedFormat, scale.format, "format for range=${case.range} days")
            assertEquals(case.expectedInc, scale.majorTickInc, 1e-9, "increment for range=${case.range} days")
        }
    }

    @Test
    fun largeRanges_continueTheYearSequenceAndStayWithinBudget() {
        // The dynamic top branch preserves the old fixed 500-year increment up to 2500 years...
        for (years in listOf(1001.0, 1500.0, 2000.0, 2500.0)) {
            assertEquals(
                500 * year,
                DateAxis.autoScale(years * year, useFewerLabels = false).majorTickInc,
                1e-6,
                "range=${years}y should still use a 500-year increment",
            )
        }
        // ...then coarsens along the 1-2-5 sequence just past 2500 years.
        assertEquals(1000 * year, DateAxis.autoScale(2501.0 * year, useFewerLabels = false).majorTickInc, 1e-6)
        assertEquals(2000 * year, DateAxis.autoScale(5001.0 * year, useFewerLabels = false).majorTickInc, 1e-6)

        // Even absurdly large ranges keep the major-tick count within the ~5 budget.
        for (years in listOf(1e4, 1e5, 1e6, 1e9)) {
            val count = majorTicks(years * year, useFewerLabels = false)
            assertTrue(count in 2.0..9.0, "range=${years}y produced $count major ticks")
        }
    }

    @Test
    fun atEachBranchUpperBound_theTickCountIsWithinBudget() {
        // Just below every branch threshold the increment is at its densest; verify that even there
        // the count never exceeds 9 (it tops out at 8, in the day-scale branch).
        val thresholdsInDays = listOf(
            1000 * year, 500 * year, 250 * year, 100 * year, 50 * year, 30 * year, 10 * year,
            5 * year, 2 * year, year, 5 * month, month, 8 * day, day,
            12 * hour, 8 * hour, 4 * hour, 2 * hour, hour,
            50 * minute, 16 * minute, 6 * minute, 90 * second, 30 * second, 4 * second,
        )
        for (useFewerLabels in listOf(false, true)) {
            for (threshold in thresholdsInDays) {
                val justBelow = threshold * (1 - 1e-7)
                val count = majorTicks(justBelow, useFewerLabels)
                assertTrue(
                    count <= 9.0 + 1e-9,
                    "range just below $threshold days produced $count major ticks (useFewerLabels=$useFewerLabels)",
                )
            }
        }
    }
}