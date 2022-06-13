/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

abstract class ChartObject {

    open val data: ChartData?
        get() = null

    abstract fun draw(g: GraphicsContext)

    internal open fun hitTest(x: Int, y: Int): Hotspot? {
        return null
    }
}
