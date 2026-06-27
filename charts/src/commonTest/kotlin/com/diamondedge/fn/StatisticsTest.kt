/**
 * Copyright 2004-2026 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.fn

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for [Statistics], the spreadsheet-style numeric helpers. Runs in `commonTest`, so the same
 * assertions execute on every platform. Each function's empty-input and edge-case contract (as
 * documented on the function) is covered alongside the normal arithmetic.
 */
class StatisticsTest {

    private val eps = 1e-9

    // The classic textbook data set with population variance 4 and mean 5.
    private val classic = doubleArrayOf(2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0)

    @Test
    fun sum_addsAllValues() {
        assertEquals(0.0, Statistics.sum(doubleArrayOf()), 0.0)
        assertEquals(6.0, Statistics.sum(doubleArrayOf(1.0, 2.0, 3.0)), eps)
        assertEquals(0.0, Statistics.sum(doubleArrayOf(-1.0, -2.0, 3.0)), eps)
        assertEquals(5.0, Statistics.sum(doubleArrayOf(5.0)), eps)
    }

    @Test
    fun sumSquares_addsTheSquares() {
        assertEquals(0.0, Statistics.sumSquares(doubleArrayOf()), 0.0)
        assertEquals(14.0, Statistics.sumSquares(doubleArrayOf(1.0, 2.0, 3.0)), eps) // 1+4+9
        assertEquals(25.0, Statistics.sumSquares(doubleArrayOf(-3.0, -4.0)), eps)    // 9+16
    }

    @Test
    fun product_multipliesAllValues_andEmptyIsZero() {
        // Documented quirk: empty product is 0.0, not 1.0.
        assertEquals(0.0, Statistics.product(doubleArrayOf()), 0.0)
        assertEquals(24.0, Statistics.product(doubleArrayOf(2.0, 3.0, 4.0)), eps)
        assertEquals(-6.0, Statistics.product(doubleArrayOf(-2.0, 3.0)), eps)
        assertEquals(0.0, Statistics.product(doubleArrayOf(2.0, 0.0, 5.0)), eps)
        assertEquals(5.0, Statistics.product(doubleArrayOf(5.0)), eps)
    }

    @Test
    fun sumProduct_combinesCorrespondingElements() {
        assertEquals(0.0, Statistics.sumProduct(arrayOf()), 0.0)
        // One vector -> plain sum.
        assertEquals(6.0, Statistics.sumProduct(arrayOf(doubleArrayOf(1.0, 2.0, 3.0))), eps)
        // Two vectors -> dot product: 1*4 + 2*5 + 3*6.
        assertEquals(32.0, Statistics.sumProduct(arrayOf(doubleArrayOf(1.0, 2.0, 3.0), doubleArrayOf(4.0, 5.0, 6.0))), eps)
        // Three vectors: 1*3*5 + 2*4*6.
        assertEquals(63.0, Statistics.sumProduct(arrayOf(doubleArrayOf(1.0, 2.0), doubleArrayOf(3.0, 4.0), doubleArrayOf(5.0, 6.0))), eps)
        // A zero in one column zeroes that column's product.
        assertEquals(6.0, Statistics.sumProduct(arrayOf(doubleArrayOf(2.0, 0.0), doubleArrayOf(3.0, 4.0))), eps)
    }

    @Test
    fun min_findsSmallest_includingNegatives() {
        assertEquals(0.0, Statistics.min(doubleArrayOf()), 0.0)
        assertEquals(1.0, Statistics.min(doubleArrayOf(3.0, 1.0, 2.0)), eps)
        assertEquals(-3.0, Statistics.min(doubleArrayOf(-3.0, -1.0, -2.0)), eps)
        assertEquals(-5.0, Statistics.min(doubleArrayOf(-5.0, 3.0, -2.0)), eps)
        assertEquals(7.0, Statistics.min(doubleArrayOf(7.0)), eps)
    }

    @Test
    fun max_findsLargest_includingAllNegativeArrays() {
        assertEquals(0.0, Statistics.max(doubleArrayOf()), 0.0)
        assertEquals(3.0, Statistics.max(doubleArrayOf(3.0, 1.0, 2.0)), eps)
        // Regression: all-negative arrays must return the actual maximum, not a near-zero value.
        assertEquals(-1.0, Statistics.max(doubleArrayOf(-3.0, -1.0, -2.0)), eps)
        assertEquals(3.0, Statistics.max(doubleArrayOf(-5.0, 3.0, -2.0)), eps)
        assertEquals(7.0, Statistics.max(doubleArrayOf(7.0)), eps)
    }

    @Test
    fun average_isTheArithmeticMean() {
        assertEquals(2.5, Statistics.average(doubleArrayOf(1.0, 2.0, 3.0, 4.0)), eps)
        assertEquals(5.0, Statistics.average(classic.copyOf()), eps)
        assertEquals(5.0, Statistics.average(doubleArrayOf(5.0)), eps)
        assertTrue(Statistics.average(doubleArrayOf()).isNaN(), "average of empty is NaN")
    }

    @Test
    fun median_returnsMiddleOrAverageOfTwoMiddle() {
        assertEquals(0.0, Statistics.median(doubleArrayOf()), 0.0)
        assertEquals(2.0, Statistics.median(doubleArrayOf(3.0, 1.0, 2.0)), eps)          // odd
        assertEquals(2.5, Statistics.median(doubleArrayOf(4.0, 1.0, 3.0, 2.0)), eps)     // even
        assertEquals(5.0, Statistics.median(doubleArrayOf(5.0)), eps)
    }

    @Test
    fun median_sortsItsInputInPlace() {
        // Documented side effect: the caller's array is sorted.
        val data = doubleArrayOf(3.0, 1.0, 2.0)
        Statistics.median(data)
        assertEquals(listOf(1.0, 2.0, 3.0), data.toList())
    }

    @Test
    fun rank_ascending_ranksSmallestAsOne() {
        val data = doubleArrayOf(30.0, 10.0, 20.0)
        assertEquals(1, Statistics.rank(10.0, data.copyOf(), ascending = 1))
        assertEquals(2, Statistics.rank(20.0, data.copyOf(), ascending = 1))
        assertEquals(3, Statistics.rank(30.0, data.copyOf(), ascending = 1))
    }

    @Test
    fun rank_descending_ranksLargestAsOne() {
        val data = doubleArrayOf(30.0, 10.0, 20.0)
        assertEquals(1, Statistics.rank(30.0, data.copyOf(), ascending = 0))
        assertEquals(2, Statistics.rank(20.0, data.copyOf(), ascending = 0))
        assertEquals(3, Statistics.rank(10.0, data.copyOf(), ascending = 0))
    }

    @Test
    fun rank_tiesShareARank_inBothDirections() {
        val data = doubleArrayOf(10.0, 20.0, 20.0, 30.0)
        // One value (30) is greater, so the tied 20s rank 2 descending...
        assertEquals(2, Statistics.rank(20.0, data.copyOf(), ascending = 0))
        // ...and one value (10) is smaller, so they rank 2 ascending as well.
        assertEquals(2, Statistics.rank(20.0, data.copyOf(), ascending = 1))
    }

    @Test
    fun rank_returnsZeroWhenAbsentOrEmpty() {
        assertEquals(0, Statistics.rank(99.0, doubleArrayOf(1.0, 2.0, 3.0), ascending = 1))
        assertEquals(0, Statistics.rank(99.0, doubleArrayOf(1.0, 2.0, 3.0), ascending = 0))
        assertEquals(0, Statistics.rank(1.0, doubleArrayOf(), ascending = 1))
    }

    @Test
    fun variance_population_and_sample() {
        // Classic data set: population variance 4, sample variance 32/7.
        assertEquals(4.0, Statistics.varp(classic.copyOf()), eps)
        assertEquals(32.0 / 7.0, Statistics.variance(classic.copyOf()), eps)
    }

    @Test
    fun standardDeviation_population_and_sample() {
        assertEquals(2.0, Statistics.stdevp(classic.copyOf()), eps)                   // sqrt(4)
        assertEquals(kotlin.math.sqrt(32.0 / 7.0), Statistics.stdev(classic.copyOf()), eps)
    }

    @Test
    fun variance_edgeCases() {
        // Sample variance needs at least two values; fewer gives NaN (zero denominator).
        assertTrue(Statistics.variance(doubleArrayOf(5.0)).isNaN(), "sample var of one value is NaN")
        assertTrue(Statistics.variance(doubleArrayOf()).isNaN(), "sample var of empty is NaN")
        assertTrue(Statistics.stdev(doubleArrayOf(5.0)).isNaN(), "sample stdev of one value is NaN")

        // Population variance of a single value is 0; of an empty array is NaN.
        assertEquals(0.0, Statistics.varp(doubleArrayOf(5.0)), eps)
        assertEquals(0.0, Statistics.stdevp(doubleArrayOf(5.0)), eps)
        assertTrue(Statistics.varp(doubleArrayOf()).isNaN(), "population var of empty is NaN")
    }
}