package com.diamondedge.charts

abstract class XYData : ChartData {
    val graphicAttributes = GraphicAttributes()

    abstract fun getX(dataPtNum: Int): Double

    abstract fun getY(dataPtNum: Int): Double

    abstract var minX: Double
        protected set
    abstract var maxX: Double
        protected set
    abstract var minY: Double
        protected set
    abstract var maxY: Double
        protected set

    override val id: Any = ""

    // following implements the more multipurpose ChartData interface:

    override val seriesCount: Int = 1
    override val valueCount: Int = 2
    override var options: Int = 0

    override fun getGraphicAttributes(series: Int): GraphicAttributes = graphicAttributes

    override fun getDouble(series: Int, dataPtNum: Int, valueNum: Int): Double {
        return if (valueNum == ChartData.xIndex) getX(dataPtNum) else getY(dataPtNum)
    }

    override fun getDataPoint(series: Int, dataPtNum: Int, createIfNull: Boolean): DataPoint? = null

    override fun getSeriesLabel(series: Int): String? = null

    override fun getDataLabel(dataPtNum: Int): String? = null

    override val minValue: Double
        get() = minY
    override val maxValue: Double
        get() = maxY
    override val minValue2: Double
        get() = minX
    override val maxValue2: Double
        get() = maxX
}
