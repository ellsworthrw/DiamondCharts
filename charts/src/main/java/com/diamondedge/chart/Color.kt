package com.diamondedge.chart

object Color {
    const val white = 0xffFFFFFF
    val gray = 0xffBABABA
    val black = 0xff000000
    val red = 0xFFFF0000
    val pink = 0xffFFC0CB
    val orange = 0xffFFA500
    val yellow = 0xffFFFF00
    val green = 0xff00FF00
    val magenta = 0xffFF00FF
    val cyan = 0xff00FFFF
    val blue = 0xff0000FF
    val purple = 0xff880ED4
    val brown = 0xff964B00
    val transparent = 0x00000000
    val none = -1L

    val origOrange = -0x3800.inv()

    fun getRed(color: Long): Long {
        return color shr 16 and 255
    }

    fun getGreen(color: Long): Long {
        return color shr 8 and 255
    }

    fun getBlue(color: Long): Long {
        return color and 255
    }

    fun getAlpha(color: Long): Long {
        return color shr 24 and 255
    }

    fun brighter(color: Long): Long {
        return color
    }

    fun transparent(color: Long): Long {
        return (color and 0xffffff) or 0x77000000
    }

    fun create(r: Long, g: Long, b: Long): Long {
        return (0xffL shl 24 or (r and 255 shl 16) or (g and 255 shl 8) or (b and 255 shl 0))
    }
}
