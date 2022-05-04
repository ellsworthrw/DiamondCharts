package com.diamondedge.charts

/**
 * Simplified `ChartData` that is specific for XYGraphs.
 * The more generic `ChartData` can still be used for XYGraphs.
 */
abstract class XYData : ChartData {
    val graphicAttributes = GraphicAttributes()

    abstract fun getX(dataPtNum: Int): Double

    abstract fun getY(dataPtNum: Int): Double

    abstract val minX: Double
    abstract val maxX: Double
    abstract val minY: Double
    abstract val maxY: Double

    override val id: Any = ""

    // following implements the more multipurpose ChartData interface:

    override val seriesCount: Int = 1
    override val valueCount: Int = 2

    override fun getGraphicAttributes(series: Int): GraphicAttributes = graphicAttributes

    override fun getValue(series: Int, dataPtNum: Int, valueIndex: Int): Double {
        return if (valueIndex == ChartData.xIndex) getX(dataPtNum) else getY(dataPtNum)
    }

    override fun getSeriesLabel(series: Int): String? = null

    override fun getDataLabel(dataPtNum: Int): String? = null

    override val minValue: Double by ::minY
    override val maxValue: Double by ::maxY
    override val minValue2: Double by ::minX
    override val maxValue2: Double by ::maxX
}
