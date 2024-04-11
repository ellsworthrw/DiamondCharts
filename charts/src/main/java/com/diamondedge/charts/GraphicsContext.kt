/**
 * Copyright 2004-2022 Reed Ellsworth.. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

/**
 * The `Graphics` class is the base class for
 * all graphics contexts that allow an application to draw onto
 * components that are realized on various devices, as well as
 * onto off-screen images.
 *
 *
 * A `Graphics` object encapsulates state information needed
 * for the basic rendering operations that Java supports.  This
 * state information includes the following properties:
 *
 *
 *
 *  * The `Component` object on which to draw.
 *  * A translation origin for rendering and clipping coordinates.
 *  * The current clip.
 *  * The current color.
 *  * The current font.
 *  * The current logical pixel operation function (XOR or Paint).
 *  * The current XOR alternation color
 * (see [Graphics.setXORMode]).
 *
 *
 *
 * Coordinates are infinitely thin and lie between the pixels of the
 * output device.
 * Operations that draw the outline of a figure operate by traversing
 * an infinitely thin path between pixels with a pixel-sized pen that hangs
 * down and to the right of the anchor point on the path.
 * Operations that fill a figure operate by filling the interior
 * of that infinitely thin path.
 * Operations that render horizontal text render the ascending
 * portion of character glyphs entirely above the baseline coordinate.
 *
 *
 * The graphics pen hangs down and to the right from the path it traverses.
 * This has the following implications:
 *
 *
 *  * If you draw a figure that covers a given rectangle, that
 * figure occupies one extra row of pixels on the right and bottom edges
 * as compared to filling a figure that is bounded by that same rectangle.
 *  * If you draw a horizontal line along the same *y* coordinate as
 * the baseline of a line of text, that line is drawn entirely below
 * the text, except for any descenders.
 *
 *
 * All coordinates that appear as arguments to the methods of this
 * `Graphics` object are considered relative to the
 * translation origin of this `Graphics` object prior to
 * the invocation of the method.
 *
 *
 * All rendering operations modify only pixels which lie within the
 * area bounded by the current clip, which is specified by a [Shape]
 * in user space and is controlled by the program using the
 * `Graphics` object.  This *user clip*
 * is transformed into device space and combined with the
 * *device clip*, which is defined by the visibility of windows and
 * device extents.  The combination of the user clip and device clip
 * defines the *composite clip*, which determines the final clipping
 * region.  The user clip cannot be modified by the rendering
 * system to reflect the resulting composite clip. The user clip can only
 * be changed through the `setClip` or `clipRect`
 * methods.
 * All drawing or writing is done in the current color,
 * using the current paint mode, and in the current font.
 *
 * @version 1.61, 03/23/00
 * @author Sami Shaio
 * @author Arthur van Hoff
 * @see java.awt.Component
 */
interface GraphicsContext {

    /**
     * This graphics context's current color to the specified
     * color. All subsequent graphics operations using this graphics
     * context use this specified color.
     */
    var color: Long

    /**
     * This graphics context's font to the specified font.
     * All subsequent text operations using this graphics context
     * use this font.
     * @param  font   the font.
     */
    var font: Font

    /**
     * The font metrics of the current font.
     * @return the font metrics of this graphics
     * context's current font.
     */
    val fontMetrics: FontMetrics

    /**
     * The `Stroke` object to be used to stroke a
     * path during the rendering process
     */
    var stroke: Any

    /**
     * returns actual pixel size from a device independent pixel size
     */
    fun dpToPixel(dp: Float): Int

    /**
     * Gets the font metrics for the specified font.
     * @return the font metrics for the specified font.
     * @param     f the specified font
     */
    fun getFontMetrics(f: Font?): FontMetrics

    /**
     * Draws a line, using the current color, between the points
     * `(x1,&nbsp;y1)` and `(x2,&nbsp;y2)`
     * in this graphics context's coordinate system.
     * @param   x1  the first point's *x* coordinate.
     * @param   y1  the first point's *y* coordinate.
     * @param   x2  the second point's *x* coordinate.
     * @param   y2  the second point's *y* coordinate.
     */
    fun drawLine(x1: Int, y1: Int, x2: Int, y2: Int)

    /**
     * Fills the specified rectangle.
     * The left and right edges of the rectangle are at
     * `x` and `x&nbsp;+&nbsp;width&nbsp;-&nbsp;1`.
     * The top and bottom edges are at
     * `y` and `y&nbsp;+&nbsp;height&nbsp;-&nbsp;1`.
     * The resulting rectangle covers an area
     * `width` pixels wide by
     * `height` pixels tall.
     * The rectangle is filled using the graphics context's current color.
     * @param         x   the *x* coordinate
     * of the rectangle to be filled.
     * @param         y   the *y* coordinate
     * of the rectangle to be filled.
     * @param         width   the width of the rectangle to be filled.
     * @param         height   the height of the rectangle to be filled.
     */
    fun fillRect(x: Int, y: Int, width: Int, height: Int)

    /**
     * Draws the outline of the specified rectangle.
     * The left and right edges of the rectangle are at
     * `x` and `x&nbsp;+&nbsp;width`.
     * The top and bottom edges are at
     * `y` and `y&nbsp;+&nbsp;height`.
     * The rectangle is drawn using the graphics context's current color.
     * @param         x   the *x* coordinate
     * of the rectangle to be drawn.
     * @param         y   the *y* coordinate
     * of the rectangle to be drawn.
     * @param         width   the width of the rectangle to be drawn.
     * @param         height   the height of the rectangle to be drawn.
     */
    fun drawRect(x: Int, y: Int, width: Int, height: Int)

    fun fillRoundedRect(x: Int, y: Int, width: Int, height: Int, radiusX: Int, radiusY: Int)

    fun drawRoundedRect(x: Int, y: Int, width: Int, height: Int, radiusX: Int, radiusY: Int)

    /**
     * Draws the outline of an oval.
     * The result is a circle or ellipse that fits within the
     * rectangle specified by the `x`, `y`,
     * `width`, and `height` arguments.
     *
     *
     * The oval covers an area that is
     * `width&nbsp;+&nbsp;1` pixels wide
     * and `height&nbsp;+&nbsp;1` pixels tall.
     * @param       x the *x* coordinate of the upper left
     * corner of the oval to be drawn.
     * @param       y the *y* coordinate of the upper left
     * corner of the oval to be drawn.
     * @param       width the width of the oval to be drawn.
     * @param       height the height of the oval to be drawn.
     */
    fun drawOval(x: Int, y: Int, width: Int, height: Int)

    /**
     * Fills an oval bounded by the specified rectangle with the
     * current color.
     * @param       x the *x* coordinate of the upper left corner
     * of the oval to be filled.
     * @param       y the *y* coordinate of the upper left corner
     * of the oval to be filled.
     * @param       width the width of the oval to be filled.
     * @param       height the height of the oval to be filled.
     */
    fun fillOval(x: Int, y: Int, width: Int, height: Int)

    /**
     * Draws the outline of a circular or elliptical arc
     * covering the specified rectangle.
     *
     * The resulting arc begins at `startAngle` and extends
     * for `arcAngle` degrees, using the current color.
     * Angles are interpreted such that 0&nbsp;degrees
     * is at the 3&nbsp;o'clock position.
     * A positive value indicates a counter-clockwise rotation
     * while a negative value indicates a clockwise rotation.
     *
     * The center of the arc is the center of the rectangle whose origin
     * is (*x*,&nbsp;*y*) and whose size is specified by the
     * `width` and `height` arguments.
     *
     * The resulting arc covers an area
     * `width&nbsp;+&nbsp;1` pixels wide
     * by `height&nbsp;+&nbsp;1` pixels tall.
     *
     * The angles are specified relative to the non-square extents of
     * the bounding rectangle such that 45 degrees always falls on the
     * line from the center of the ellipse to the upper right corner of
     * the bounding rectangle. As a result, if the bounding rectangle is
     * noticeably longer in one axis than the other, the angles to the
     * start and end of the arc segment will be skewed farther along the
     * longer axis of the bounds.
     * @param        x the *x* coordinate of the
     * upper-left corner of the arc to be drawn.
     * @param        y the *y*  coordinate of the
     * upper-left corner of the arc to be drawn.
     * @param        width the width of the arc to be drawn.
     * @param        height the height of the arc to be drawn.
     * @param        startAngle the beginning angle.
     * @param        arcAngle the angular extent of the arc,
     * relative to the start angle.
     */
    fun drawArc(
        x: Int, y: Int, width: Int, height: Int,
        startAngle: Int, arcAngle: Int
    )

    /**
     * Fills a circular or elliptical arc covering the specified rectangle.
     *
     * The resulting arc begins at `startAngle` and extends
     * for `arcAngle` degrees.
     * Angles are interpreted such that 0&nbsp;degrees
     * is at the 3&nbsp;o'clock position.
     * A positive value indicates a counter-clockwise rotation
     * while a negative value indicates a clockwise rotation.
     *
     * The center of the arc is the center of the rectangle whose origin
     * is (*x*,&nbsp;*y*) and whose size is specified by the
     * `width` and `height` arguments.
     *
     * The resulting arc covers an area
     * `width&nbsp;+&nbsp;1` pixels wide
     * by `height&nbsp;+&nbsp;1` pixels tall.
     *
     * The angles are specified relative to the non-square extents of
     * the bounding rectangle such that 45 degrees always falls on the
     * line from the center of the ellipse to the upper right corner of
     * the bounding rectangle. As a result, if the bounding rectangle is
     * noticeably longer in one axis than the other, the angles to the
     * start and end of the arc segment will be skewed farther along the
     * longer axis of the bounds.
     * @param        x the *x* coordinate of the
     * upper-left corner of the arc to be filled.
     * @param        y the *y*  coordinate of the
     * upper-left corner of the arc to be filled.
     * @param        width the width of the arc to be filled.
     * @param        height the height of the arc to be filled.
     * @param        startAngle the beginning angle.
     * @param        arcAngle the angular extent of the arc,
     * relative to the start angle.
     */
    fun fillArc(
        x: Int, y: Int, width: Int, height: Int,
        startAngle: Int, arcAngle: Int
    )

    /**
     * Draws a sequence of connected lines defined by
     * arrays of *x* and *y* coordinates.
     * Each pair of (*x*,&nbsp;*y*) coordinates defines a point.
     * The figure is not closed if the first point
     * differs from the last point.
     * @param       xPoints an array of *x* points
     * @param       yPoints an array of *y* points
     * @param       nPoints the total number of points
     */
    fun drawPolyline(xPoints: IntArray, yPoints: IntArray, nPoints: Int)

    /**
     * Draws a sequence of connected lines defined by
     * arrays of *x* and *y* coordinates.
     * Each pair of (*x*,&nbsp;*y*) coordinates defines a point.
     * The figure is not closed if the first point
     * differs from the last point.
     * @param       xPoints an array of *x* points
     * @param       yPoints an array of *y* points
     * @param       startIndex the index into x/yPoints of first point
     * @param       nPoints the total number of points
     */
    fun drawPolyline(xPoints: IntArray, yPoints: IntArray, startIndex: Int, nPoints: Int)

    /**
     * Draws a closed polygon defined by
     * arrays of *x* and *y* coordinates.
     * Each pair of (*x*,&nbsp;*y*) coordinates defines a point.
     *
     * This method draws the polygon defined by `nPoint` line
     * segments, where the first `nPoint&nbsp;-&nbsp;1`
     * line segments are line segments from
     * `(xPoints[i&nbsp;-&nbsp;1],&nbsp;yPoints[i&nbsp;-&nbsp;1])`
     * to `(xPoints[i],&nbsp;yPoints[i])`, for
     * 1&nbsp;&nbsp;*i*&nbsp;&nbsp;`nPoints`.
     * The figure is automatically closed by drawing a line connecting
     * the final point to the first point, if those points are different.
     * @param        xPoints   a an array of `x` coordinates.
     * @param        yPoints   a an array of `y` coordinates.
     * @param        nPoints   a the total number of points.
     */
    fun drawPolygon(xPoints: IntArray, yPoints: IntArray, nPoints: Int)

    /**
     * Fills a closed polygon defined by
     * arrays of *x* and *y* coordinates.
     *
     * This method draws the polygon defined by `nPoint` line
     * segments, where the first `nPoint&nbsp;-&nbsp;1`
     * line segments are line segments from
     * `(xPoints[i&nbsp;-&nbsp;1],&nbsp;yPoints[i&nbsp;-&nbsp;1])`
     * to `(xPoints[i],&nbsp;yPoints[i])`, for
     * 1&nbsp;&nbsp;*i*&nbsp;&nbsp;`nPoints`.
     * The figure is automatically closed by drawing a line connecting
     * the final point to the first point, if those points are different.
     *
     * The area inside the polygon is defined using an
     * even-odd fill rule, also known as the alternating rule.
     * @param        xPoints   a an array of `x` coordinates.
     * @param        yPoints   a an array of `y` coordinates.
     * @param        nPoints   a the total number of points.
     */
    fun fillPolygon(xPoints: IntArray, yPoints: IntArray, nPoints: Int)

    /**
     * Creates a `Stroke` object that can be used to set the stroke.
     */
    fun createStroke(
        lineWidth: Float,
        lineStyle: StrokeStyle = StrokeStyle.Solid,
        curveSmoothing: Boolean = false,
        cornerRadius: Float = 1f
    ): Any

    /**
     * Creates and sets the `stroke` for the graphics context.
     */
    fun setStroke(lineWidth: Float, lineStyle: StrokeStyle)

    /**
     * Draws the text given by the specified string, using this
     * graphics context's current font and color. The baseline of the
     * leftmost character is at position (*x*,&nbsp;*y*) in this
     * graphics context's coordinate system.
     * @param       str      the string to be drawn.
     * @param       x        the *x* coordinate.
     * @param       y        the *y* coordinate.
     */
    fun drawString(str: String, x: Int, y: Int)

    /**
     * Draws the text given by the specified string inside the bounding box specified by *width*, using this
     * graphics context's current font and color. The baseline of the
     * leftmost character is at position (*x*,&nbsp;*y*) in this
     * graphics context's coordinate system. The string will be truncated
     * to fit inside the bounds of x and x + width.
     * @param       str      the string to be drawn.
     * @param       x        the *x* coordinate.
     * @param       y        the *y* coordinate.
     * @param       width    the space available to draw string
     * @param       truncateSuffix    the string appended to the truncated string
     */
    fun drawString(str: String, x: Int, y: Int, width: Int, truncateSuffix: String = "...") {
        var label = str
        if (stringWidth(str) > width) {
            label = truncate(label, width, truncateSuffix)
        }
        drawString(label, x, y)
    }

    fun truncate(str: String, width: Int, suffix: String = "..."): String {
        if (str.length <= suffix.length) {
            return str
        }
        var s = str.substring(0, str.length - suffix.length)
        while (stringWidth(s + suffix) > width) {
            if (s.length <= 1)
                return s + suffix
            s = s.substring(0, s.length - 2)
        }
        return s + suffix
    }

    fun drawStringVertical(str: String, xCenter: Int, yCenter: Int)

    /**
     * measure and return the width in pixels when the given string would be drawn to the canvas.
     */
    fun stringWidth(str: String): Int

    /**
     * Draws the given [image] into the canvas with its top-left corner at the
     * given x,y offset.
     */
    fun drawImage(image: Any, x: Int, y: Int)

    /**
     * Returns the width and height of the given [image]
     */
    fun getImageSize(image: Any): Pair<Int, Int>

    /**
     * The `gradient` object will be used to stroke a
     * path or fill a shape during the rendering process
     */
    fun applyGradient(gradient: Gradient, alpha: Float = 1f)

    /**
     * Remove the previously applied `gradient`
     */
    fun clearGradient()

    fun save()
    fun restore()
}
