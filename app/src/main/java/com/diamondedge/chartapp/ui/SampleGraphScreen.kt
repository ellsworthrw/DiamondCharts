package com.diamondedge.chartapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.diamondedge.charts.DefaultData
import com.diamondedge.charts.RandomData
import com.diamondedge.charts.compose.LineGraph

@Composable
fun SampleGraphScreen() {

    Scaffold(Modifier.fillMaxSize()) {
//        BarChart(
//            RandomData(DefaultData.SIMPLE_SERIES, 3),
//            modifier = Modifier.fillMaxSize()
//        )

//        StackedAreaGraph(
//            RandomData(DefaultData.SIMPLE_SERIES, 5), is100Percent = false,
//            modifier = Modifier.fillMaxSize()
//        )
//        DateXYGraph(
//            RandomData(DefaultData.HLOC_SERIES, 3),
//            modifier = Modifier.fillMaxSize()
//        )
        LineGraph(
            RandomData(DefaultData.HLOC_SERIES, 3),
            modifier = Modifier.fillMaxSize()
        )
//        StockChart(
//            RandomData(DefaultData.HLOC_SERIES, 1),
//            modifier = Modifier.fillMaxSize()
//        )
    }
}
