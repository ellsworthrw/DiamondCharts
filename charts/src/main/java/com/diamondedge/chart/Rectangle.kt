package com.diamondedge.chart

class Rectangle(var x: Int = 0, var y: Int = 0, var width: Int = 0, var height: Int = 0) {

    val right: Int
        get() = x + width
    val bottom: Int
        get() = y + height

    val center: Pair<Int, Int>
        get() = Pair(x + width / 2, y + height / 2)

    fun contains(x: Int, y: Int): Boolean {
        var w = this.width
        var h = this.height
        if (w or h < 0) {
            return false
        } else {
            if (x >= this.x && y >= this.y) {
                w += this.x
                h += this.x
                return (w < this.x || w > x) && (h < this.y || h > y)
            } else {
                return false
            }
        }
    }

    override fun toString(): String {
        return "Rectangle(x=$x, y=$y, width=$width, height=$height)"
    }

}

class RectangleF(
    var x: Float = 0f,
    var y: Float = 0f,
    var width: Float = Float.POSITIVE_INFINITY,
    var height: Float = Float.POSITIVE_INFINITY
) {

    val right: Float
        get() = x + width
    val bottom: Float
        get() = y + height

    val center: Pair<Float, Float>
        get() = Pair(x + width / 2, y + height / 2)

    fun contains(x: Float, y: Float): Boolean {
        var w = width
        var h = height
        return if (w <= 0 || h <= 0) {
            false
        } else {
            if (x >= this.x && y >= this.y) {
                w += this.x
                h += this.x
                (w < this.x || w > x) && (h < this.y || h > y)
            } else {
                false
            }
        }
    }

    override fun toString(): String {
        return "RectangleF(x=$x, y=$y, width=$width, height=$height)"
    }
}