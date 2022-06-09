package com.diamondedge.charts

/**
 * Style for the font. Note: SemiBold is not supported on all platforms and will be same as Bold on those platforms.
 * SemiBold is supported on Android API 28
 */
enum class FontStyle { Normal, Bold, SemiBold, Italic, BoldItalic }

enum class FontFace { Default, SansSerif, Serif, Monospace, Native }

open class Font(val face: FontFace = FontFace.Default, val style: FontStyle = FontStyle.Normal, val size: Float = 12f) {

    internal var typeface: Any? = null
    var fontMetrics: FontMetrics? = null
        internal set

    override fun toString(): String {
        return "Font(face=$face, style=$style, size=$size, typeface=$typeface)"
    }

    companion object {
        val XSmall = Font(size = 8f)
        val Small = Font(size = 10f)
        val Default = Font()    // 12f
        val Medium = Font(size = 14f)
        val Large = Font(size = 16f)
        val XLarge = Font(size = 20f)
        val Headline = Font(size = 32f)

        val Bold = Font(style = FontStyle.Bold)
        val BoldLarge = Font(style = FontStyle.Bold, size = 16f)
        val BoldXLarge = Font(style = FontStyle.Bold, size = 20f)

        val SemiBoldXSmall = Font(style = FontStyle.SemiBold, size = 8f)
        val SemiBoldSmall = Font(style = FontStyle.SemiBold, size = 10f)
        val SemiBold = Font(style = FontStyle.SemiBold)
        val SemiBoldMedium = Font(style = FontStyle.SemiBold, size = 14f)
        val SemiBoldLarge = Font(style = FontStyle.SemiBold, size = 16f)

        internal fun createNative(typeface: Any, style: FontStyle, size: Float): Font {
            val f = Font(FontFace.Native, style, size)
            f.typeface = typeface
            return f
        }
    }
}

