package com.diamondedge.charts.compose

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import com.diamondedge.charts.BarChart
import com.diamondedge.charts.ChartData
import com.diamondedge.charts.Charts
import com.diamondedge.charts.Margins

@Composable
fun BarChart(
    data: ChartData,
    modifier: Modifier,
    isVertical: Boolean = true,
    margins: Margins = Margins.default,
    legendPosition: Int = Charts.LEGEND_NONE,
) {
    val density = LocalDensity.current
    Canvas(modifier = modifier) {
        val charts = Charts(size.width, size.height, margins, legendPosition)
        charts.add(BarChart(data, isVertical))

        drawIntoCanvas { canvas ->
            charts.draw(ComposeGC(canvas, density))
        }
    }
}
