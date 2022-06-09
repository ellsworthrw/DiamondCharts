/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 * @author  Reed Ellsworth
 */
package com.diamondedge.charts.compose

import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Build
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Density
import com.diamondedge.charts.Font
import com.diamondedge.charts.FontFace
import com.diamondedge.charts.FontMetrics
import com.diamondedge.charts.FontStyle
import com.diamondedge.charts.Gradient
import com.diamondedge.charts.GradientType
import com.diamondedge.charts.GraphicsContext
import com.diamondedge.charts.StrokeStyle
import com.diamondedge.charts.Symbol
import kotlin.math.roundToInt

class ComposeGC(private val g: Canvas, private val density: Density) : GraphicsContext {
    private val paint = Paint()
    override val fontMetrics: FontMetrics
        get() = _fontMetrics
    private var _fontMetrics: FontMetrics

    override var font: Font = Font.Default
        set(value) {
            field = value
            textSize = value.size
            paint.asFrameworkPaint().typeface = installTypeface(value)
            _fontMetrics = installFontMetricsFromCurrent(value)
        }

    private var textSize: Float = 0f
        set(value) {
            field = value
            paint.asFrameworkPaint().textSize = dpToPx(value)
        }

    init {
        paint.asFrameworkPaint().typeface = installTypeface(font)
        _fontMetrics = installFontMetrics(font)
        textSize = font.size

        //perform initializations that do not work if done in the ctor
        //    g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    }

    override fun save() {
        g.save()
    }

    override fun restore() {
        g.restore()
    }

    override fun dpToPixel(dp: Float): Int {
        return dpToPx(dp).roundToInt()
    }

    private fun dpToPx(dp: Float): Float {
        return dp * density.density
    }

    private fun calcFontmetrics(): FontMetrics {
        val fm = paint.asFrameworkPaint().fontMetricsInt
        _fontMetrics = AndroidFontMetrics(fm.ascent, fm.descent, fm.leading, fm.top, fm.bottom)
        return _fontMetrics
    }

    /*
    In compose package we could support creating arbitrary typefaces and then creating a font based on it
    Since we will cache the typeface and fontMetrics inside the font, it shouldn't be expensive to have several and swap them in and out
    but we need a graphics context before we can calculate the fontMetrics, so that can be done on first usage
     */

    private fun getFontAttributes(font: Font): Pair<Typeface, FontMetrics> {
        return Pair(installTypeface(font), installFontMetrics(font))
    }

    private fun installFontMetrics(font: Font): FontMetrics {
        return font.fontMetrics ?: run {
            val origTextSize = textSize
            val origTypeface = paint.asFrameworkPaint().typeface
            val typeface = installTypeface(font)
            paint.asFrameworkPaint().textSize = dpToPx(font.size)
            paint.asFrameworkPaint().typeface = typeface
            val fm = calcFontmetrics()
            font.fontMetrics = fm
            paint.asFrameworkPaint().textSize = dpToPx(origTextSize)
            paint.asFrameworkPaint().typeface = origTypeface
            fm
        }
    }

    private fun installFontMetricsFromCurrent(font: Font): FontMetrics {
        return font.fontMetrics ?: run {
            val fm = calcFontmetrics()
            font.fontMetrics = fm
            fm
        }

    }

    private fun installTypeface(font: Font): Typeface {
        return (font.typeface as? Typeface) ?: run {
            val t = createTypeface(font)
            font.typeface = t
            t
        }
    }

    override var color: Long
        get() = paint.color.value.toLong()
        set(value) {
            clearGradient()
            paint.color = Color(value)
        }

    override var stroke: Any
        get() = Stroke(paint.strokeWidth, pathEffect = paint.pathEffect)
        set(value) {
            if (value is Stroke) {
                paint.strokeWidth = value.width
                paint.pathEffect = value.pathEffect
            }
        }

    override fun setStroke(lineWidth: Float, lineStyle: StrokeStyle) {
        stroke = createStroke(lineWidth, lineStyle)
    }

    override fun createStroke(lineWidth: Float, lineStyle: StrokeStyle): Any {
        return when (lineStyle) {
            StrokeStyle.Solid -> Stroke(dpToPx(lineWidth))
            StrokeStyle.Dash -> Stroke(dpToPx(lineWidth), pathEffect = dashEffect)
            StrokeStyle.Dot -> Stroke(dpToPx(lineWidth), pathEffect = dotEffect)
            StrokeStyle.DashDot -> Stroke(dpToPx(lineWidth), pathEffect = dashDotEffect)
            StrokeStyle.DashDotDot -> Stroke(dpToPx(lineWidth), pathEffect = dashDotDotEffect)
        }
    }

    override fun applyGradient(gradient: Gradient, alpha: Float) {
        val colors = gradient.colors.map { (fraction, color) -> Pair(fraction, Color(color)) }
        val brush = when (gradient.style) {
            GradientType.TopToBottom -> Brush.verticalGradient(
                *colors.toTypedArray(),
                startY = gradient.bounds.y,
                endY = gradient.bounds.bottom
            )
            GradientType.Radial -> {
                val (centerX, centerY) = gradient.bounds.center
                Brush.radialGradient(
                    *colors.toTypedArray(),
                    center = Offset(centerX, centerY),
                    radius = maxOf(gradient.bounds.width / 2, gradient.bounds.height / 2)
                )
            }
            GradientType.LeftToRight -> Brush.horizontalGradient(
                *colors.toTypedArray(),
                startX = gradient.bounds.x,
                endX = gradient.bounds.right
            )
        }
        brush.applyTo(Size(gradient.bounds.width, gradient.bounds.height), paint, alpha)
    }

    override fun clearGradient() {
        paint.shader = null
        paint.alpha = 1f
    }

    /**
     * Clip using intersect operation so drawing is only done inside of the given rectangle.
     */
    fun clipRect(x: Int, y: Int, width: Int, height: Int) {
        g.clipRect(x.toFloat(), y.toFloat(), (x + width).toFloat(), (y + height).toFloat(), ClipOp.Intersect)
    }

    /**
     * Clip using difference operation so drawing is only done outside of the given rectangle.
     */
    fun clipOutside(x: Int, y: Int, width: Int, height: Int) {
        g.clipRect(x.toFloat(), y.toFloat(), (x + width).toFloat(), (y + height).toFloat(), ClipOp.Difference)
    }

    override fun stringWidth(str: String): Int {
        return paint.asFrameworkPaint().measureText(str).toInt()
    }

    override fun drawLine(x1: Int, y1: Int, x2: Int, y2: Int) {
        drawLine(x1.toFloat(), y1.toFloat(), x2.toFloat(), y2.toFloat())
    }

    fun drawLine(x1: Float, y1: Float, x2: Float, y2: Float) {
        g.drawLine(Offset(x1, y1), Offset(x2, y2), paint)
    }

    override fun fillRect(x: Int, y: Int, width: Int, height: Int) {
        fillRect(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())
    }

    fun fillRect(x: Float, y: Float, width: Float, height: Float) {
        paint.style = PaintingStyle.Fill
        g.drawRect(x, y, x + width, y + height, paint)
    }

    override fun drawRect(x: Int, y: Int, width: Int, height: Int) {
        drawRect(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())
    }

    fun drawRect(x: Float, y: Float, width: Float, height: Float) {
        paint.style = PaintingStyle.Stroke
        g.drawRect(x, y, x + width, y + height, paint)
    }

    override fun fillRoundedRect(x: Int, y: Int, width: Int, height: Int, radiusX: Int, radiusY: Int) {
        paint.style = PaintingStyle.Fill
        drawRoundedRect(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(), radiusX.toFloat(), radiusY.toFloat())
    }

    override fun drawRoundedRect(x: Int, y: Int, width: Int, height: Int, radiusX: Int, radiusY: Int) {
        paint.style = PaintingStyle.Stroke
        drawRoundedRect(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(), radiusX.toFloat(), radiusY.toFloat())
    }

    private fun drawRoundedRect(x: Float, y: Float, width: Float, height: Float, radiusX: Float, radiusY: Float) {
        g.drawRoundRect(x, y, x + width, y + height, radiusX, radiusY, paint)
    }

    override fun drawOval(x: Int, y: Int, width: Int, height: Int) {
        drawOval(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())
    }

    fun drawOval(x: Float, y: Float, width: Float, height: Float) {
        paint.style = PaintingStyle.Stroke
        g.drawOval(x, y, x + width, y + height, paint)
    }

    override fun fillOval(x: Int, y: Int, width: Int, height: Int) {
        fillOval(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())
    }

    fun fillOval(x: Float, y: Float, width: Float, height: Float) {
        paint.style = PaintingStyle.Fill
        g.drawOval(x, y, x + width, y + height, paint)
    }

    override fun drawArc(x: Int, y: Int, width: Int, height: Int, startAngle: Int, arcAngle: Int) {
        drawArc(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(), startAngle.toFloat(), arcAngle.toFloat())
    }

    fun drawArc(x: Float, y: Float, width: Float, height: Float, startAngle: Float, arcAngle: Float) {
        paint.style = PaintingStyle.Stroke
        g.drawArc(x, y, x + width, y + height, startAngle, arcAngle, true, paint)
    }

    override fun fillArc(x: Int, y: Int, width: Int, height: Int, startAngle: Int, arcAngle: Int) {
        fillArc(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat(), startAngle.toFloat(), arcAngle.toFloat())
    }

    fun fillArc(x: Float, y: Float, width: Float, height: Float, startAngle: Float, arcAngle: Float) {
        paint.style = PaintingStyle.Fill
        g.drawArc(x, y, x + width, y + height, startAngle, arcAngle, true, paint)
    }

    override fun drawPolygon(xPoints: IntArray, yPoints: IntArray, nPoints: Int) {
        paint.style = PaintingStyle.Stroke
        g.drawPath(createPath(xPoints, yPoints, nPoints, true), paint)
    }

    override fun drawPolyline(xPoints: IntArray, yPoints: IntArray, nPoints: Int) {
        paint.style = PaintingStyle.Stroke
        g.drawPath(createPath(xPoints, yPoints, nPoints, false), paint)
    }

    override fun fillPolygon(xPoints: IntArray, yPoints: IntArray, nPoints: Int) {
        paint.style = PaintingStyle.Fill
        val path = createPath(xPoints, yPoints, nPoints, true)
        g.drawPath(path, paint)
    }

    private fun createPath(xPoints: IntArray, yPoints: IntArray, nPoints: Int, closeShape: Boolean): Path {
        val path = Path()
        path.moveTo(xPoints[0].toFloat(), yPoints[0].toFloat())
        for (i in 1 until nPoints) {
            path.lineTo(xPoints[i].toFloat(), yPoints[i].toFloat())
        }
        if (closeShape) {
            path.lineTo(xPoints[0].toFloat(), yPoints[0].toFloat())
        }
        return path
    }

    override fun getFontMetrics(f: Font?): FontMetrics {
        return if (f == null) {
            fontMetrics
        } else {
            var fm = f.fontMetrics
            if (fm != null)
                return fm
            val origFont = font
            font = f
            fm = fontMetrics
            font = origFont
            return fm
        }
    }

    override fun drawString(str: String, x: Int, y: Int) {
        drawString(str, x.toFloat(), y.toFloat())
    }

    fun drawString(str: String, x: Float, y: Float) {
        g.nativeCanvas.drawText(str, x, y, paint.asFrameworkPaint())
    }

    override fun drawStringVertical(str: String, xCenter: Int, yCenter: Int) {
        drawStringVertical(str, xCenter.toFloat(), yCenter.toFloat())
    }

    fun drawStringVertical(str: String, xCenter: Float, yCenter: Float) {
        // print chars on top of each other
        val strlen = str.length
        val fontHeight = dpToPx(font.size)
        val x = xCenter - stringWidth("X") / 2
        var y = yCenter - strlen * fontHeight / 2
        for (i in 0 until strlen) {
            drawString(str.substring(i, i + 1), x, y)
            y += fontHeight
        }
    }

//    fun drawStringVertical(str: String, xCenter: Int, yCenter: Int) {
//        drawString(str, xCenter, yCenter, 270)
//    }

/*
    private fun drawString(str: String, xCenter: Int, yCenter: Int, degrees: Int) {
        if (degrees != 0) {
            val fm = g2.getFontMetrics()
            val orig = g2.getTransform()
            // add to existing transform if exists
            val at = AffineTransform(orig)
            val theta = Math.PI / 180 * degrees
            at.rotate(theta, xCenter, yCenter)
            g2.setTransform(at)
            g2.drawString(str, xCenter - fm.stringWidth(str) / 2, yCenter)
            g2.setTransform(orig)
        } else {
            g2.drawString(str, xCenter, yCenter)
        }
    }
*/

    /**
     * Draws the given [ImageBitmap] into the canvas with its top-left corner at the
     * given [Offset]. The image is composited into the canvas using the given [Paint].
     */
    fun drawImage(image: ImageBitmap, topLeftOffset: Offset) {
        g.drawImage(image, topLeftOffset, paint)
    }

    /**
     * Draws the given [Bitmap] into the canvas with its top-left corner at the
     * given x,y offset. The image is composited into the canvas using the given [Paint].
     */
    fun drawImage(image: ImageBitmap, x: Int, y: Int) {
//        val image = bitmap.asImageBitmap()
        g.drawImage(image, Offset(x.toFloat(), y.toFloat()), paint)
    }

    /**
     * Draws the given image (Bitmap or Symbol) into the canvas with its top-left corner at the
     * given x,y offset.
     */
    override fun drawImage(image: Any, x: Int, y: Int) {
        if (image is Bitmap) {
            val imageBitmap = image.asImageBitmap()
            g.drawImage(imageBitmap, Offset(x.toFloat(), y.toFloat()), paint)
        } else if (image is Symbol) {
            image.draw(this, x, y)
        }
    }

    override fun getImageSize(image: Any): Pair<Int, Int> {
        if (image is Bitmap) {
            return Pair(image.width, image.height)
        } else if (image is Symbol) {
            val size = dpToPixel(image.sizeDp)
            return Pair(size, size)
        }
        return Pair(0, 0)
    }

    companion object {

        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
        val dotEffect = PathEffect.dashPathEffect(floatArrayOf(2f, 2.5f))
        val dashDotEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 5f, 2f, 5f))
        val dashDotDotEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 4f, 2f, 4f, 2f, 4f))

        private fun toAndroidFontStyle(fontStyle: FontStyle): Int {
            return when (fontStyle) {
                FontStyle.Bold -> Typeface.BOLD
                FontStyle.SemiBold -> Typeface.BOLD
                FontStyle.Normal -> Typeface.NORMAL
                FontStyle.Italic -> Typeface.ITALIC
                FontStyle.BoldItalic -> Typeface.BOLD_ITALIC
            }
        }

        private fun toFontStyle(typeface: Typeface): FontStyle {
            return when (typeface.style) {
                Typeface.BOLD_ITALIC -> FontStyle.BoldItalic
                Typeface.BOLD -> FontStyle.Bold
                Typeface.ITALIC -> FontStyle.Italic
                else -> FontStyle.Normal
            }
        }

        fun createFont(typeface: Typeface, size: Float): Font {
            return Font.createNative(typeface, toFontStyle(typeface), size)
        }

        fun createFont(family: Typeface, style: FontStyle, size: Float): Font {
            val typeface = createTypeface(family, style)
            return Font.createNative(typeface, style, size)
        }

        private fun createTypeface(font: Font): Typeface {
            val typeface = when (font.face) {
                FontFace.Serif -> Typeface.SERIF
                FontFace.SansSerif -> Typeface.SANS_SERIF
                FontFace.Monospace -> Typeface.MONOSPACE
                FontFace.Default -> Typeface.DEFAULT
                FontFace.Native -> font.typeface as Typeface
            }
            return createTypeface(typeface, font.style)
        }

        private fun createTypeface(family: Typeface, style: FontStyle): Typeface {
            var style = style
            if (style == FontStyle.SemiBold) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                    return Typeface.create(family, 600, false)
                style = FontStyle.Bold
            }
            return Typeface.create(family, toAndroidFontStyle(style))
        }
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
