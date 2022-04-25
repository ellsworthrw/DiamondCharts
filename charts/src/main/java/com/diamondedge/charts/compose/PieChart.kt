package com.diamondedge.charts.compose

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import com.diamondedge.charts.ChartContainer
import com.diamondedge.charts.ChartData
import com.diamondedge.charts.Charts

@Composable
private fun PieChart(
    pieChartData: ChartData,
    explodeIndex: Int = -1,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    Canvas(modifier = modifier) {
        val chart = Charts(size.width, size.height, ChartContainer.LEGEND_RIGHT)
        val pie = com.diamondedge.charts.PieChart(pieChartData)
        if (explodeIndex >= 0)
            pie.setExploded(explodeIndex, true)
        chart.add(pie)

        drawIntoCanvas { canvas ->
            chart.draw(ComposeGC(canvas, density))
        }
    }
}
