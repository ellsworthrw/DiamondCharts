package com.diamondedge.charts.compose

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import com.diamondedge.charts.BarChart

import com.diamondedge.charts.ChartData
import com.diamondedge.charts.Charts

@Composable
fun StackedBarChart(
    data: ChartData,
    modifier: Modifier,
    is100Percent: Boolean = false,
    isVertical: Boolean = true,
    legendPosition: Int = Charts.LEGEND_NONE,
) {
    val density = LocalDensity.current
    Canvas(modifier = modifier) {
        val charts = Charts(size.width, size.height, legendPosition)
        charts.add(BarChart(data, isVertical, isStacked = true, is100Percent = is100Percent))

        drawIntoCanvas { canvas ->
            charts.draw(ComposeGC(canvas, density))
        }
    }
}
