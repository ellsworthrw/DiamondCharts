package com.diamondedge.chart

interface FontMetrics {
    /**
     * The maximum distance above the baseline for the tallest glyph in
     * the font at a given text size.
     */
    val top: Int

    /**
     * The recommended distance above the baseline for singled spaced text.
     */
    val ascent: Int

    /**
     * The recommended distance below the baseline for singled spaced text.
     */
    val descent: Int

    /**
     * The maximum distance below the baseline for the lowest glyph in
     * the font at a given text size.
     */
    val bottom: Int

    /**
     * The recommended additional space to add between lines of text.
     */
    val leading: Int

    /**
     * total height of the font
     */
    val height: Int

    /**
     * distance from top for the baseline
     */
    val baseline: Int
}
