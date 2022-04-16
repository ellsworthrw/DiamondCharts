/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.chart

abstract class Chart(override val data: ChartData) : ChartObject() {

    open val showInLegend: Int
        get() = Legend.SERIES

    var horizontalAxis: Axis?
        get() = horAxis
        set(axis) {
            horAxis = axis
            setup()
        }

    var verticalAxis: Axis?
        get() = vertAxis
        set(axis) {
            vertAxis = axis
            setup()
        }

    /** Returns the number of values per data point
     * public int getValueCount();
     * public static int getValueCount( GraphData );
     *
     * public Date getDate( int series, int dataPtNum, int valueNum );
     * public void setDate( int series, int dataPtNum, int valueNum, Date val );
     *
     * public void setData( int series, double val );
     * public void setData( int series, double val0, double val1 );
     * public void setData( int series, double val0, double val1, double val2 );
     * public void setData( int series, double val0, double val1, double val2, double val3, double val4 );
     */


    /** @return true if the vertical axis displays the data values for the chart.
     * override if the horizontal axis is the axis which scales the data and return false
     */
    open val isVertical: Boolean
        get() = true

    /** If set to true cause the graph to create Hotspot areas on the graph.
     * This is required for the method hitTest to work and is required
     * for roll over labels to be displayed.
     * @see .hitTest
     *
     * @see .setLabelType
     */
    var isHotspotsAvailable: Boolean
        get() = hotspots != null
        set(createHotspots) = if (createHotspots)
            hotspots = ArrayList()
        else
            hotspots = null

    internal var vertAxis: Axis? = null
    internal var horAxis: Axis? = null
    internal var hotspots: ArrayList<Hotspot>? = null
    internal var labelType = LABEL_NEVER

    abstract override fun draw(g: GraphicsContext)

    internal fun isLegendSymbolShowing(series: Int, dataPtNum: Int): Boolean {
        return true
    }

    open fun drawLegendSymbol(g: GraphicsContext, x: Int, y: Int, width: Int, height: Int, series: Int, dataPtNum: Int): Boolean {
        val gattr = data.getGraphicAttributes(series)
        g.color = gattr.color
        g.fillRect(x, y, width, height)
        return true
    }

    open fun setBounds(x: Int, y: Int, width: Int, height: Int) {}

    open fun usesAxis(): Boolean {
        return true
    }

    open fun createHorizontalAxis(): Axis {
        return DecimalAxis()
    }

    open fun createVerticalAxis(): Axis {
        return DecimalAxis()
    }

    init {
        setup()
    }

    private fun setup() {

        log.d { "setup: $data" }
        data.recalc()
        log.d { "setup after: $data" }
        val dataCount = data.dataCount
        val labels = ArrayList<Any?>(dataCount)
        for (i in 0 until dataCount) {
            labels.add(data.getDataLabel(i))
        }

        val min = data.minValue
        val max = data.maxValue
        if (isVertical) {
            if (vertAxis != null && vertAxis!!.isAutoScaling) {
                vertAxis!!.minValue = min
                vertAxis!!.maxValue = max
            }
            if (horAxis != null) {
                horAxis!!.setLabels(labels)
                horAxis!!.setDataCount(dataCount)
            }
        } else {
            if (horAxis != null && horAxis!!.isAutoScaling) {
                horAxis!!.minValue = min
                horAxis!!.maxValue = max
            }
            if (vertAxis != null) {
                vertAxis!!.setLabels(labels)
                vertAxis!!.setDataCount(dataCount)
            }
        }
    }

    override fun hitTest(x: Int, y: Int): Hotspot? {
        val num = if (hotspots == null) 0 else hotspots!!.size
        for (i in 0 until num) {
            val h = hotspots!!.get(i)
            if (h.shape.contains(x, y)) {
                //log.d {  "series:" + h.series + " datapt:" + h.dataPtNum  };
                return h
            }
        }
        return null
    }

    fun getLabelType(): Int {
        return labelType
    }

    fun setLabelType(type: Int) {
        labelType = type
        if (labelType == LABEL_ROLL_OVER)
            isHotspotsAvailable = true
    }

    /*
  protected boolean isDirty()
  {
    return isDirty;
  }

  protected void setDirty( boolean dirty )
  {
    isDirty = dirty;
  }
  private boolean isDirty = false;
  */

    protected open fun toStringParam(): String {
        return "data=$data,vert=$vertAxis,hor=$horAxis"
    }

    companion object {
        private val log = moduleLogging()

        val LABEL_NEVER = 0

        //public static final int LABEL_ALWAYS = 1;
        val LABEL_ROLL_OVER = 2
    }
}
