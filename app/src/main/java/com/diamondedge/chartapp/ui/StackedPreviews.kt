package com.diamondedge.chartapp.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.diamondedge.charts.DefaultData
import com.diamondedge.charts.RandomData
import com.diamondedge.charts.compose.StackedAreaGraph
import com.diamondedge.charts.compose.StackedBarChart

@Preview
@Composable
private fun BarChartPreview() {
    StackedBarChart(
        RandomData(DefaultData.SIMPLE_SERIES, 3),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}

@Preview
@Composable
private fun BarChart100PercentPreview() {
    StackedBarChart(
        RandomData(DefaultData.SIMPLE_SERIES, 3),
        is100Percent = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}

@Preview
@Composable
private fun BarChartHorizontalPreview() {
    StackedBarChart(
        RandomData(DefaultData.SIMPLE_SERIES, 3),
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(horizontal = 8.dp),
        isVertical = false
    )
}

@Preview
@Composable
private fun StackedAreaGraphPreview() {
    StackedAreaGraph(
        RandomData(DefaultData.SIMPLE_SERIES, 5), modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}

@Preview
@Composable
private fun Stacked100PercentAreaGraphPreview() {
    StackedAreaGraph(
        RandomData(DefaultData.SIMPLE_SERIES, 5), is100Percent = true, modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}
