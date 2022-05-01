package com.diamondedge.charts

/**
 * Simplified `ChartData` that is specific for XYGraphs.
 * The more generic `ChartData` can still be used for XYGraphs.
 */
abstract class XYData : ChartData {
    val graphicAttributes = GraphicAttributes()

    abstract fun getX(dataPtNum: Int): Double

    abstract fun getY(dataPtNum: Int): Double

    abstract var minX: Double
    abstract var maxX: Double
    abstract var minY: Double
    abstract var maxY: Double

    override val id: Any = ""

    // following implements the more multipurpose ChartData interface:

    override val seriesCount: Int = 1
    override val valueCount: Int = 2

    override fun getGraphicAttributes(series: Int): GraphicAttributes = graphicAttributes

    override fun getDouble(series: Int, dataPtNum: Int, valueNum: Int): Double {
        return if (valueNum == ChartData.xIndex) getX(dataPtNum) else getY(dataPtNum)
    }

    override fun getDataPoint(series: Int, dataPtNum: Int, createIfNull: Boolean): DataPoint? = null

    override fun getSeriesLabel(series: Int): String? = null

    override fun getDataLabel(dataPtNum: Int): String? = null

    override var minValue: Double by ::minY
    override var maxValue: Double by ::maxY
    override var minValue2: Double by ::minX
    override var maxValue2: Double by ::maxX
}
