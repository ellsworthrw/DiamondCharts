/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.chart

enum class GradientType {
    Radial,
    TopToBottom,
    LeftToRight
}

class Gradient(
    val colors: List<Pair<Float, Long>>,
    val bounds: RectangleF = RectangleF(),
    val style: GradientType = GradientType.TopToBottom
) {
    companion object {
        fun create(
            colors: List<Long>,
            bounds: RectangleF = RectangleF(),
            style: GradientType = GradientType.TopToBottom
        ): Gradient {
            val colorStops = ArrayList<Pair<Float, Long>>(colors.size)
            colorStops[0] = 0f to colors[0]
            for (i in 1 until colors.lastIndex) {
                val stop = i.toFloat() / colors.lastIndex
                colorStops[i] = stop to colors[i]
            }
            colorStops[colors.size - 1] = 1f to colors[colors.size - 1]
            return Gradient(colorStops, bounds, style)
        }
    }
}
