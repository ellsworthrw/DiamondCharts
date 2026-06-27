/**
 * Copyright 2004-2026 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round

/**
 * Formats [value] with up to [maxFractionDigits] fractional digits, dropping any trailing zeros.
 * This is the multiplatform replacement for the `java.text.DecimalFormat` patterns `"#"` and `"#.#"`
 * (plus extra `#`) that were previously used for axis tick labels. No grouping separators are emitted.
 */
internal fun formatDecimal(value: Double, maxFractionDigits: Int): String {
    if (value.isNaN()) return "NaN"
    if (value.isInfinite()) return if (value > 0) "∞" else "-∞"
    val neg = value < 0
    val v = abs(value)
    if (maxFractionDigits <= 0) {
        val r = round(v).toLong()
        return if (neg && r != 0L) "-$r" else r.toString()
    }
    val factor = 10.0.pow(maxFractionDigits).toLong()
    val scaled = round(v * factor).toLong()
    val intPart = scaled / factor
    val fracPart = scaled % factor
    val s = if (fracPart == 0L) {
        intPart.toString()
    } else {
        val frac = fracPart.toString().padStart(maxFractionDigits, '0').trimEnd('0')
        if (frac.isEmpty()) intPart.toString() else "$intPart.$frac"
    }
    return if (neg && scaled != 0L) "-$s" else s
}

/**
 * Formats [value] with exactly [digits] fractional digits (trailing zeros kept), the multiplatform
 * equivalent of `String.format("%.${digits}f", value)`. No grouping separators are emitted.
 */
internal fun formatFixed(value: Double, digits: Int): String {
    if (value.isNaN()) return "NaN"
    if (value.isInfinite()) return if (value > 0) "∞" else "-∞"
    val neg = value < 0
    val v = abs(value)
    if (digits <= 0) {
        val r = round(v).toLong()
        return if (neg && r != 0L) "-$r" else r.toString()
    }
    val factor = 10.0.pow(digits).toLong()
    val scaled = round(v * factor).toLong()
    val intPart = scaled / factor
    val fracPart = scaled % factor
    val frac = fracPart.toString().padStart(digits, '0')
    val s = "$intPart.$frac"
    return if (neg && scaled != 0L) "-$s" else s
}
