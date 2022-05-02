package com.diamondedge.charts

enum class FontStyle { Normal, Bold, Italic, BoldItalic }

enum class FontFace { Default, SansSerif, Serif, Monospace, Native }

open class Font(val face: FontFace = FontFace.Default, val style: FontStyle = FontStyle.Normal, val size: Float = 12f) {

    internal var typeface: Any? = null
    var fontMetrics: FontMetrics? = null
        internal set

    override fun toString(): String {
        return "Font(face=$face, style=$style, size=$size, typeface=$typeface)"
    }

    companion object {
        val Default = Font()
        val Small = Font(size = 8f)
        val Large = Font(size = 18f)
        val XLarge = Font(size = 22f)
        val Bold = Font(style = FontStyle.Bold)
        val BoldLarge = Font(style = FontStyle.Bold, size = 18f)

        internal fun createNative(typeface: Any, style: FontStyle, size: Float): Font {
            val f = Font(FontFace.Native, style, size)
            f.typeface = typeface
            return f
        }
    }
}

