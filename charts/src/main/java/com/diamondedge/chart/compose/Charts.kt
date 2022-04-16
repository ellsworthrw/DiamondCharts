/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.chart.compose

import com.diamondedge.chart.BaseMultiChart
import com.diamondedge.chart.Dimension

open class Charts(width: Float, height: Float, legendPosition: Int = LEGEND_NONE) : BaseMultiChart(legendPosition) {

    override val size = Dimension(width.toInt(), height.toInt())

}