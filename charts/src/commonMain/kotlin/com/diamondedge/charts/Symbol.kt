package com.diamondedge.charts

data class Symbol(val type: SymbolType, val color: Long, val sizeDp: Float) {

    fun drawCentered(g: GraphicsContext, xc: Int, yc: Int) {
        Draw.drawSymbol(g, xc, yc, sizeDp, type, color)
    }

    fun draw(g: GraphicsContext, x: Int, y: Int) {
        Draw.drawSymbolAt(g, x, y, sizeDp, type, color)
    }
}

enum class SymbolType {
    NONE,
    SQUARE,
    SQUARE_OUTLINE,
    CIRCLE,
    CIRCLE_OUTLINE,
    TRIANGLE,
    TRIANGLE_OUTLINE,
    DIAMOND,
    DIAMOND_OUTLINE,
    PLUS,
    ASTERISK,
    CROSS,
    CROSS_DIAGONAL,
    SQUARE_SMALL,
    CIRCLE_SMALL,
    UP_ARROW,
    DOWN_ARROW,
    TRIANGLE_DOWN,
    SMALL_DOT,
}