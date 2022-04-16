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
import com.diamondedge.chart.TickLabelPosition
import com.diamondedge.chart.XYGraph
import com.diamondedge.chart.compose.Charts
import com.diamondedge.chart.compose.ComposeGC

@Composable
fun FunctionGraph(
    fn: (Double) -> Double,
    minX: Double,
    maxX: Double,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    Canvas(modifier = modifier) {
        val charts = Charts(size.width, size.height, ChartContainer.LEGEND_NONE)
        charts.add(XYGraph(createData(fn, minX, maxX)))
        charts.recalcAxis()  // get min/max values into axis

        charts.vertAxis?.apply {
            majorTickLabelPosition = TickLabelPosition.BelowTick
        }
        charts.horizontalAxis?.apply {
            majorTickLabelPosition = TickLabelPosition.RightOfTick
        }

        drawIntoCanvas { canvas ->
            val g = ComposeGC(canvas, density)
            charts.draw(g)
        }
    }
}

@Preview
@Composable
private fun FunctionGraphPreview() {
    Scaffold {
        FunctionGraph(
            { x -> (x + 1) * (x - 2) * (x - 2) },
            -2.0,
            4.0,
            modifier = Modifier.fillMaxSize()
        )
    }
}


fun createData(fn: (Double) -> Double, minX: Double, maxX: Double): ChartData {

    val data = DefaultData(DefaultData.XY_SERIES)
    data.seriesCount = 1
    data.dataCount = 100
    val xInc = (maxX - minX) / data.dataCount
    for (i in 0 until data.dataCount) {
        val x = minX + (i + 1) * xInc
        data.setDouble(0, i, ChartData.xIndex, x)
        data.setDouble(0, i, ChartData.yIndex, fn(x))
    }
    return data
}
