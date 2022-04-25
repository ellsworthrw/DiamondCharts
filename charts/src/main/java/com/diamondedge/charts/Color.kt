package com.diamondedge.charts

object Color {
    const val white = 0xffFFFFFF
    const val gray = 0xffBABABA
    const val gray50 = 0x7fBABABAL  // .5 opacity
    const val gray20 = 0x33BABABAL  // .2 opacity
    const val ltGray = 0xffF8F8F8
    const val black = 0xff000000
    const val black20 = 0x33000000L
    const val red = 0xFFFF0000
    const val pink = 0xffFFC0CB
    const val orange = 0xffFFA500
    const val yellow = 0xffFFFF00
    const val green = 0xff00FF00
    const val magenta = 0xffFF00FF
    const val cyan = 0xff00FFFF
    const val blue = 0xff0000FF
    const val purple = 0xff880ED4
    const val brown = 0xff964B00
    const val transparent = 0x00000000L
    const val none = -1L

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
