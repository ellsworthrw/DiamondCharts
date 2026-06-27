/**
 * Copyright 2004 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.fn

import com.diamondedge.fn.Statistics.variance
import com.diamondedge.fn.Statistics.varp
import kotlin.math.sqrt

/**
 * Spreadsheet-style statistical helpers operating on `DoubleArray`s, modeled on the Excel worksheet
 * functions of the same names. Unless noted otherwise, an empty input returns `0.0`.
 */
object Statistics {
    /** Returns the sum of [args] (Σ vᵢ), or `0.0` if [args] is empty. */
    fun sum(args: DoubleArray): Double {
        if (args.isEmpty()) return 0.0
        var sum = 0.0
        for (v in args) sum += v
        return sum
    }

    /** Returns the sum of the squares of [args] (Σ vᵢ²), or `0.0` if [args] is empty. */
    fun sumSquares(args: DoubleArray): Double {
        if (args.isEmpty()) return 0.0
        var sum = 0.0
        for (v in args) sum += v * v
        return sum
    }

    /**
     * Returns the product of [args] (Π vᵢ).
     *
     * Note: returns `0.0` (not `1.0`) for an empty array.
     */
    fun product(args: DoubleArray): Double {
        if (args.isEmpty()) return 0.0
        var product = 1.0
        for (v in args) product *= v
        return product
    }

    /**
     * Multiplies the corresponding elements of each vector in [args] and returns the sum of those
     * products — a generalized dot product. With a single vector this is simply its sum; with two
     * vectors it is their dot product (Σ aⱼbⱼ).
     *
     * Every vector must be at least as long as the first (whose length sets how many columns are
     * combined). Returns `0.0` if [args] is empty.
     */
    fun sumProduct(args: Array<DoubleArray>): Double {
        if (args.isEmpty()) return 0.0
        val nvect = args.size
        val nvals = args[0].size
        var sum = 0.0
        for (j in 0 until nvals) {
            var product = 0.0
            for (i in 0 until nvect) {
                product = if (i == 0) args[i][j] else product * args[i][j]
            }
            sum += product
        }
        return sum
    }

    /** Returns the smallest value in [args], or `0.0` if [args] is empty. */
    fun min(args: DoubleArray): Double {
        if (args.isEmpty()) return 0.0
        var min = Double.MAX_VALUE
        for (v in args) if (v < min) min = v
        return min
    }

    /** Returns the largest value in [args], or `0.0` if [args] is empty. */
    fun max(args: DoubleArray): Double {
        if (args.isEmpty()) return 0.0
        var max = -Double.MAX_VALUE
        for (v in args) if (v > max) max = v
        return max
    }

    /**
     * Returns the arithmetic mean of [args].
     *
     * Note: returns `NaN` for an empty array (a `0.0 / 0` division).
     */
    fun average(args: DoubleArray): Double {
        return sum(args) / args.size
    }

    /**
     * Returns the median of [args] — the middle value, or the average of the two middle values when
     * the count is even. Returns `0.0` if [args] is empty.
     *
     * Note: sorts [args] in place as a side effect.
     */
    fun median(args: DoubleArray): Double {
        if (args.isEmpty()) return 0.0
        args.sort()
        return if ((args.size % 2) == 0) {
            // average the 2 middle values
            (args[args.size / 2 - 1] + args[args.size / 2]) / 2
        } else {
            // odd number so return the middle value
            args[args.size / 2]
        }
    }

    /**
     * Returns the 1-based rank of [num] within [args], or `0` if [num] is not present (or [args] is
     * empty). When [ascending] is positive the smallest value ranks 1; otherwise the largest value
     * ranks 1. Equal values share the same rank.
     *
     * Note: sorts [args] in place as a side effect.
     */
    fun rank(num: Double, args: DoubleArray, ascending: Int): Int {
        if (args.isEmpty()) return 0
        args.sort()
        if (ascending > 0) {
            for (i in args.indices) {
                if (num == args[i]) return i + 1
            }
        } else {
            for (i in args.indices.reversed()) {
                if (num == args[i]) return args.size - i
            }
        }
        return 0
    }

    /**
     * Returns the sample standard deviation of [vals] (√[variance], dividing by n − 1).
     *
     * Note: returns `NaN` for fewer than two values.
     */
    fun stdev(vals: DoubleArray): Double {
        return sqrt(variance(vals))
    }

    /**
     * Returns the population standard deviation of [vals] (√[varp], dividing by n).
     *
     * Note: returns `NaN` for an empty array.
     */
    fun stdevp(vals: DoubleArray): Double {
        return sqrt(varp(vals))
    }

    /**
     * Returns the sample variance of [vals], dividing by n − 1.
     *
     * Note: returns `NaN` for fewer than two values (the denominator is zero).
     */
    fun variance(vals: DoubleArray): Double {
        val sum = sum(vals)
        val sumsq = sumSquares(vals)
        val n = vals.size
        return (n * sumsq - sum * sum) / (n * (n - 1))
    }

    /**
     * Returns the population variance of [vals], dividing by n.
     *
     * Note: returns `NaN` for an empty array; `0.0` for a single value.
     */
    fun varp(vals: DoubleArray): Double {
        val sum = sum(vals)
        val sumsq = sumSquares(vals)
        val n = vals.size
        return (n * sumsq - sum * sum) / (n * n)
    }
}