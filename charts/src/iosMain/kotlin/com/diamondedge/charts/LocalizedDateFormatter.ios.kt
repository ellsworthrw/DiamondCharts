/**
 * Copyright 2004-2026 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterMediumStyle
import platform.Foundation.NSDateFormatterNoStyle
import platform.Foundation.NSDateFormatterShortStyle
import platform.Foundation.NSLocale
import platform.Foundation.NSTimeZone
import platform.Foundation.currentLocale
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.localTimeZone
import platform.Foundation.timeZoneWithName

/**
 * iOS implementation backed by `NSDateFormatter`. A formatter created with the default initializer
 * uses the current locale and system time zone, so the date/time styles below match
 * `DateFormat.getTimeInstance`/`getDateInstance` on the JVM. [localeTag]/[timeZoneId] override the
 * locale/zone when supplied (used by tests).
 */
internal actual class LocalizedDateFormatter actual constructor(
    localeTag: String?,
    timeZoneId: String?,
) {
    private val nsLocale: NSLocale =
        if (localeTag != null) NSLocale(localeIdentifier = localeTag) else NSLocale.currentLocale()
    private val nsZone: NSTimeZone =
        if (timeZoneId != null) NSTimeZone.timeZoneWithName(timeZoneId) ?: NSTimeZone.localTimeZone()
        else NSTimeZone.localTimeZone()

    private val shortTimeFormat = NSDateFormatter().apply {
        locale = nsLocale
        timeZone = nsZone
        dateStyle = NSDateFormatterNoStyle
        timeStyle = NSDateFormatterShortStyle
    }
    private val mediumTimeFormat = NSDateFormatter().apply {
        locale = nsLocale
        timeZone = nsZone
        dateStyle = NSDateFormatterNoStyle
        timeStyle = NSDateFormatterMediumStyle
    }
    private val mediumDateFormat = NSDateFormatter().apply {
        locale = nsLocale
        timeZone = nsZone
        dateStyle = NSDateFormatterMediumStyle
        timeStyle = NSDateFormatterNoStyle
    }

    // Abbreviated month + year, reordered to suit the locale. The locale must be set before
    // setLocalizedDateFormatFromTemplate so the generated pattern follows it.
    private val monthYearFormat = NSDateFormatter().apply {
        locale = nsLocale
        timeZone = nsZone
        setLocalizedDateFormatFromTemplate("yMMM")
    }

    actual fun shortTime(epochMillis: Long): String = shortTimeFormat.stringFromDate(date(epochMillis))

    actual fun mediumTime(epochMillis: Long): String = mediumTimeFormat.stringFromDate(date(epochMillis))

    actual fun mediumDate(epochMillis: Long): String = mediumDateFormat.stringFromDate(date(epochMillis))

    actual fun monthYear(epochMillis: Long): String = monthYearFormat.stringFromDate(date(epochMillis))

    private fun date(epochMillis: Long): NSDate =
        NSDate.dateWithTimeIntervalSince1970(epochMillis / 1000.0)
}