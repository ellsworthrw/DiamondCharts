package com.diamondedge.chartapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.diamondedge.chart.ChartData
import com.diamondedge.chart.DefaultData
import com.diamondedge.chart.RandomData
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())

        setContent {
            MainUI()
        }
        logit(RandomData(DefaultData.SIMPLE_SERIES))
    }

    private fun logit(data: ChartData) {
        data.recalc()
        Timber.i("data: $data")
    }
}
