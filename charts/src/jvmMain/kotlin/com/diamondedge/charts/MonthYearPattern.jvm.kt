/**
 * Copyright 2004-2026 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import com.ibm.icu.text.DateTimePatternGenerator
import com.ibm.icu.util.ULocale
import java.util.Locale

/**
 * The JDK exposes no CLDR skeleton generator, so we use ICU4J's [DateTimePatternGenerator] — the
 * same engine behind Android's `getBestDateTimePattern` — to turn the `"yMMM"` skeleton into a
 * fully locale-correct abbreviated-month + year pattern (e.g. en `"MMM y"`, ja `"y年M月"`). The
 * result is a standard CLDR pattern that `java.text.SimpleDateFormat` formats as-is.
 */
internal actual fun monthYearPattern(locale: Locale): String =
    DateTimePatternGenerator.getInstance(ULocale.forLocale(locale)).getBestPattern("yMMM")