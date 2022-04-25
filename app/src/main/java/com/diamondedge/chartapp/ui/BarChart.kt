package com.diamondedge.chartapp.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import com.diamondedge.charts.ChartContainer
import com.diamondedge.charts.ChartData
import com.diamondedge.charts.Charts
import com.diamondedge.charts.DefaultData
import com.diamondedge.charts.RandomData
import com.diamondedge.charts.compose.ComposeGC

@Composable
fun BarChart(
    data: ChartData,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    Canvas(modifier = modifier) {
        val chart = Charts(size.width, size.height - 50, ChartContainer.LEGEND_NONE)
        chart.add(com.diamondedge.charts.StackedAreaGraph(data))

        drawIntoCanvas { canvas ->
            chart.draw(ComposeGC(canvas, density))
        }
    }
}

@Preview
@Composable
private fun BarChartPreview() {
    Scaffold {
        BarChart(RandomData(DefaultData.SIMPLE_SERIES, 5), modifier = Modifier.fillMaxSize(.5f))
    }
}
