/**
 * Copyright 2004-2026 Reed Ellsworth. All rights reserved.
 * @author  Reed Ellsworth
 */
package com.diamondedge.charts.compose

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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import com.diamondedge.charts.Font
import com.diamondedge.charts.FontMetrics
import com.diamondedge.charts.Gradient
import com.diamondedge.charts.GradientType
import com.diamondedge.charts.GraphicsContext
import com.diamondedge.charts.StrokeStyle
import com.diamondedge.charts.Symbol
import kotlin.math.roundToInt

class ComposeGC(private val g: Canvas, private val density: Density) : GraphicsContext {
    private val paint = Paint()
    private val textEngine = PlatformTextEngine(density)

    override val fontMetrics: FontMetrics
        get() = _fontMetrics
    private var _fontMetrics: FontMetrics

    override var font: Font = Font.Default
        set(value) {
            field = value
            _fontMetrics = textEngine.installFont(value)
        }

    init {
        _fontMetrics = textEngine.installFont(font)
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

    override fun createStroke(lineWidth: Float, lineStyle: StrokeStyle, curveSmoothing: Boolean, cornerRadius: Float): Any {
        return when (lineStyle) {
            StrokeStyle.Solid -> {
                if (curveSmoothing)
                    Stroke(dpToPx(lineWidth), pathEffect = PathEffect.cornerPathEffect(cornerRadius))
                else
                    Stroke(dpToPx(lineWidth))
            }
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
        return textEngine.stringWidth(str)
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

    override fun drawPolyline(xPoints: IntArray, yPoints: IntArray, startIndex: Int, nPoints: Int) {
        paint.style = PaintingStyle.Stroke
        g.drawPath(createPath(xPoints, yPoints, nPoints, false, startIndex), paint)
    }

    override fun fillPolygon(xPoints: IntArray, yPoints: IntArray, nPoints: Int) {
        paint.style = PaintingStyle.Fill
        val path = createPath(xPoints, yPoints, nPoints, true)
        g.drawPath(path, paint)
    }

    private fun createPath(xPoints: IntArray, yPoints: IntArray, nPoints: Int, closeShape: Boolean, startIndex: Int = 0): Path {
        val path = Path()
        // Guard against empty/short point arrays: drawing zero points is a no-op, not a crash.
        val end = minOf(startIndex + nPoints, xPoints.size, yPoints.size)
        if (nPoints < 1 || startIndex >= end) return path
        path.moveTo(xPoints[startIndex].toFloat(), yPoints[startIndex].toFloat())
        for (i in startIndex + 1 until end) {
            path.lineTo(xPoints[i].toFloat(), yPoints[i].toFloat())
        }
        if (closeShape) {
            path.lineTo(xPoints[startIndex].toFloat(), yPoints[startIndex].toFloat())
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
        textEngine.drawText(g, str, x, y, paint.color.toArgb())
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

    /**
     * Draws the given [ImageBitmap] into the canvas with its top-left corner at the
     * given [Offset]. The image is composited into the canvas using the given [Paint].
     */
    fun drawImage(image: ImageBitmap, topLeftOffset: Offset) {
        g.drawImage(image, topLeftOffset, paint)
    }

    /**
     * Draws the given [ImageBitmap] into the canvas with its top-left corner at the
     * given x,y offset. The image is composited into the canvas using the given [Paint].
     */
    fun drawImage(image: ImageBitmap, x: Int, y: Int) {
        g.drawImage(image, Offset(x.toFloat(), y.toFloat()), paint)
    }

    /**
     * Draws the given image (ImageBitmap or Symbol) into the canvas with its top-left corner at the
     * given x,y offset.
     */
    override fun drawImage(image: Any, x: Int, y: Int) {
        when (image) {
            is ImageBitmap -> g.drawImage(image, Offset(x.toFloat(), y.toFloat()), paint)
            is Symbol -> image.draw(this, x, y)
        }
    }

    override fun getImageSize(image: Any): Pair<Int, Int> {
        return when (image) {
            is ImageBitmap -> Pair(image.width, image.height)
            is Symbol -> {
                val size = dpToPixel(image.sizeDp)
                Pair(size, size)
            }
            else -> Pair(0, 0)
        }
    }

    companion object {
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
        val dotEffect = PathEffect.dashPathEffect(floatArrayOf(2f, 2.5f))
        val dashDotEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 5f, 2f, 5f))
        val dashDotDotEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 4f, 2f, 4f, 2f, 4f))
    }
}
