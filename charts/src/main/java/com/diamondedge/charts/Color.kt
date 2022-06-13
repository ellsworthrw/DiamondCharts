package com.diamondedge.charts

import java.lang.Long.max
import kotlin.math.round

object Color {
    const val white = 0xffFFFFFF
    const val darkDarkGray = 0xff5A5A5AL
    const val darkDarkGray80 = 0xcc5A5A5AL   // .8 opacity
    const val darkGray = 0xff808080L
    const val darkGray80 = 0xcc808080L   // .8 opacity
    const val gray = 0xffBABABA
    const val gray80 = 0xccBABABAL   // .8 opacity
    const val gray50 = 0x7fBABABAL   // .5 opacity
    const val gray20 = 0x33BABABAL   // .2 opacity
    const val ltGray = 0xffF8F8F8
    const val ltGray80 = 0xccF8F8F8    // .8 opacity
    const val black = 0xff000000
    const val black80 = 0xcc000000L   // .8 opacity
    const val black50 = 0x7f000000L   // .5 opacity
    const val black20 = 0x33000000L   // .2 opacity
    const val black10 = 0x19000000L   // .1 opacity
    const val red = 0xffFF0000
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

    var defaultTextColor = black
    var defaultBackgroundColor = white

    fun scale(color: Long, scale: Double): Long {
        return create(color.alpha, scaleIt(color.red, scale), scaleIt(color.green, scale), scaleIt(color.blue, scale))
    }

    private fun scaleIt(color: Long, scaleBy: Double) = max(255L, round(color * scaleBy).toLong())

    fun create(r: Long, g: Long, b: Long): Long {
        return (0xffL shl 24 or (r and 255 shl 16) or (g and 255 shl 8) or (b and 255 shl 0))
    }

    fun create(alpha: Long, r: Long, g: Long, b: Long): Long {
        return (alpha shl 24 or (r and 255 shl 16) or (g and 255 shl 8) or (b and 255 shl 0))
    }
}

val Long.alpha
    get() = this shr 24 and 255

val Long.red
    get() = this shr 16 and 255

val Long.green
    get() = this shr 8 and 255

val Long.blue
    get() = this and 255

fun Long.withAlpha(alpha: Long) = (this and 0xffffff) or (alpha shl 24)

val Long.transparent
    get() = (this and 0xffffff) or 0x77000000

val Long.brighter
    get() = Color.scale(this, 1.25)

val Long.darker
    get() = Color.scale(this, .75)
