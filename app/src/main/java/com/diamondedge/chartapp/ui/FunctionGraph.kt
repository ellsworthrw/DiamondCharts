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
import com.diamondedge.charts.DefaultXYData
import com.diamondedge.charts.TickLabelPosition
import com.diamondedge.charts.XYGraph
import com.diamondedge.charts.compose.ComposeGC

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

    val data = DefaultXYData("fn")
    data.dataCount = 100
    val xInc = (maxX - minX) / data.dataCount
    for (i in 0 until data.dataCount) {
        val x = minX + (i + 1) * xInc
        data.setValue(i, x, fn(x))
    }
    return data
}
