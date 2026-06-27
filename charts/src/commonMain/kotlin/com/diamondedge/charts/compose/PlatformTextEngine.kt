package com.diamondedge.charts.compose

import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.unit.Density
import com.diamondedge.charts.Font
import com.diamondedge.charts.FontMetrics

/**
 * Platform-specific text engine used by [ComposeGC] for typeface resolution, text measurement and
 * text drawing. The shape/path drawing in [ComposeGC] uses multiplatform Compose APIs directly;
 * only text rendering needs a per-platform implementation (android.graphics on Android,
 * org.jetbrains.skia.* on every Skia-backed target).
 */
internal expect class PlatformTextEngine(density: Density) {

    /**
     * Installs [font] as the current typeface and text size, caching the resolved native typeface
     * and metrics into the [Font] instance. Returns the metrics for the installed font.
     */
    fun installFont(font: Font): FontMetrics

    /** Width in pixels of [str] when drawn with the currently installed font. */
    fun stringWidth(str: String): Int

    /**
     * Draws [str] with the baseline of the left-most character at ([x], [y]) using the currently
     * installed font and the given [colorArgb] (packed 0xAARRGGBB).
     */
    fun drawText(canvas: Canvas, str: String, x: Float, y: Float, colorArgb: Int)
}
