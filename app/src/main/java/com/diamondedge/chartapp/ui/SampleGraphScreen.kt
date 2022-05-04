package com.diamondedge.chartapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.diamondedge.charts.DefaultData
import com.diamondedge.charts.RandomData
import com.diamondedge.charts.compose.BarChart
import com.diamondedge.charts.compose.DateXYGraph
import com.diamondedge.charts.compose.LineGraph
import com.diamondedge.charts.compose.StackedAreaGraph
import com.diamondedge.charts.compose.StackedBarChart
import com.diamondedge.charts.compose.StockChart

@Composable
fun SampleGraphScreen(sampleNum: Int = 1) {

    Scaffold(Modifier.fillMaxSize()) {
        when (sampleNum) {
            1 -> BarChart(RandomData(DefaultData.SIMPLE_SERIES, 3), modifier = Modifier.fillMaxSize())
            2 -> StackedAreaGraph(RandomData(DefaultData.SIMPLE_SERIES, 5), is100Percent = false, modifier = Modifier.fillMaxSize())
            3 -> StackedBarChart(RandomData(DefaultData.SIMPLE_SERIES, 3), modifier = Modifier.fillMaxSize())
            4 -> BarChart(RandomData(DefaultData.SIMPLE_SERIES, 3), isVertical = false, modifier = Modifier.fillMaxSize())

            5 -> DateXYGraph(RandomData(DefaultData.HLOC_SERIES, 3), modifier = Modifier.fillMaxSize())
            6 -> LineGraph(RandomData(DefaultData.HLOC_SERIES, 3), modifier = Modifier.fillMaxSize())
            7 -> StockChart(RandomData(DefaultData.HLOC_SERIES, 1), modifier = Modifier.fillMaxSize())
        }
    }
}
