package com.diamondedge.chartapp

import androidx.compose.ui.window.ComposeUIViewController
import com.diamondedge.chartapp.ui.MainUI
import com.diamondedge.charts.ChartsLogging

@Suppress("unused", "FunctionName")
fun MainViewController() = ComposeUIViewController {
    ChartsLogging.enabled = true
    MainUI()
}
