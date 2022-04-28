package com.diamondedge.charts.compose

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import com.diamondedge.charts.BarChart
import com.diamondedge.charts.ChartContainer
import com.diamondedge.charts.ChartData
import com.diamondedge.charts.Charts

@Composable
fun BarChart(
    data: ChartData,
    modifier: Modifier = Modifier,
    isVertical: Boolean = true,
) {
    val density = LocalDensity.current
    Canvas(modifier = modifier) {
        val charts = Charts(size.width, size.height, ChartContainer.LEGEND_RIGHT)
        charts.add(BarChart(data, isVertical))

        drawIntoCanvas { canvas ->
            charts.draw(ComposeGC(canvas, density))
        }
    }
}
