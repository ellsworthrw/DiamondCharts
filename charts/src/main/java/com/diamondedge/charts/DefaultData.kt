/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import java.util.Date
import java.util.Vector

open class DefaultData(override val id: Any = "", val seriesType: Int) : ChartData {
    protected var data = Vector<Vector<Any>>()

    override var seriesCount: Int
        get() = (columnCount - 1) / valueCount
        set(value) {
            if (value == 0)
                data[0] = Vector<Any>()
            updateColumnCount(value * valueCount + 1)
        }

    override var dataCount: Int
        get() = rowCount - 1
        set(value) {
            updateRowCount(value + 1)
        }

    override val valueCount: Int
        get() = seriesType and 0xF

    private val graphicAttributesList = Vector<GraphicAttributes?>()
    override var maxValue = 100.0
    override var minValue = 0.0
    override var maxValue2 = 100.0
    override var minValue2 = 0.0

    init {
        updateRowCount(1)
    }

    override fun getValue(series: Int, dataPtNum: Int, valueIndex: Int): Double {
        return toDouble(getValueAt(dataPtNum + 1, series * valueCount + 1 + valueIndex))
    }

    fun setDouble(series: Int, dataPtNum: Int, value: Double) {
        setDouble(series, dataPtNum, ChartData.valueIndex, value)
    }

    fun setDouble(series: Int, dataPtNum: Int, valueNum: Int, value: Double) {
        setValueAt(value, dataPtNum + 1, series * valueCount + 1 + valueNum)
    }

    override fun getSeriesLabel(series: Int): String? {
        val value = getValueAt(0, series + 1)
        return value?.toString()
    }

    fun setSeriesLabel(series: Int, label: String) {
        setValueAt(label, 0, series + 1)
    }

    override fun getDataLabel(dataPtNum: Int): String? {
        val value = getValueAt(dataPtNum + 1, 0)
        return value?.toString()
    }

    fun setDataLabel(dataPtNum: Int, label: String) {
        setValueAt(label, dataPtNum + 1, 0)
    }

    override fun getGraphicAttributes(series: Int): GraphicAttributes {
        if (series >= graphicAttributesList.size)
            graphicAttributesList.setSize(series + 1)
        var attr = graphicAttributesList[series]
        if (attr == null) {
            attr = GraphicAttributes()
            graphicAttributesList[series] = attr
        }
        return attr
    }

    fun setGraphicAttributes(series: Int, attr: GraphicAttributes) {
        if (series >= graphicAttributesList.size)
            graphicAttributesList.setSize(series + 1)
        graphicAttributesList[series] = attr
    }

    override fun recalc(combineSeries: Boolean) {
        maxValue = Double.MIN_VALUE
        minValue = Double.MAX_VALUE
        maxValue2 = Double.MIN_VALUE
        minValue2 = Double.MAX_VALUE
        var value: Double
        val valueCount = valueCount
        var index = ChartData.valueIndex
        var index2 = ChartData.xIndex
        val dataCount = dataCount
        val seriesCount = seriesCount
        if (valueCount > 1) {
            if (seriesType == HLOC_SERIES)
                index2 = ChartData.dateIndex
        } else {
            minValue2 = 0.0
            maxValue2 = (dataCount - 1).toDouble()
        }

        for (i in 0 until dataCount) {
            value = 0.0
            for (series in 0 until seriesCount) {
                if (seriesType == HLOC_SERIES) {
                    value = getValue(series, i, ChartData.highIndex)  // get high value
                    if (value > maxValue)
                        maxValue = value
                    value = getValue(series, i, ChartData.lowIndex)  // get low value
                    if (value < minValue)
                        minValue = value
                } else {
                    if (combineSeries)
                        value += getValue(series, i, index)
                    else
                        value = getValue(series, i, index)
                    if (value < minValue)
                        minValue = value
                    if (value > maxValue)
                        maxValue = value
                }

                if (valueCount > 1) {
                    value = getValue(series, i, index2)
                    if (value < minValue2)
                        minValue2 = value
                    if (value > maxValue2)
                        maxValue2 = value
                }
            }
        }

        log.d { "Data min: $minValue max: $maxValue" }
        log.d { "    min2: $minValue2 max2: $maxValue2" }
        log.d { "    combined: $combineSeries" }
    }

    var rowCount: Int
        get() = data.size
        set(_) {
        }

    var columnCount: Int = 0

    private fun updateRowCount(newRowCount: Int) {
        log.d { "updateRowCount " + newRowCount }
        if (rowCount != newRowCount) {
            justifyData(newRowCount, columnCount)
        }
    }

    private fun updateColumnCount(columnCount: Int) {
        log.d { "updateColumnCount " + columnCount }
        if (this.columnCount != columnCount) {
            justifyData(rowCount, columnCount)
        }
    }

    private fun getValueAt(row: Int, column: Int): Any? {
        try {
            return data[row][column]
        } catch (e: Exception) {
        }
        return null
    }

    private fun setValueAt(value: Any, row: Int, column: Int) {
        if (row >= rowCount) {
            updateRowCount(row + 1)
        }
        val rowList = data[row]

        if (rowList == null)
            log.d { "setValueAt is null at $row, $column" }

        rowList?.set(column, value)

        //fireTableCellUpdated( row, column );
    }

    private fun justifyData(newRowCount: Int, newColCount: Int) {
        //log.d {  "justify " + rowCount + ", " + colCount  };
        val oldRowCount = rowCount
        val oldColCount = columnCount
        //log.d {  "  old " + oldRowCount + ", " + oldColCount  };

        this.columnCount = newColCount

        if (newColCount != oldColCount) {
            if (newColCount < oldColCount) {
                //fireTableColumnsDeleted( );
            } else {
                for (i in 0 until oldRowCount) {
                    if (data[i] == null) {
                        data[i] = Vector<Any>()
                    }
                    data[i].setSize(newColCount)
                }
                //fireTableColumnsInserted( );
            }
        }

        data.setSize(newRowCount)

        if (newRowCount != oldRowCount) {
            if (newRowCount < oldRowCount) {
                //fireTableRowsDeleted( rowCount, oldRowCount - 1 );
            } else {
                for (i in oldRowCount until newRowCount) {
                    val v = Vector<Any>()
                    v.setSize(newColCount)
                    data[i] = v
                }
                //fireTableRowsInserted( oldRowCount, rowCount - 1 );
            }
        }
    }

    protected fun toStringParam(): String {
        return if (valueCount == 1) {
            "series=$seriesCount,size=$dataCount,min=$minValue,max=$maxValue,min2=$minValue2,max2=$maxValue2"
        } else {
            if (seriesType == DATE_SERIES)
                "series=$seriesCount,size=$dataCount,min=$minValue,max=$maxValue,min2=${DateUtil.toDate(minValue2)},max2=${
                    DateUtil.toDate(
                        maxValue2
                    )
                }"
            else
                "series=$seriesCount,size=$dataCount,min=$minValue,max=$maxValue,min2=$minValue2,max2=$maxValue2"
        }
    }

    override fun toString(): String {
        return "DefaultData[" + toStringParam() + "]"
    }

    companion object {
        private val log = moduleLogging()

        val DATE_0TH = 0x1000

        //       type             flags    default # of
        //                                 value  values
        val SIMPLE_SERIES = 0x100 or 0x00 or 0x01
        val XY_SERIES = 0x200 or 0x10 or 0x02
        val DATE_SERIES = 0x300 or 0x10 or 0x02 or DATE_0TH
        val XY_BUBBLE_SERIES = 0x400 or 0x10 or 0x03
        val DATE_BUBBLE_SERIES = 0x500 or 0x10 or 0x03 or DATE_0TH
        val HLOC_SERIES = 0x600 or 0x40 or 0x05

        private fun toDouble(value: Any?): Double {
            if (value is Number)
                return value.toDouble()
            else if (value is Date)
                return DateUtil.toDouble(value as Date?)
            return 0.0
        }

        internal fun getTitleWidth(d: ChartData, g: GraphicsContext): Int2D {
            val num = d.seriesCount
            if (num == 0)
                return Int2D(0, 0)
            var maxWidth = 0
            var totalWidth = 0
            var w: Int
            var title: String?
            for (i in 0 until num) {
                title = d.getSeriesLabel(i)
                if (title != null) {
                    w = g.stringWidth(title)
                    if (w > maxWidth)
                        maxWidth = w
                    totalWidth += w
                }
            }
            return Int2D(totalWidth / num, maxWidth)
        }

        internal fun getDataLabelWidth(d: ChartData, g: GraphicsContext): Int2D {
            val num = d.dataCount
            if (num == 0)
                return Int2D(0, 0)
            var maxWidth = 0
            var totalWidth = 0
            var w: Int
            var title: String?
            for (i in 0 until num) {
                title = d.getDataLabel(i)
                if (title != null) {
                    w = g.stringWidth(title)
                    if (w > maxWidth)
                        maxWidth = w
                    totalWidth += w
                }
            }
            return Int2D(totalWidth / num, maxWidth)
        }
    }
}
