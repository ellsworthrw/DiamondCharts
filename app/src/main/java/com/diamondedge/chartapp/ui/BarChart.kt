package com.diamondedge.chartapp.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import com.diamondedge.chart.ChartContainer
import com.diamondedge.chart.ChartData
import com.diamondedge.chart.DefaultData
import com.diamondedge.chart.RandomData
import com.diamondedge.chart.compose.Charts
import com.diamondedge.chart.compose.ComposeGC

@Composable
fun BarChart(
    data: ChartData,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    Canvas(modifier = modifier) {
        val chart = Charts(size.width, size.height - 50, ChartContainer.LEGEND_NONE)
        chart.add(com.diamondedge.chart.StackedAreaGraph(data))

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
