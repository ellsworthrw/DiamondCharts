package com.diamondedge.charts

data class Margins(val left: Float, val top: Float, val right: Float, val bottom: Float) {
    companion object {
        val none = Margins(0f, 0f, 0f, 0f)
        val default = Margins(5f, 5f, 5f, 5f)
        val medium = Margins(10f, 10f, 10f, 10f)
        val wide = Margins(25f, 10f, 25f, 10f)
        val wideRight = Margins(10f, 10f, 25f, 10f)
    }
}