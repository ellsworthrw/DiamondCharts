/**
 * Copyright 2004-2026 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
@file:OptIn(ExperimentalWasmJsInterop::class)

package com.diamondedge.charts

/**
 * wasmJs implementation backed by the browser's `Intl.DateTimeFormat`. A `null` [localeTag] passes
 * `undefined` for the locale (the runtime default) and a `null` [timeZoneId] omits the `timeZone`
 * option (the system zone), mirroring `DateFormat.getTimeInstance`/`getDateInstance` on the JVM.
 * Supplying them forces a specific locale/zone (used by tests).
 */
internal actual class LocalizedDateFormatter actual constructor(
    private val localeTag: String?,
    private val timeZoneId: String?,
) {
    actual fun shortTime(epochMillis: Long): String =
        formatShortTime(epochMillis.toDouble(), localeTag, timeZoneId)

    actual fun mediumTime(epochMillis: Long): String =
        formatMediumTime(epochMillis.toDouble(), localeTag, timeZoneId)

    actual fun mediumDate(epochMillis: Long): String =
        formatMediumDate(epochMillis.toDouble(), localeTag, timeZoneId)

    actual fun monthYear(epochMillis: Long): String =
        formatMonthYear(epochMillis.toDouble(), localeTag, timeZoneId)
}

// `Intl.DateTimeFormat`'s locale argument rejects `null` (it would throw), so map a null tag to
// `undefined`; the `timeZone` option is added only when a zone id is supplied.
private fun formatShortTime(epochMillis: Double, locale: String?, tz: String?): String =
    js(
        """(function() {
        var opts = { timeStyle: 'short' };
        if (tz != null) opts.timeZone = tz;
        return new Intl.DateTimeFormat(locale == null ? undefined : locale, opts).format(new Date(epochMillis));
    })()"""
    )

private fun formatMediumTime(epochMillis: Double, locale: String?, tz: String?): String =
    js(
        """(function() {
        var opts = { timeStyle: 'medium' };
        if (tz != null) opts.timeZone = tz;
        return new Intl.DateTimeFormat(locale == null ? undefined : locale, opts).format(new Date(epochMillis));
    })()"""
    )

private fun formatMediumDate(epochMillis: Double, locale: String?, tz: String?): String =
    js(
        """(function() {
        var opts = { dateStyle: 'medium' };
        if (tz != null) opts.timeZone = tz;
        return new Intl.DateTimeFormat(locale == null ? undefined : locale, opts).format(new Date(epochMillis));
    })()"""
    )

private fun formatMonthYear(epochMillis: Double, locale: String?, tz: String?): String =
    js(
        """(function() {
        var opts = { year: 'numeric', month: 'short' };
        if (tz != null) opts.timeZone = tz;
        return new Intl.DateTimeFormat(locale == null ? undefined : locale, opts).format(new Date(epochMillis));
    })()"""
    )