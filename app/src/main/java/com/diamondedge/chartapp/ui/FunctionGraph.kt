package com.diamondedge.chartapp.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import com.diamondedge.charts.ChartData
import com.diamondedge.charts.Charts
import com.diamondedge.charts.Color
import com.diamondedge.charts.DefaultXYData
import com.diamondedge.charts.Margins
import com.diamondedge.charts.TickLabelPosition
import com.diamondedge.charts.XYGraph
import com.diamondedge.charts.compose.ComposeGC

@Composable
fun FunctionGraph(
    minX: Double,
    maxX: Double,
    modifier: Modifier = Modifier,
    margins: Margins = Margins.default,
    fn: (Double) -> Double,
) {
    val density = LocalDensity.current
    Canvas(modifier = modifier) {
        // create an instance of the Charts container
        val charts = Charts(size.width, size.height, margins, Charts.LEGEND_NONE)

        // add the desired charts and graphs to the container
        // if multiple are added they will be drawn on top of each other
        charts.add(XYGraph(createData(fn, minX, maxX)))

        // make any desired changes to the vertical and horizontal axis
        charts.vertAxis?.apply {
            majorTickLabelPosition = TickLabelPosition.BelowTick
        }
        charts.horizontalAxis?.apply {
            majorTickLabelPosition = TickLabelPosition.RightOfTick
        }

        // do the drawing of chart
        // you can add custom drawing on top of the charts to provide even more customizations
        drawIntoCanvas { canvas ->
            val g = ComposeGC(canvas, density)
            charts.draw(g)
        }
    }
}

fun createData(fn: (Double) -> Double, minX: Double, maxX: Double, id: Any = "fn", color: Long = Color.blue): ChartData {

    val data = object : DefaultXYData(id) {
        override fun getSeriesLabel(series: Int): String {
            return id.toString()
        }
    }
    data.dataCount = 100
    val xInc = (maxX - minX) / data.dataCount
    for (i in 0 until data.dataCount) {
        val x = minX + (i + 1) * xInc
        data.setValue(i, x, fn(x))
    }
    data.graphicAttributes.color = color
    return data
}

@Preview
@Composable
private fun FunctionGraphPreview() {
    Surface {
        FunctionGraph(
            minX = -2.0,
            maxX = 4.0,
            modifier = Modifier.fillMaxSize(),
            margins = Margins.wideRight
        ) { x ->
            (x + 1) * (x - 1) * (x - 3)
        }
    }
}
