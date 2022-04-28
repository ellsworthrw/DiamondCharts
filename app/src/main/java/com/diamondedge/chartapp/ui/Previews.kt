package com.diamondedge.chartapp.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.diamondedge.charts.DefaultData
import com.diamondedge.charts.RandomData
import com.diamondedge.charts.compose.BarChart
import com.diamondedge.charts.compose.LineGraph
import com.diamondedge.charts.compose.PieChart
import com.diamondedge.charts.compose.StackedAreaGraph

@Preview
@Composable
private fun BarChartPreview() {
    BarChart(
        RandomData(DefaultData.SIMPLE_SERIES, 3),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
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
        isVertical = false
    )
}

@Preview
@Composable
private fun PieChartPreview() {
    PieChart(
        RandomData(DefaultData.SIMPLE_SERIES, 1),
//            explodeIndex = 1,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
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
    PieChart(RandomData(DefaultData.SIMPLE_SERIES), modifier = Modifier
        .fillMaxWidth()
        .height(500.dp))
}


@Preview
@Composable
private fun LineGraphPreview() {
    LineGraph(RandomData(DefaultData.SIMPLE_SERIES, 1), fillArea = true, modifier = Modifier
        .fillMaxWidth()
        .height(200.dp))
}

@Preview
@Composable
private fun LineGraphMultiPreview() {
    LineGraph(RandomData(DefaultData.SIMPLE_SERIES), modifier = Modifier
        .fillMaxWidth()
        .height(200.dp))
}

@Preview
@Composable
private fun StackedAreaGraphPreview() {
    StackedAreaGraph(RandomData(DefaultData.SIMPLE_SERIES, 5), modifier = Modifier
        .fillMaxWidth()
        .height(200.dp))
}
