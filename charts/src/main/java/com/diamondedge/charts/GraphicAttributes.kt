/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

class GraphicAttributes(
    var color: Long = Color.blue,
    var gradient: Gradient? = null,
    var symbol: SymbolType = SymbolType.NONE,
    var fill: Boolean = true,
    var drawBorder: Boolean = true,
    var borderColor: Long = Color.defaultTextColor
) {
    var draw3D: Boolean = false
    internal var flags = 0
}
