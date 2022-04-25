package com.diamondedge.chartapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.diamondedge.charts.ChartsLogging

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ChartsLogging.enabled = true

        setContent {
            MainUI()
        }
    }
}
