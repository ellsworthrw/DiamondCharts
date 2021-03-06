package com.diamondedge.charts

class Rectangle(var x: Int = 0, var y: Int = 0, var width: Int = 0, var height: Int = Int.MAX_VALUE) {

    val right: Int
        get() = x + width
    val bottom: Int
        get() = y + height

    val center: Pair<Int, Int>
        get() = Pair(x + width / 2, y + height / 2)

    fun copy(): Rectangle {
        return Rectangle().also { it.set(this) }
    }

    fun set(x: Int, y: Int, width: Int, height: Int) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }

    fun set(rect: Rectangle) {
        x = rect.x
        y = rect.y
        width = rect.width
        height = rect.height
    }

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

    /**
     * Returns true iff the this and the other rectangle intersect.
     *
     * @param other The second rectangle being tested for intersection
     * @return true iff the two rectangles intersect.
     */
    fun intersects(other: Rectangle): Boolean {
        return this.x < other.right && other.x < this.right && this.y < other.bottom && other.y < this.bottom
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

    fun set(rect: RectangleF) {
        x = rect.x
        y = rect.y
        width = rect.width
        height = rect.height
    }

    fun set(
        x: Float = 0f,
        y: Float = 0f,
        width: Float = Float.POSITIVE_INFINITY,
        height: Float = Float.POSITIVE_INFINITY
    ) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }

    fun set(x: Int, y: Int, width: Int, height: Int) {
        this.x = x.toFloat()
        this.y = y.toFloat()
        this.width = width.toFloat()
        this.height = height.toFloat()
    }

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

    /**
     * Returns true iff the this and the other rectangle intersect.
     *
     * @param other The second rectangle being tested for intersection
     * @return true iff the two rectangles intersect.
     */
    fun intersects(other: Rectangle): Boolean {
        return this.x < other.right && other.x < this.right && this.y < other.bottom && other.y < this.bottom
    }

    override fun toString(): String {
        return "RectangleF(x=$x, y=$y, width=$width, height=$height)"
    }
}