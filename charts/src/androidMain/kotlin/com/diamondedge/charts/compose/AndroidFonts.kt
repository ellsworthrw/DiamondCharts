/**
 * Copyright 2004-2026 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts.compose

import android.graphics.Typeface
import android.os.Build
import com.diamondedge.charts.Font
import com.diamondedge.charts.FontFace
import com.diamondedge.charts.FontStyle

/**
 * Creates a chart [Font] wrapping an existing Android [typeface]. Replaces the former
 * `ComposeGC.createFont(Typeface, Float)` companion helper.
 */
fun createFont(typeface: Typeface, size: Float): Font {
    return Font.createNative(typeface, toFontStyle(typeface), size)
}

/**
 * Creates a chart [Font] from an Android [family] typeface and the given [style].
 * Replaces the former `ComposeGC.createFont(Typeface, FontStyle, Float)` companion helper.
 */
fun createFont(family: Typeface, style: FontStyle, size: Float): Font {
    val typeface = createTypeface(family, style)
    return Font.createNative(typeface, style, size)
}

internal fun createTypeface(font: Font): Typeface {
    val typeface = when (font.face) {
        FontFace.Serif -> Typeface.SERIF
        FontFace.SansSerif -> Typeface.SANS_SERIF
        FontFace.Monospace -> Typeface.MONOSPACE
        FontFace.Default -> Typeface.DEFAULT
        FontFace.Native -> font.typeface as Typeface
    }
    return createTypeface(typeface, font.style)
}

internal fun createTypeface(family: Typeface, style: FontStyle): Typeface {
    var fontStyle = style
    if (fontStyle == FontStyle.SemiBold) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            return Typeface.create(family, 600, false)
        fontStyle = FontStyle.Bold
    }
    return Typeface.create(family, toAndroidFontStyle(fontStyle))
}

internal fun toAndroidFontStyle(fontStyle: FontStyle): Int = when (fontStyle) {
    FontStyle.Bold -> Typeface.BOLD
    FontStyle.SemiBold -> Typeface.BOLD
    FontStyle.Normal -> Typeface.NORMAL
    FontStyle.Italic -> Typeface.ITALIC
    FontStyle.BoldItalic -> Typeface.BOLD_ITALIC
}

internal fun toFontStyle(typeface: Typeface): FontStyle = when (typeface.style) {
    Typeface.BOLD_ITALIC -> FontStyle.BoldItalic
    Typeface.BOLD -> FontStyle.Bold
    Typeface.ITALIC -> FontStyle.Italic
    else -> FontStyle.Normal
}
