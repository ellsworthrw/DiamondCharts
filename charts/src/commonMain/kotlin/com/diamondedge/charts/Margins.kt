package com.diamondedge.charts

data class Margins(val left: Float, val top: Float, val right: Float, val bottom: Float) {
    companion object {
        val none = Margins(0f, 0f, 0f, 0f)
        val default = Margins(8f, 8f, 8f, 8f)
        val medium = Margins(16f, 16f, 16f, 16f)
        val wide = Margins(25f, 10f, 25f, 10f)
        val wideRight = Margins(16f, 16f, 25f, 16f)
    }
}