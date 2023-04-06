package com.diamondedge.chartapp.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.diamondedge.charts.Charts
import com.diamondedge.charts.DefaultData
import com.diamondedge.charts.Margins
import com.diamondedge.charts.RandomData
import com.diamondedge.charts.compose.BarChart
import com.diamondedge.charts.compose.LineGraph
import com.diamondedge.charts.compose.PieChart
import com.diamondedge.charts.compose.StockChart

@Preview
@Composable
private fun BarChartPreview() {
    BarChart(
        RandomData(DefaultData.SIMPLE_SERIES, 3),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        margins = Margins.medium
    )
}

@Preview
@Composable
private fun BarChartHorizontalPreview() {
    BarChart(
        RandomData(DefaultData.SIMPLE_SERIES, 3),
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        isVertical = false,
        margins = Margins.wide,
        legendPosition = Charts.LEGEND_RIGHT
    )
}

@Preview
@Composable
private fun PieChartPreview() {
    PieChart(
        RandomData(DefaultData.SIMPLE_SERIES, 1),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        margins = Margins.wide,
        legendPosition = Charts.LEGEND_RIGHT
    )
}

/*
@Preview
@Composable
private fun PieChartOthersPreview() {
    PieChart(
        OthersData(RandomData(DefaultData.SIMPLE_SERIES, 1)),
        modifier = Modifier.fillMaxWidth().height(400.dp)
    )
}
*/

@Preview
@Composable
private fun PieChartMultiPreview() {
    PieChart(
        RandomData(DefaultData.SIMPLE_SERIES),
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp),
        margins = Margins.wide,
        legendPosition = Charts.LEGEND_RIGHT
    )
}


@Preview
@Composable
private fun LineGraphPreview() {
    LineGraph(
        RandomData(DefaultData.SIMPLE_SERIES, 1),
        fillArea = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        margins = Margins.wideRight
    )
}

@Preview
@Composable
private fun LineGraphMultiPreview() {
    LineGraph(
        RandomData(DefaultData.SIMPLE_SERIES, 2),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        margins = Margins.wideRight
    )
}

@Preview
@Composable
private fun ScatterGraphPreview() {
    LineGraph(
        RandomData(DefaultData.SIMPLE_SERIES, 7),
        drawLine = false,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        margins = Margins.wideRight
    )
}

@Preview
@Composable
private fun StockChartPreview() {
    StockChart(
        RandomData(DefaultData.HLOC_SERIES, 2),
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        margins = Margins(10f, 10f, 30f, 10f)
    )
}

@Preview
@Composable
private fun FunctionGraphPreview() {
    FunctionGraph(
        -1.5,
        3.5,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        margins = Margins.wideRight
    ) { x ->
        (x + 1) * (x - 2) * (x - 2)
    }
}
