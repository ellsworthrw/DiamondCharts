package com.diamondedge.chartapp.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import com.diamondedge.charts.ChartData
import com.diamondedge.charts.Charts
import com.diamondedge.charts.Charts.Companion.LEGEND_RIGHT
import com.diamondedge.charts.Color
import com.diamondedge.charts.Draw
import com.diamondedge.charts.GridLines
import com.diamondedge.charts.LineAttributes
import com.diamondedge.charts.Margins
import com.diamondedge.charts.TickLabelPosition
import com.diamondedge.charts.XYGraph
import com.diamondedge.charts.compose.ComposeGC

private val fn1: (Double) -> Double = { x ->
    (x + 1) * (x - 2) * (x - 1)
}
private val fn2: (Double) -> Double = { x ->
    (x + 2) * (x - 1) * (x - 3)
}
private const val minX = -1.0
private const val maxX = 3.0
private val scrubLine = LineAttributes(color = 0xffC4C4C4, width = 1f)
private const val scrubDataPointSizeDp = 8f

@Composable
fun ScrubbingScreen() {
    val density = LocalDensity.current
    var dragX by remember { mutableStateOf(0.0) }
    var isScrubbing by remember { mutableStateOf(false) }
    val data1 = createData(fn1, minX, maxX, "fn1", Color.green)
    val data2 = createData(fn2, minX, maxX, "fn2")
    val allData = listOf(data1, data2)

    Surface(Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        dragX += delta
                    },
                    onDragStarted = { pos ->
                        dragX = pos.x.toDouble()
                        isScrubbing = true
                    },
                    onDragStopped = {
                        isScrubbing = false
                    }
                )
        ) {
            val charts = Charts(size.width, size.height, Margins(25f, 25f, 45f, 10f), LEGEND_RIGHT)

            charts.add(XYGraph(data1))
            charts.add(XYGraph(data2))

            charts.vertAxis?.apply {
                majorTickLabelPosition = TickLabelPosition.BelowTick
            }
            charts.horizontalAxis?.apply {
                majorTickLabelPosition = TickLabelPosition.RightOfTick
            }

            drawIntoCanvas { canvas ->
                val g = ComposeGC(canvas, density)
                charts.draw(g)

                if (isScrubbing) {
                    val scrubX = dragX.toInt().coerceIn(charts.chartBounds.x, charts.chartBounds.right)
                    val scrubXValue = charts.horizontalAxis?.convertToValue(scrubX) ?: 0.0
                    val scrubPoints = ChartData.dataPointsAtX(scrubXValue, allData)
                    var text = "x = ${String.format("%.2f", scrubXValue)}"
                    GridLines.drawLine(g, scrubLine, scrubX, charts.chartBounds.y, scrubX, charts.chartBounds.bottom)
                    for ((data, value) in scrubPoints) {
                        val y = charts.vertAxis?.convertToPixel(value) ?: 0
                        Draw.drawCircle(g, scrubX, y, scrubDataPointSizeDp, null, Color.red)
                        text += " ${data.id}: ${String.format("%.2f", value)}"
                    }
                    Draw.drawTileCentered(g, scrubX, charts.chartBounds.y, null, text, false, 8f, 8f)
                }
            }
        }
    }
}

@Preview
@Composable
private fun ScrubbingScreenPreview() {
    ScrubbingScreen()
}
