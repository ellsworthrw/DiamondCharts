package com.diamondedge.chartapp.ui

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToLong

/**
 * Multiplatform replacement for `String.format("%.${digits}f", value)`.
 * Rounds half-up and zero-pads the fractional part. No locale grouping.
 */
fun Double.formatDecimal(digits: Int): String {
    if (isNaN()) return "NaN"
    if (isInfinite()) return if (this > 0) "Inf" else "-Inf"

    val factor = 10.0.pow(digits).toLong()
    val scaled = (abs(this) * factor).roundToLong()
    val intPart = scaled / factor
    val fracPart = scaled % factor

    val sb = StringBuilder()
    if (this < 0 && scaled != 0L) sb.append('-')
    sb.append(intPart)
    if (digits > 0) {
        sb.append('.')
        sb.append(fracPart.toString().padStart(digits, '0'))
    }
    return sb.toString()
}
