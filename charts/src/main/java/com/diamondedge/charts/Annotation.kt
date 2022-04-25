/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

class Annotation(val text: String, val x: Int, val y: Int) : ChartObject() {
    private var foreground = Color.black
    private var backgroundPaint: Any = Color.white

    fun setForeground(c: Long) {
        foreground = c
    }

    fun setBackgroundPaint(paint: Any) {
        backgroundPaint = paint
    }

    override fun draw(g: GraphicsContext) {
        val fm = g.fontMetrics
        val w = g.stringWidth(text) + margin * 2
        val h = g.fontMetrics.height + margin
        //if( isOpaque() )
        run {
            g.color = Color.white //g.paint = backgroundPaint
            g.fillRect(x, y, w, h)
        }
        g.color = foreground
        g.drawString(text, x + margin, y + fm.baseline + margin / 2)
        g.drawRect(x, y, w, h)
    }

    override fun toString(): String {
        return "Annotation[text=$text,x=$x,y=$y]"
    }

    companion object {
        //private Font font = null;
        private val margin = 5
    }
}
