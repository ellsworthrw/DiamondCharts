/**
 * Copyright 2004-2026 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts.compose

import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Density
import com.diamondedge.charts.FontFace
import com.diamondedge.charts.FontMetrics
import com.diamondedge.charts.FontStyle
import org.jetbrains.skia.Font
import org.jetbrains.skia.FontMgr
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Typeface
import kotlin.math.roundToInt
import com.diamondedge.charts.Font as ChartFont
import org.jetbrains.skia.FontStyle as SkiaFontStyle

internal actual class PlatformTextEngine actual constructor(private val density: Density) {
    private var skFont = Font()

    actual fun installFont(font: ChartFont): FontMetrics {
        val typeface = resolveTypeface(font)
        skFont = Font(typeface, font.size * density.density)
        // matchFamilyStyle can fall back to a regular-weight default typeface (see resolveTypeface).
        // Synthesize bold so weighted labels (e.g. the bold axis-label font) still render bold.
        val wantsBold = font.style == FontStyle.Bold || font.style == FontStyle.BoldItalic ||
                font.style == FontStyle.SemiBold
        if (wantsBold && (typeface == null || typeface.fontStyle.weight < 600)) {
            skFont.isEmboldened = true
        }
        return font.fontMetrics ?: run {
            val m = skFont.metrics
            val metrics = SkiaFontMetrics(m.ascent, m.descent, m.leading, m.top, m.bottom)
            font.fontMetrics = metrics
            metrics
        }
    }

    actual fun stringWidth(str: String): Int {
        return skFont.measureTextWidth(str).roundToInt()
    }

    actual fun drawText(canvas: Canvas, str: String, x: Float, y: Float, colorArgb: Int) {
        val paint = Paint().apply { color = colorArgb }
        canvas.nativeCanvas.drawString(str, x, y, skFont, paint)
    }

    private fun resolveTypeface(font: ChartFont): Typeface? {
        (font.typeface as? Typeface)?.let { return it }
        if (font.face == FontFace.Native) return font.typeface as? Typeface
        // Skia's desktop FontMgr (notably on macOS) returns null for the CSS-generic family names
        // ("sans-serif"/"serif"/"monospace") and for a null family, and its empty-name default
        // typeface has no glyphs. A Font built with such a typeface renders nothing (invisible
        // text). So resolve through concrete family names per genre, then fall back to whatever
        // family the platform actually has, guaranteeing labels are drawn.
        val style = font.style.toSkiaStyle()
        val candidates = when (font.face) {
            FontFace.Serif -> serifFamilies
            FontFace.Monospace -> monospaceFamilies
            else -> sansFamilies   // Default + SansSerif
        }
        val typeface = candidates.firstNotNullOfOrNull { FontMgr.default.matchFamilyStyle(it, style) }
            ?: firstAvailableTypeface(style)
        if (typeface != null)
            font.typeface = typeface
        return typeface
    }

    private fun firstAvailableTypeface(style: SkiaFontStyle): Typeface? {
        val mgr = FontMgr.default
        return if (mgr.familiesCount > 0) mgr.matchFamilyStyle(mgr.getFamilyName(0), style) else null
    }
}

// Concrete family names tried in order for each generic genre (covers macOS / Windows / Linux),
// starting with the CSS-generic name for platforms whose FontMgr does honour it.
private val sansFamilies = listOf(
    "sans-serif", "Helvetica Neue", "Helvetica", "Arial", "Segoe UI", "Roboto",
    "Liberation Sans", "DejaVu Sans", "Noto Sans",
)
private val serifFamilies = listOf(
    "serif", "Times New Roman", "Times", "Georgia",
    "Liberation Serif", "DejaVu Serif", "Noto Serif",
)
private val monospaceFamilies = listOf(
    "monospace", "Menlo", "Consolas", "Courier New", "Courier",
    "Liberation Mono", "DejaVu Sans Mono", "Noto Sans Mono",
)

private fun FontStyle.toSkiaStyle(): SkiaFontStyle = when (this) {
    FontStyle.Bold -> SkiaFontStyle.BOLD
    FontStyle.SemiBold -> SkiaFontStyle.BOLD
    FontStyle.Normal -> SkiaFontStyle.NORMAL
    FontStyle.Italic -> SkiaFontStyle.ITALIC
    FontStyle.BoldItalic -> SkiaFontStyle.BOLD_ITALIC
}

private class SkiaFontMetrics(
    ascentF: Float,
    descentF: Float,
    leadingF: Float,
    topF: Float,
    bottomF: Float
) : FontMetrics {
    override val ascent: Int = ascentF.roundToInt()
    override val descent: Int = descentF.roundToInt()
    override val leading: Int = leadingF.roundToInt()
    override val top: Int = topF.roundToInt()
    override val bottom: Int = bottomF.roundToInt()

    override val baseline: Int
        get() = -top

    override val height: Int
        get() = -top + bottom

    override fun toString(): String {
        return "SkiaFontMetrics(ascent=$ascent, descent=$descent, leading=$leading, top=$top, bottom=$bottom, height=$height)"
    }
}
