/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

class GraphicAttributes(
    var color: Long = Color.blue,
    var gradient: Gradient? = null,
    var symbol: Int = 0,
    var fill: Boolean = true,
    var drawBorder: Boolean = true,
    var borderColor: Long = Color.black
) {
    var draw3D: Boolean = false
    internal var flags = 0
}
