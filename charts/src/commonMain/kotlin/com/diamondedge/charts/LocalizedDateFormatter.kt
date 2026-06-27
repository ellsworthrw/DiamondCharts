/**
 * Copyright 2004-2026 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

/**
 * Locale-aware date/time formatting for [DateAxis] tick labels. This is the platform-specific
 * replacement for the three localized `java.text` formats that have no common equivalent in
 * kotlinx-datetime:
 *
 * - [shortTime]  ↔ `DateFormat.getTimeInstance(DateFormat.SHORT, locale)`
 * - [mediumTime] ↔ `DateFormat.getTimeInstance(DateFormat.MEDIUM, locale)`
 * - [mediumDate] ↔ `DateFormat.getDateInstance(DateFormat.MEDIUM, locale)`
 * - [monthYear]  ↔ `SimpleDateFormat("MMM yyyy", locale)`
 *
 * Each actual resolves the current locale and renders in the current system time zone, caching the
 * underlying platform formatter (java.text on Android/JVM, NSDateFormatter on iOS, `Intl` on wasmJs).
 *
 * In every method `epochMillis` is milliseconds since the Unix epoch (UTC).
 *
 * @param localeTag an optional BCP-47 language tag (e.g. `"fr-FR"`) to force a specific locale;
 *   `null` (the default, used in production) resolves the current system locale.
 * @param timeZoneId an optional IANA time-zone id (e.g. `"UTC"`) to force a specific zone;
 *   `null` (the default, used in production) renders in the current system time zone.
 *   Both parameters exist so the formatting can be exercised deterministically in tests.
 */
internal expect class LocalizedDateFormatter(
    localeTag: String? = null,
    timeZoneId: String? = null,
) {
    /** Localized short time, e.g. en-US `"3:45 PM"`, fr-FR `"15:45"`. */
    fun shortTime(epochMillis: Long): String

    /** Localized medium time, e.g. en-US `"3:45:30 PM"`, fr-FR `"15:45:30"`. */
    fun mediumTime(epochMillis: Long): String

    /** Localized medium date, e.g. en-US `"Jan 5, 2024"`, fr-FR `"5 janv. 2024"`. */
    fun mediumDate(epochMillis: Long): String

    /** Localized abbreviated month and year, e.g. en-US `"Jan 2024"`, fr-FR `"janv. 2024"`. */
    fun monthYear(epochMillis: Long): String
}