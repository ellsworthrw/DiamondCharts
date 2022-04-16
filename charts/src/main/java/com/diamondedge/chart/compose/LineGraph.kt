package com.diamondedge.chart.compose

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import com.diamondedge.chart.ChartContainer
import com.diamondedge.chart.ChartData
import com.diamondedge.chart.LineGraph

@Composable
private fun LineGraph(
    data: ChartData, drawLine: Boolean = true, fillArea: Boolean = false,
    modifier: Modifier,
) {
    val density = LocalDensity.current
    Canvas(modifier = modifier) {
        val chart = Charts(
            width = size.width,
            height = size.height,
            legendPosition = ChartContainer.LEGEND_RIGHT
        )
        chart.add(LineGraph(data, drawLine, fillArea))
        drawIntoCanvas { canvas ->
            chart.draw(ComposeGC(canvas, density))
        }
    }
}
