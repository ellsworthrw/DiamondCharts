/**
 * Copyright 2004-2026 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Verifies [LocalizedDateFormatter] on every platform. The test source set is `commonTest`, so the
 * exact same assertions run against each actual: `java.text` on JVM/Android, `NSDateFormatter` on
 * iOS, and `Intl.DateTimeFormat` on wasmJs.
 *
 * The formatter is pinned to a fixed locale and the `UTC` zone so the output is deterministic
 * regardless of where the tests run. Four locales with deliberately distinct conventions are
 * covered: en-US (12-hour clock, month-first dates), fr-FR and de-DE (24-hour clock, day-first
 * dates), and ja-JP (24-hour clock, year-first dates, CJK month/year).
 *
 * Time assertions are exact (after [normSpaces], because CLDR 42+ uses a narrow no-break space
 * before AM/PM and the exact space character varies by platform CLDR version). Date assertions
 * check the locale-defining field ordering and tokens rather than exact separators, which keeps
 * them robust across the three formatting engines while still proving each locale renders its own
 * conventions.
 *
 * The class is `open` so the Android host-test source set can run the same cases under Robolectric
 * (see `androidHostTest/.../LocalizedDateFormatterAndroidTest`).
 */
open class LocalizedDateFormatterTest {

    // 2024-01-15 15:45:30 UTC
    private val jan15 = 1705333530000L

    // 2024-07-15 15:45:30 UTC
    private val jul15 = 1721058330000L

    // 2025-01-15 15:45:30 UTC
    private val jan15NextYear = 1736955930000L

    // 2024-01-15 10:20:30 UTC and the same minute at :45 seconds
    private val tenTwenty30 = 1705314030000L
    private val tenTwenty45 = 1705314045000L

    private fun formatter(localeTag: String) =
        LocalizedDateFormatter(localeTag = localeTag, timeZoneId = "UTC")

    @Test
    fun enUS_twelveHourTimeAndMonthFirstDate() {
        val f = formatter("en-US")
        assertEquals("3:45 PM", f.shortTime(jan15).normSpaces())
        assertEquals("3:45:30 PM", f.mediumTime(jan15).normSpaces())

        val date = f.mediumDate(jan15).normSpaces()
        assertContainsInOrder(date, "Jan", "15", "2024")

        val my = f.monthYear(jan15).normSpaces()
        assertTrue(my.contains("Jan"), "monthYear=\"$my\"")
        assertTrue(my.contains("2024"), "monthYear=\"$my\"")
        assertFalse(my.contains("15"), "monthYear should not contain the day: \"$my\"")
    }

    @Test
    fun frFR_twentyFourHourTimeAndDayFirstDate() {
        val f = formatter("fr-FR")
        assertEquals("15:45", f.shortTime(jan15).normSpaces())
        assertEquals("15:45:30", f.mediumTime(jan15).normSpaces())
        assertFalse(f.shortTime(jan15).contains("PM"), "French uses a 24-hour clock")

        val date = f.mediumDate(jan15).normSpaces()
        assertContainsInOrder(date, "15", "janv", "2024")

        val my = f.monthYear(jan15).normSpaces()
        assertTrue(my.contains("janv"), "monthYear=\"$my\"")
        assertTrue(my.contains("2024"), "monthYear=\"$my\"")
    }

    @Test
    fun deDE_twentyFourHourTimeAndDayFirstDate() {
        val f = formatter("de-DE")
        assertEquals("15:45", f.shortTime(jan15).normSpaces())
        assertEquals("15:45:30", f.mediumTime(jan15).normSpaces())

        // German medium date is day-first numeric, e.g. "15.01.2024".
        val date = f.mediumDate(jan15).normSpaces()
        assertContainsInOrder(date, "15", "2024")
        assertTrue(date.startsWith("15"), "German date is day-first: \"$date\"")

        val my = f.monthYear(jan15).normSpaces()
        assertTrue(my.contains("Jan"), "monthYear=\"$my\"")
        assertTrue(my.contains("2024"), "monthYear=\"$my\"")
    }

    @Test
    fun jaJP_twentyFourHourTimeAndYearFirstDate() {
        val f = formatter("ja-JP")
        assertEquals("15:45", f.shortTime(jan15).normSpaces())
        assertEquals("15:45:30", f.mediumTime(jan15).normSpaces())

        // Japanese medium date is year-first, e.g. "2024/01/15".
        val date = f.mediumDate(jan15).normSpaces()
        assertContainsInOrder(date, "2024", "15")

        val my = f.monthYear(jan15).normSpaces()
        assertTrue(my.contains("2024"), "monthYear=\"$my\"")
        assertTrue(my.contains("1月"), "Japanese monthYear should use the CJK month marker: \"$my\"")
    }

    @Test
    fun monthYear_reflectsMonthAndYear() {
        val f = formatter("en-US")
        assertNotEquals(f.monthYear(jan15), f.monthYear(jul15), "different months must differ")
        assertNotEquals(f.monthYear(jan15), f.monthYear(jan15NextYear), "different years must differ")
    }

    @Test
    fun shortTimeOmitsSeconds_mediumTimeIncludesThem() {
        val f = formatter("en-US")
        // Two instants in the same minute but different seconds.
        assertEquals(f.shortTime(tenTwenty30), f.shortTime(tenTwenty45), "short time has no seconds")
        assertNotEquals(f.mediumTime(tenTwenty30), f.mediumTime(tenTwenty45), "medium time shows seconds")
    }

    @Test
    fun timeZoneIsHonored() {
        // The same instant rendered in UTC vs. a +9 zone must produce different wall-clock times.
        val utc = LocalizedDateFormatter(localeTag = "en-US", timeZoneId = "UTC")
        val tokyo = LocalizedDateFormatter(localeTag = "en-US", timeZoneId = "Asia/Tokyo")
        assertNotEquals(utc.shortTime(jan15), tokyo.shortTime(jan15))
    }

    @Test
    fun sameInputProducesSameOutput() {
        val f = formatter("fr-FR")
        assertEquals(f.shortTime(jan15), f.shortTime(jan15))
        assertEquals(f.mediumDate(jan15), f.mediumDate(jan15))
        assertEquals(f.monthYear(jan15), f.monthYear(jan15))
    }

    @Test
    fun systemDefaultFormatterReturnsNonBlankValues() {
        // The production path (no locale/zone pinned) must work on every platform.
        val f = LocalizedDateFormatter()
        assertTrue(f.shortTime(jan15).isNotBlank())
        assertTrue(f.mediumTime(jan15).isNotBlank())
        assertTrue(f.mediumDate(jan15).isNotBlank())
        assertTrue(f.monthYear(jan15).isNotBlank())
    }

    /** Maps the various Unicode spaces (NBSP, narrow NBSP, thin space, figure space) to a plain space. */
    private fun String.normSpaces(): String =
        map { c -> if (c == ' ' || c == ' ' || c == ' ' || c == ' ') ' ' else c }
            .joinToString("")
            .trim()

    /** Asserts each token appears in [s], each strictly after the previous one. */
    private fun assertContainsInOrder(s: String, vararg tokens: String) {
        var from = 0
        var prev = ""
        for (token in tokens) {
            val at = s.indexOf(token, from)
            assertTrue(at >= 0, "expected \"$token\" after \"$prev\" in \"$s\"")
            from = at + token.length
            prev = token
        }
    }
}