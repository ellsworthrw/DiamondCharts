package com.diamondedge.chartapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.diamondedge.charts.DateUtil
import com.diamondedge.charts.DefaultData
import com.diamondedge.charts.Margins
import com.diamondedge.charts.RandomData
import com.diamondedge.charts.compose.BarChart
import com.diamondedge.charts.compose.DateXYGraph
import com.diamondedge.charts.compose.LineGraph
import com.diamondedge.charts.compose.PieChart
import com.diamondedge.charts.compose.StackedAreaGraph
import com.diamondedge.charts.compose.StackedBarChart
import com.diamondedge.charts.compose.StockChart
import kotlin.random.Random

@Composable
fun SampleGraphScreen(sampleNum: Int = 1) {
    val margins = Margins.medium
    Surface(Modifier.fillMaxSize()) {
        when (sampleNum) {
            1 -> BarChart(RandomData(DefaultData.SIMPLE_SERIES, 3), margins = margins)
            2 -> BarChart(RandomData(DefaultData.SIMPLE_SERIES, 3), isVertical = false, margins = margins)
            3 -> StackedBarChart(RandomData(DefaultData.SIMPLE_SERIES, 3), margins = margins)
            4 -> StackedAreaGraph(RandomData(DefaultData.SIMPLE_SERIES, 5), is100Percent = false, margins = margins)
            5 -> PieChart(RandomData(DefaultData.SIMPLE_SERIES, 3), margins = margins)
            6 -> DateXYGraph(RandomData(DefaultData.HLOC_SERIES, 3), margins = margins)
            7 -> LineGraph(RandomData(DefaultData.HLOC_SERIES, 3), margins = margins)
            8 -> StockChart(RandomData(DefaultData.HLOC_SERIES, 1), margins = margins)
            9 -> {
                val randomMinutes = Random.nextDouble() * 100 * DateUtil.ONE_MINUTE
                val min = DateUtil.now() - randomMinutes
                val max = DateUtil.now() + randomMinutes
                DateXYGraph(RandomData(DefaultData.DATE_SERIES, 3, min, max, 50.0, 150.0), margins = margins)
            }
        }
    }
}
