/**
 * Copyright 2004-2026 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts.compose

import android.graphics.Paint
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Density
import com.diamondedge.charts.Font
import com.diamondedge.charts.FontMetrics

internal actual class PlatformTextEngine actual constructor(private val density: Density) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    actual fun installFont(font: Font): FontMetrics {
        paint.typeface = createTypeface(font)
        paint.textSize = font.size * density.density
        return font.fontMetrics ?: run {
            val fm = paint.fontMetricsInt
            val metrics = AndroidFontMetrics(fm.ascent, fm.descent, fm.leading, fm.top, fm.bottom)
            font.fontMetrics = metrics
            metrics
        }
    }

    actual fun stringWidth(str: String): Int {
        return paint.measureText(str).toInt()
    }

    actual fun drawText(canvas: Canvas, str: String, x: Float, y: Float, colorArgb: Int) {
        paint.color = colorArgb
        canvas.nativeCanvas.drawText(str, x, y, paint)
    }
}

private class AndroidFontMetrics(
    override val ascent: Int,
    override val descent: Int,
    override val leading: Int,
    override val top: Int,
    override val bottom: Int
) : FontMetrics {

    override val baseline: Int
        get() = -top

    override val height: Int
        get() = -top + bottom

    override fun toString(): String {
        return "AndroidFontMetrics(ascent=$ascent, descent=$descent, leading=$leading, top=$top, bottom=$bottom, height=$height)"
    }
}
