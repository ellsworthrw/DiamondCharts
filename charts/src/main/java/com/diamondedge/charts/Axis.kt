/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

typealias NumberFormatter = (Double) -> String

open class Axis protected constructor() {

    /**
     * The minimum value of the axis. This is the value of the first major tick mark.
     * If AutoScaling is set to true, this is calculated automatically
     * based on an integral unit before minimum value of the data being displayed or the minValueOverride.
     */
    var minValue: Double
        get() = minVal
        internal set(value) {
            minVal = value
            adjustMinMax()
            calcScale()
        }

    /**
     * The maximum value for the axis. This is the value of the last major tick mark.
     * If AutoScaling is set to true, this is calculated automatically
     * based on the integral unit after maximum value of the data being displayed or the maxValueOverride.
     */
    var maxValue: Double
        get() = maxVal
        internal set(value) {
            maxVal = value
            adjustMinMax()
            calcScale()
        }

    /**
     * Override the maximum data value so it is at least this value.
     * The auto-scaling will pick the next increment larger than the largest of this value and the actual maximum data value.
     */
    var maxValueOverride: Double? = null

    /**
     * Override the minimum data value so it is at most this value.
     * The auto-scaling will pick the next increment smaller than the smallest of this value and the actual minimum data value.
     */
    var minValueOverride: Double? = null

    /**
     * The label drawn along the axis. For a horizontal axis the label is
     * centered below the axis. For a vertical axis it is centered on the left
     * or right of the axis and drawn vertically.
     */
    var axisLabel: String? = null

    /**
     * The color of the label drawn along the axis.
     */
    var axisLabelColor = Color.defaultTextColor

    /**
     * The font used for the label drawn along the axis.
     */
    var axisLabelFont: Font = Font.BoldLarge

    var majorTickFont: Font = Font.Default

    var numberFormatter: NumberFormatter? = null

    /**
     * The minimum margin between the min/max value and the edge of the axis. This value is in the units of the data.
     */
    var minDataMargin = 0.0

    private val majorTickLabelShowing = true
    private var minVal = 0.0
    private var maxVal = 100.0
    protected var scale = 1.0

    /**
     * Set this to a value if the distance between major tick marks should be different from what is calculated based on the data.
     * The value is in the range of values for the data.
     */
    var majorTickIncrement = -1.0

    /**
     * Set this to a value > 0 if the number of ticks between major tick marks should be different from what is calculated based on the data.
     * There will be one less tick mark than there are increments in between a major tick mark.
     */
    var numberMinorIncrements = -1

    /* Returns the spacing in between major tick marks. This is in the same scale as the data.
     * Note this is provided temporarily for GridLines. A more general solution that will work
     * for a DateAxis is needed.
     */
    internal var majorTickInc = 1.0

    /* The number of sections in between the major tick marks.
     * There will be one less minor tick marks drawn.
     */
    internal var minorTickIncNum = 5

    /**
     * Controls whether the value zero should be in the range between the minimum and maximum values.
     */
    var isZeroRequired = false
        set(zeroRequired) {
            field = zeroRequired
            adjustMinMax()
        }

    /**
     * Controls whether the major tick marks are to be drawn.
     */
    var isMajorTickShowing = true

    /**
     * Controls whether the minor tick marks are to be drawn.
     */
    var isMinorTickShowing = true

    /**
     * The color of the major tick marks.
     */
    var majorTickColor = Color.gray50

    /**
     * The color of the minor tick marks.
     */
    var minorTickColor = Color.gray20

    /**
     * The length of the major tick marks.
     */
    var majorTickSize = 6f

    /**
     * The length of the minor tick marks.
     */
    var minorTickSize = 4f

    /**
     * The position relative to the axis that the major tick marks are being drawn.
     * One of: INSIDE, OUTSIDE or CROSS
     */
    var majorTickStyle = TickStyle.Outside

    /**
     * The position relative to the axis that the minor tick marks are being drawn.
     * One of: INSIDE, OUTSIDE or CROSS
     */
    var minorTickStyle = TickStyle.Outside

    val axisLineAttributes = LineAttributes()

    var labelGap = 4f

    /**
     * Allow labels drawn on horizontal axis to alternate vertical positions if the label cannot fit in between tick marks.
     * This labels to not overlap.
     */
    var allow2LabelPositions = true
    private var showAltHeight = -1

    /**
     * screen coordinate (pixels) of the left of the bounding box for the axis
     */
    var x: Int = 0
        protected set

    /**
     * screen coordinate (pixels) of the bottom of the bounding box for the axis
     */
    var y: Int = 0
        protected set

    /**
     * width (pixels) of the bounding box for the axis
     */
    var width: Int = 0
        protected set

    /**
     * height (pixels) of the bounding box for the axis
     */
    var height: Int = 0
        protected set

    var isVertical = true
        internal set

    /**
     * screen coordinate (pixels) corresponding to the 0 data value.
     * The axis will be drawn at this location.
     */
    var xOrigin: Int = 0
        protected set

    /**
     * screen coordinate (pixels) corresponding to the 0 data value.
     * The axis will be drawn at this location.
     */
    var yOrigin: Int = 0
        protected set

    /**
     * Sets the minimum and maximum values of the axis and the majorTickIncrement
     * based on the minimum and maximum values of the data being displayed.
     */
    internal var isAutoScaling = true

    /**
     * make XYGraphs start drawing data at the y-axis.
     * There will not be a buffer of empty space on the left side between the y-axis and the fist data poing.
     * Also, the y-axis will not be an even multiple of the major tick lines. The first major tick line will always be labelled
     *  but if it is too close the y-axis line, the grid line will not be drawn. Too close is governed by the value
     *  GridLines.minDistanceToNextLineDp.
     */
    var startAtMinValue: Boolean = false

    /**
     * make XYGraphs draw data points all the way to the right edge of the graph.
     * There will not be a buffer on the right side.
     */
    var endAtMaxValue: Boolean = false

    /**
     * The position of the label relative to the major tick marks are being drawn.
     * One of: TICK_CENTER, GROUP_CENTER, RIGHT_OF_TICK, BELOW_TICK, ABOVE_TICK
     */
    var majorTickLabelPosition = TickLabelPosition.TickCenter

    var majorTickLabelColor = Color.defaultTextColor

    private var drawCustomLineLabel: ((g: GraphicsContext, x: Int, y: Int, value: Double, axis: Axis) -> Unit)? = null
    private var customLineValue = 0.0

    internal fun setMinMaxData(min: Double, max: Double) {
        minVal = min
        maxVal = max
        adjustMinMax()
        calcScale()
    }

    /**
     * Calculate the scale based on the minimum and maximum values
     */
    private fun calcScale() {
        if (isVertical) {
            calcScale(height)
        } else {    // horizontal axis
            calcScale(width)
        }
    }

    private fun tag(): String = if (isVertical) "VertAxis" else "HorAxis"

    internal open fun calcScale(rangePix: Int): Double {
        if (rangePix == 0)
            return 1.0
        val rangeVal = maxVal - minVal
        scale = rangeVal / rangePix
        if (scale == 0.0)
            scale = 1.0
        return scale
    }

    internal open fun calcMetrics(rangePix: Int, g: GraphicsContext, font: Font? = null) {
        maxValueOverride?.let { overrideValue ->
            if (overrideValue > maxValue)
                maxValue = overrideValue
        }

        minValueOverride?.let { overrideValue ->
            if (overrideValue < minValue)
                minValue = overrideValue
        }

        calcScale(rangePix)
        if (numberMinorIncrements > 0) {
            minorTickIncNum = numberMinorIncrements
        }
        log.d(tag()) { "calcMetrics($rangePix) $this" }
    }

    fun draw(g: GraphicsContext) {
        g.setStroke(1f, StrokeStyle.Solid)
        var fm = g.getFontMetrics(this.majorTickFont)
        val majorTickLen = g.dpToPixel(majorTickSize)
        val minorTickLen = g.dpToPixel(minorTickSize)
        val tickLabelGap = g.dpToPixel(labelGap)
        val roundoff = min(this.maxVal * .0001, .000001)
        showAltHeight = -1
        if (isVertical) {
            // add a small percentage for round-off error
            val maxVal = this.maxVal + roundoff
            var majorTickInc = this.majorTickInc
            var tickPos = minVal
            while (tickPos <= maxVal) {
                majorTickInc = nextMajorIncVal(tickPos, majorTickInc)
                val y = convertToPixel(tickPos)
                if (isMajorTickShowing) {
                    g.font = majorTickFont
                    g.color = majorTickColor
                    drawTick(g, xOrigin, y, majorTickLen, majorTickStyle)

                    if (majorTickLabelShowing) {
                        drawVertLabel(g, tickPos, majorTickInc, majorTickLen, majorTickLabelPosition, fm, majorTickLabelColor)
                    }
                }
                if (isMinorTickShowing) {
                    var minorTickInc = nextMinorIncVal(tickPos, this.majorTickInc / minorTickIncNum)
                    var minorTickPos = tickPos + minorTickInc
                    val nextMajorTickPos = tickPos + majorTickInc - roundoff
                    while (minorTickPos < nextMajorTickPos && minorTickPos <= maxVal) {
                        minorTickInc = nextMinorIncVal(minorTickPos, minorTickInc)
                        g.color = minorTickColor
                        drawTick(g, xOrigin, convertToPixel(minorTickPos), minorTickLen, minorTickStyle)
                        minorTickPos += minorTickInc
                    }
                }
                tickPos += majorTickInc
            }

            axisLabel?.let { label ->
                val tickLabelWidth = getTickLabelMaxWidth(g)
                g.font = this.axisLabelFont
                fm = g.fontMetrics
                val x = this.x - majorTickLen - tickLabelWidth - fm.height / 2 - tickLabelGap
                val y = this.y - height / 2
                g.color = axisLabelColor
                g.drawStringVertical(label, x, y)
            }
        } else {   // horizontal axis

            val maxVal = this.maxVal + roundoff     // add a small percentage for round-off error
            var majorTickInc = this.majorTickInc
            var extraLine = 0
            var tickPos = minVal
            if (startAtMinValue) {
                if (isMajorTickShowing) {       // looks better with major tick line at origin
                    g.color = majorTickColor
                    drawTick(g, x, yOrigin, majorTickLen, majorTickStyle)
                }
                // make tickPos be an exact multiple of majorTickInc just larger than minVal
                val minorTickInc = this.majorTickInc / minorTickIncNum
                tickPos = ceil(minValue / majorTickInc) * majorTickInc
                if ((tickPos - minVal) > minorTickInc) {
                    // there is room to draw minor ticks before first major tick is drown
                    val majorTickPosBeforeOrigin = floor(minValue / majorTickInc) * majorTickInc
                    drawMinorTicks(majorTickPosBeforeOrigin, tickPos, minVal, maxVal, g, minorTickLen)
                }
            }
            while (tickPos <= maxVal) {
                majorTickInc = nextMajorIncVal(tickPos, majorTickInc)

                val x = convertToPixel(tickPos)
                if (isMajorTickShowing) {
                    g.font = this.majorTickFont
                    var showTick = true
                    if (majorTickLabelShowing) {
                        val ex = drawHorLabel(g, tickPos, this.majorTickInc, majorTickLen, majorTickLabelPosition, fm, majorTickLabelColor)
                        if (ex < 0)
                            showTick = false
                        else
                            extraLine += ex
                    }
                    g.color = majorTickColor
                    if (showTick)
                        drawTick(g, x, yOrigin, majorTickLen, majorTickStyle)
                    else
                        drawTick(g, x, yOrigin, minorTickLen, majorTickStyle)
                }
                drawMinorTicks(tickPos, tickPos + majorTickInc - roundoff, tickPos, maxVal, g, minorTickLen)
                tickPos += majorTickInc
            }
            if (drawCustomLineLabel != null) {
                val x = convertToPixel(customLineValue)
                g.color = majorTickColor
                drawTick(g, x, yOrigin, majorTickLen, majorTickStyle)
                drawHorLabel(g, customLineValue, this.majorTickInc, majorTickLen, majorTickLabelPosition, fm, majorTickLabelColor)
            }

            axisLabel?.let { label ->
                g.font = this.axisLabelFont
                fm = g.fontMetrics
                val strwidth = g.stringWidth(label)
//                val tickLabelHeight = fm.height
//                y = yAxis + majorTickLen + tickLabelHeight + fm.height
//                if (extraLine > 0)
//                 tick labels used 2 lines
//                    y += tickLabelHeight - 2
                g.color = axisLabelColor
                g.drawString(label, this.x + width / 2 - strwidth / 2, y)
            }
        }
    }

    private fun drawMinorTicks(startPos: Double, endPos: Double, minVal: Double, maxVal: Double, g: GraphicsContext, minorTickLen: Int) {
        if (isMinorTickShowing) {
            g.color = minorTickColor
            var minorTickInc = nextMinorIncVal(startPos, this.majorTickInc / minorTickIncNum)
            var minorTickPos = startPos + minorTickInc
            while (minorTickPos < endPos && minorTickPos <= maxVal) {
                if (minorTickPos >= minVal)
                    drawTick(g, convertToPixel(minorTickPos), yOrigin, minorTickLen, minorTickStyle)
                minorTickInc = nextMinorIncVal(minorTickPos, minorTickInc)
                minorTickPos += minorTickInc
            }
        }
    }

    internal open fun nextMinorIncVal(pos: Double, incVal: Double): Double {
        return incVal
    }

    internal open fun nextMajorIncVal(pos: Double, incrementVal: Double): Double {
        return incrementVal
    }

    private fun drawTick(g: GraphicsContext, x: Int, y: Int, tickLen: Int, tickStyle: TickStyle) {
        var beginOffset = 0
        var endOffset = tickLen
        if (tickStyle == TickStyle.Inside) {
            beginOffset = tickLen
            endOffset = 0
        } else if (tickStyle == TickStyle.Cross) {
            beginOffset = tickLen + 1
            endOffset = tickLen + 1
        }
        if (isVertical)
            g.drawLine(x + beginOffset, y, x - endOffset, y)
        else
            g.drawLine(x, y - beginOffset, x, y + endOffset)
    }

    internal fun extraHorLabelHeight(g: GraphicsContext, width: Int): Int {
        calcScale(width)
        val unitWidth = scaleData(majorTickInc)
        val label = tickLabel(minVal)
        val strWidth = g.stringWidth(label)
        if (allow2LabelPositions && strWidth > unitWidth / 2) {
            return g.fontMetrics.height
        }
        return 0
    }

    private fun drawHorLabel(
        g: GraphicsContext, tickPos: Double, tickInc: Double, tickLen: Int,
        align: TickLabelPosition, fm: FontMetrics, color: Long
    ): Int {
        var extraLine = 0
        val unitWidth = scaleData(tickInc)
        var label = tickLabel(tickPos)
        var strWidth = g.stringWidth(label)
        val tickLabelGap = g.dpToPixel(labelGap)
        var x = convertToPixel(tickPos)
        var y = yOrigin + tickLabelGap + fm.baseline

        if (allow2LabelPositions && showAltHeight < 0 && strWidth > unitWidth / 2)
            showAltHeight = 1

        if (showAltHeight == 1) {
            y += fm.height
            if (align != TickLabelPosition.GroupCenter)
                g.drawLine(x, y + fm.top, x, yOrigin)
            extraLine = 1
        }
        if (allow2LabelPositions)
            showAltHeight = if (showAltHeight == 0) 1 else if (showAltHeight > 0) 0 else -1

        g.color = color

        val maxStrWidth = if (showAltHeight < 0) unitWidth else unitWidth * 3 / 2
        if (strWidth > maxStrWidth) {
            val s = g.truncate(label, maxStrWidth)
            strWidth = g.stringWidth(s)
        }

        when (align) {
            TickLabelPosition.GroupCenter -> {
                x += unitWidth / 2
                x -= strWidth / 2
                y += tickLen
            }
            TickLabelPosition.TickCenter -> {
                x -= strWidth / 2
                y += tickLen
            }
            TickLabelPosition.RightOfTick -> x += tickLabelGap
            TickLabelPosition.BottomRightOfTick -> {
                x += tickLabelGap
                y += tickLen - tickLabelGap
            }
            else -> {}
        }

        if (drawCustomLineLabel != null) {
            if (tickPos == customLineValue) {
                drawCustomLineLabel?.invoke(g, x, y, tickPos, this)
            } else if (abs(convertToPixel(customLineValue) - x) > g.dpToPixel(4f) + strWidth) {   // don't draw label if too close to custom label
                g.drawString(label, x, y, maxStrWidth)
            } else {
                return -1
            }
        } else {
            g.drawString(label, x, y, maxStrWidth)
        }
        return extraLine
    }

    private fun drawVertLabel(
        g: GraphicsContext, tickPos: Double, tickInc: Double, tickLen: Int, align: TickLabelPosition, fm: FontMetrics, color: Long
    ) {
        val label = tickLabel(tickPos)
        val strWidth = g.stringWidth(label)
        g.color = color
        val tickLabelGap = g.dpToPixel(labelGap)
        val x = xOrigin - strWidth - tickLabelGap
        val y = convertToPixel(tickPos)

        when (align) {
            TickLabelPosition.GroupCenter -> g.drawString(label, x - tickLen, convertToPixel(tickPos + tickInc / 2) - fm.ascent / 2)
            TickLabelPosition.TickCenter -> g.drawString(label, x - tickLen, y - fm.ascent / 2)
            TickLabelPosition.AboveTick -> g.drawString(label, x, y - tickLabelGap)
            TickLabelPosition.BelowTick -> g.drawString(label, x, y + fm.baseline)
            else -> {}
        }
    }

    /**
     * Return the screen coordinate (in pixels) based on a data value
     */
    fun convertToPixel(dataValue: Double): Int {
        var value = scaleData(dataValue - minVal)
        if (isVertical)
            value = y - value
        else
            value += x
        return value
    }

    /**
     * Return the data value corresponding to the screen coordinate (in pixels)
     */
    fun convertToValue(pixelValue: Int): Double {
        val minValPixel = convertToPixel(minVal)
        return minVal + scalePixel(abs(pixelValue - minValPixel))
    }

    /**
     * Return the data value scaled to be in pixels (screen coordinates)
     */
    open fun scaleData(dataValue: Double): Int {
        // converting to long first causes this not to overflow to Int.MAX_VALUE
        return (dataValue / scale).toLong().toInt()
    }

    protected open fun scalePixel(pixelValue: Int): Double {
        return pixelValue * scale
    }

    /**
     * set location of origin of axis which is bottom left screen coordinate.
     */
    fun setBounds(x: Int, y: Int, width: Int, height: Int) {
        log.d(tag()) { "setBounds($x, $y, $width, $height)" }
        this.x = x
        this.y = y
        this.width = width
        this.height = height
        xOrigin = x
        yOrigin = y
        calcScale()
    }

    internal fun getTickLabelMaxWidth(g: GraphicsContext): Int {
        calcScale()
        var s = tickLabel(maxVal)
        if (tickLabel(0.0).length > s.length)
            s = tickLabel(0.0)
        return g.stringWidth(s)
    }

    internal fun getThickness(g: GraphicsContext): Int {
        val fm = g.getFontMetrics(this.majorTickFont)
        val fontHeight = fm.height
        val tickLabelMaxWidth = getTickLabelMaxWidth(g)
        val tickLength = g.dpToPixel(majorTickSize)
        val gap = g.dpToPixel(labelGap) * 2
        var thickness = 0
        val calcLabelPlusTick = when (majorTickLabelPosition) {
            TickLabelPosition.TickCenter -> true
            TickLabelPosition.GroupCenter -> true
            TickLabelPosition.BottomRightOfTick -> true
            else -> false
        }

        if (calcLabelPlusTick) {
            if (isVertical)
                thickness += tickLabelMaxWidth + gap + tickLength
            else
                thickness += fontHeight + gap + tickLength
        } else {
            if (isVertical)
                thickness += maxOf(tickLabelMaxWidth + gap, tickLength)
            else
                thickness += maxOf(fontHeight + gap, tickLength)
        }
        if (axisLabel != null) {
            thickness += g.getFontMetrics(axisLabelFont).height + gap
        }
        return thickness
    }

    open fun tickLabel(value: Double): String {
        return numberFormatter?.invoke(value) ?: value.toString()
    }

    internal fun setLabelPosition(value: TickLabelPosition) {
        majorTickLabelPosition = value
    }

    /**
     * Draw a vertical grid line and label at the x coordinate specified by `value`. The `drawCustomLabel` lambda will be
     * called with x, y of the baseline of where the label should be drawn.
     */
    fun setupCustomLine(
        value: Double,
        charts: Charts,
        drawCustomLabel: (g: GraphicsContext, x: Int, y: Int, value: Double, axis: Axis) -> Unit
    ) {
        drawCustomLineLabel = drawCustomLabel
        customLineValue = value
        charts.gridLines.customLine.isVisible = true
        charts.gridLines.customLineValue = value
    }

    protected open fun toStringParam(): String {
        return "vert=$isVertical,scale=${dbl(scale)},min=${dbl(minVal)},max=${dbl(maxVal)},majorTickInc=${dbl(majorTickInc)},minorTickNum=" +
                "$minorTickIncNum, xOrigin=$xOrigin, yOrigin=$yOrigin,axisLabel=$axisLabel,left=$x,bottom=$y,width=$width,height=$height"
    }

    private fun dbl(d: Double): String {
        var str = String.format("%.2f", d)
        if (str.endsWith("00"))
            str = String.format("%.6f", d)
        if (str.endsWith("000000"))
            str = String.format("%.1f", d)
        return str
    }

    override fun toString(): String {
        return "Axis[" + toStringParam() + "]"
    }

    internal open fun adjustMinMax() {
        if (isZeroRequired) {
            if (minVal > 0)
                minVal = 0.0
            if (maxVal < 0)
                maxVal = 0.0
        }
    }

    internal fun adjustOriginPosition(zeroPosition: Int?) {
        log.d { "adjustOriginPosition($zeroPosition)" }
        if (zeroPosition != null) {
            if (isVertical)
                xOrigin = zeroPosition
            else
                yOrigin = zeroPosition
        }
        log.d { "vert=$isVertical, xOrigin=$xOrigin, yOrigin=$yOrigin,left=$x,bottom=$y,width=$width,height=$height" }
    }

    companion object {
        private val log = moduleLogging()
    }
}

enum class TickStyle {
    Inside, Outside, Cross
}

enum class TickLabelPosition {
    TickCenter, GroupCenter, RightOfTick, BottomRightOfTick, BelowTick, AboveTick
}