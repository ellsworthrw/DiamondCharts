/**
 * Copyright 2004-2026 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import android.text.format.DateFormat
import java.util.Locale

/** Uses Android's CLDR-backed skeleton generator for a fully locale-correct month + year pattern. */
internal actual fun monthYearPattern(locale: Locale): String =
    DateFormat.getBestDateTimePattern(locale, "yMMM")