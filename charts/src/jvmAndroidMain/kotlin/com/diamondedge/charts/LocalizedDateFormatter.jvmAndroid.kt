/**
 * Copyright 2004-2026 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** Android + JVM (desktop) implementation backed by `java.text.DateFormat`. */
internal actual class LocalizedDateFormatter actual constructor(
    private val localeTag: String?,
    private val timeZoneId: String?,
) {
    private var locale = currentLocale()
    private var zone = currentZone()
    private var shortTimeFormat = inZone(DateFormat.getTimeInstance(DateFormat.SHORT, locale))
    private var mediumTimeFormat = inZone(DateFormat.getTimeInstance(DateFormat.MEDIUM, locale))
    private var mediumDateFormat = inZone(DateFormat.getDateInstance(DateFormat.MEDIUM, locale))
    private var monthYearFormat = inZone(SimpleDateFormat(monthYearPattern(locale), locale))

    actual fun shortTime(epochMillis: Long): String {
        refreshIfNeeded()
        return shortTimeFormat.format(Date(epochMillis))
    }

    actual fun mediumTime(epochMillis: Long): String {
        refreshIfNeeded()
        return mediumTimeFormat.format(Date(epochMillis))
    }

    actual fun mediumDate(epochMillis: Long): String {
        refreshIfNeeded()
        return mediumDateFormat.format(Date(epochMillis))
    }

    actual fun monthYear(epochMillis: Long): String {
        refreshIfNeeded()
        return monthYearFormat.format(Date(epochMillis))
    }

    /**
     * Rebuild the cached formatters when the resolved locale or time zone has changed. When the
     * formatter is pinned (via [localeTag]/[timeZoneId]) the resolved values never change, so this
     * is effectively a no-op; for the system-default case it tracks runtime locale/zone changes.
     */
    private fun refreshIfNeeded() {
        val curLocale = currentLocale()
        val curZone = currentZone()
        if (curLocale != locale || curZone != zone) {
            locale = curLocale
            zone = curZone
            shortTimeFormat = inZone(DateFormat.getTimeInstance(DateFormat.SHORT, locale))
            mediumTimeFormat = inZone(DateFormat.getTimeInstance(DateFormat.MEDIUM, locale))
            mediumDateFormat = inZone(DateFormat.getDateInstance(DateFormat.MEDIUM, locale))
            monthYearFormat = inZone(SimpleDateFormat(monthYearPattern(locale), locale))
        }
    }

    private fun currentLocale(): Locale =
        if (localeTag != null) Locale.forLanguageTag(localeTag) else Locale.getDefault(Locale.Category.FORMAT)

    private fun currentZone(): TimeZone =
        if (timeZoneId != null) TimeZone.getTimeZone(timeZoneId) else TimeZone.getDefault()

    private fun <T : DateFormat> inZone(format: T): T = format.apply { timeZone = zone }
}

/**
 * A locale-correct abbreviated-month + year pattern (an `"MMM yyyy"`/`"yyyy MMM"`-style
 * `SimpleDateFormat` pattern). Android derives it from the CLDR skeleton `"yMMM"`; the JVM has no
 * skeleton generator, so it falls back to ordering inferred from the locale's medium date pattern.
 */
internal expect fun monthYearPattern(locale: Locale): String