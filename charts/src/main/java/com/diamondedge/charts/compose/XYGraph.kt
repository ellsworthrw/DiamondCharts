package com.diamondedge.charts.compose

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity

import com.diamondedge.charts.ChartData
import com.diamondedge.charts.Charts
import com.diamondedge.charts.Margins
import com.diamondedge.charts.XYGraph

@Composable
fun XYGraph(
    data: ChartData,
    modifier: Modifier,
    drawLine: Boolean = true,
    fillArea: Boolean = false,
    margins: Margins = Margins.default,
    legendPosition: Int = Charts.LEGEND_NONE,
) {
    val density = LocalDensity.current
    Canvas(modifier = modifier) {
        val charts = Charts(size.width, size.height, margins, legendPosition)
        charts.add(XYGraph(data, drawLine, fillArea))
        drawIntoCanvas { canvas ->
            charts.draw(ComposeGC(canvas, density))
        }
    }
}
