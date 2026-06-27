/**
 * Copyright 2004-2026 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import kotlin.test.Test

/**
 * Minimal GraphicsContext so the full chart draw path can run headless in tests. It counts the
 * "data rendering" operations (filled shapes / polylines) so a blank chart — one that runs without
 * error but paints no data — can be detected. It also flags any coordinate that is NaN/Infinite.
 */
private class StubGraphicsContext : GraphicsContext {
    var dataOps = 0
    var badCoords = 0

    private fun check(vararg v: Int) {
        // Int can't be NaN; the real risk is a Double->Int of NaN/Inf upstream, which Kotlin maps to
        // Int.MIN_VALUE/MAX_VALUE. Treat those sentinels as evidence of a NaN/Infinite coordinate.
        for (x in v) if (x == Int.MIN_VALUE || x == Int.MAX_VALUE) badCoords++
    }

    override var color: Long = 0
    override var font: Font = Font.Default
    override val fontMetrics: FontMetrics = StubFontMetrics
    override var stroke: Any = Any()
    override fun dpToPixel(dp: Float): Int = dp.toInt()
    override fun getFontMetrics(f: Font?): FontMetrics = StubFontMetrics
    override fun drawLine(x1: Int, y1: Int, x2: Int, y2: Int) = check(x1, y1, x2, y2)
    override fun fillRect(x: Int, y: Int, width: Int, height: Int) {
        dataOps++; check(x, y, width, height)
    }

    override fun drawRect(x: Int, y: Int, width: Int, height: Int) = check(x, y, width, height)
    override fun fillRoundedRect(x: Int, y: Int, width: Int, height: Int, radiusX: Int, radiusY: Int) {
        dataOps++
    }

    override fun drawRoundedRect(x: Int, y: Int, width: Int, height: Int, radiusX: Int, radiusY: Int) {}
    override fun drawOval(x: Int, y: Int, width: Int, height: Int) {}
    override fun fillOval(x: Int, y: Int, width: Int, height: Int) {
        dataOps++; check(x, y, width, height)
    }

    override fun drawArc(x: Int, y: Int, width: Int, height: Int, startAngle: Int, arcAngle: Int) {}
    override fun fillArc(x: Int, y: Int, width: Int, height: Int, startAngle: Int, arcAngle: Int) {
        dataOps++; check(x, y, width, height)
    }

    override fun drawPolyline(xPoints: IntArray, yPoints: IntArray, nPoints: Int) {
        dataOps++; touchPath(xPoints, yPoints, nPoints)
    }

    override fun drawPolyline(xPoints: IntArray, yPoints: IntArray, startIndex: Int, nPoints: Int) {
        dataOps++; touchPath(xPoints, yPoints, nPoints, startIndex)
    }

    override fun drawPolygon(xPoints: IntArray, yPoints: IntArray, nPoints: Int) {
        dataOps++; touchPath(xPoints, yPoints, nPoints)
    }

    override fun fillPolygon(xPoints: IntArray, yPoints: IntArray, nPoints: Int) {
        dataOps++; touchPath(xPoints, yPoints, nPoints)
    }

    // Mirror ComposeGC.createPath's array indexing so an empty/short point array reproduces the
    // real-platform IndexOutOfBounds crash here instead of being silently ignored.
    private fun touchPath(xPoints: IntArray, yPoints: IntArray, nPoints: Int, startIndex: Int = 0) {
        @Suppress("UNUSED_VARIABLE") val x0 = xPoints[startIndex]
        yPoints[startIndex]
        for (i in startIndex + 1 until startIndex + nPoints) {
            @Suppress("UNUSED_VARIABLE") val xi = xPoints[i]
            yPoints[i]
        }
    }

    override fun createStroke(lineWidth: Float, lineStyle: StrokeStyle, curveSmoothing: Boolean, cornerRadius: Float): Any = Any()
    override fun setStroke(lineWidth: Float, lineStyle: StrokeStyle) {}
    override fun drawString(str: String, x: Int, y: Int) {}
    override fun drawStringVertical(str: String, xCenter: Int, yCenter: Int) {}
    override fun stringWidth(str: String): Int = str.length * 7
    override fun drawImage(image: Any, x: Int, y: Int) {}
    override fun getImageSize(image: Any): Pair<Int, Int> = 1 to 1
    override fun applyGradient(gradient: Gradient, alpha: Float) {}
    override fun clearGradient() {}
    override fun save() {}
    override fun restore() {}

    private object StubFontMetrics : FontMetrics {
        override val top = 14
        override val ascent = 11
        override val descent = 3
        override val bottom = 4
        override val leading = 0
        override val height = 14
        override val baseline = 11
    }
}

/**
 * Reproduction harness for the report that clicking a sample tab sometimes shows a blank chart.
 * Each tab rebuilds a fresh [RandomData], so a "sometimes" failure means certain random values hit
 * an edge case in data/axis calculation or drawing. This runs the full add()+draw() path for each
 * tab's chart many times and fails with the offending data if anything throws.
 */
class SampleDataReproTest {

    private val iterations = 5000

    private fun runManyIndexed(label: String, build: (Int) -> Pair<Charts, ChartData>) {
        repeat(iterations) { i ->
            val (charts, data) = build(i)
            val gc = StubGraphicsContext()
            try {
                charts.draw(gc)
            } catch (t: Throwable) {
                throw AssertionError(
                    "$label threw on iteration $i with data {$data}: ${t::class.simpleName}: ${t.message}",
                    t,
                )
            }
            if (gc.badCoords > 0) {
                throw AssertionError("$label produced ${gc.badCoords} NaN/Infinite coordinates on iteration $i with data {$data}")
            }
        }
    }

    // Canvas sizes Compose may hand the chart, including the tiny/degenerate sizes seen during the
    // first measure passes when navigating to a tab.
    private val sizes = listOf(
        800f to 600f, 360f to 640f, 200f to 120f, 50f to 50f, 10f to 10f, 1f to 1f, 0f to 0f,
    )

    private fun sizeFor(i: Int) = sizes[i % sizes.size]

    @Test
    fun stackedBarChart_neverThrows() = runManyIndexed("StackedBarChart (tab Bar)") { i ->
        val data = RandomData(DefaultData.SIMPLE_SERIES, 3)
        val (w, h) = sizeFor(i)
        val charts = Charts(w, h, Margins.medium)
        charts.add(BarChart(data, isVertical = true, isStacked = true, is100Percent = false))
        charts to data
    }

    @Test
    fun stackedAreaGraph_neverThrows() = runManyIndexed("StackedAreaGraph (tab Area)") { i ->
        val data = RandomData(DefaultData.SIMPLE_SERIES, 5)
        val (w, h) = sizeFor(i)
        val charts = Charts(w, h, Margins.medium)
        charts.add(StackedAreaGraph(data, is100Percent = false))
        charts to data
    }

    // The live compose wrappers rebuild Charts + the chart object on every Canvas redraw but reuse
    // the SAME ChartData instance, so recalc() runs repeatedly on an already-populated table. Replay
    // that: one data object, many redraws, each regenerating random row/series counts.
    private fun runRedraws(label: String, data: ChartData, build: (ChartData) -> Charts) {
        repeat(iterations) { i ->
            val gc = StubGraphicsContext()
            try {
                build(data).draw(gc)
            } catch (t: Throwable) {
                throw AssertionError(
                    "$label threw on redraw $i with data {$data}: ${t::class.simpleName}: ${t.message}",
                    t,
                )
            }
        }
    }

    @Test
    fun stackedBarChart_reusedDataRedraws() = runRedraws("StackedBarChart redraw", RandomData(DefaultData.SIMPLE_SERIES, 3)) { data ->
        Charts(800f, 600f, Margins.medium).apply { add(BarChart(data, isVertical = true, isStacked = true, is100Percent = false)) }
    }

    @Test
    fun stackedAreaGraph_reusedDataRedraws() = runRedraws("StackedAreaGraph redraw", RandomData(DefaultData.SIMPLE_SERIES, 5)) { data ->
        Charts(800f, 600f, Margins.medium).apply { add(StackedAreaGraph(data, is100Percent = false)) }
    }

    @Test
    fun pieChart_reusedDataRedraws() = runRedraws("PieChart redraw", RandomData(DefaultData.SIMPLE_SERIES, 3)) { data ->
        Charts(800f, 600f, Margins.medium).apply { add(PieChart(data)) }
    }

    @Test
    fun pieChart_neverThrows() = runManyIndexed("PieChart (tab Pie)") { i ->
        val data = RandomData(DefaultData.SIMPLE_SERIES, 3)
        val (w, h) = sizeFor(i)
        val charts = Charts(w, h, Margins.medium)
        charts.add(PieChart(data))
        charts to data
    }
}