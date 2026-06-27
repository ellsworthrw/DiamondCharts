package com.diamondedge.chartapp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.diamondedge.chartapp.ui.MainUI
import com.diamondedge.charts.ChartsLogging

fun main() {
    ChartsLogging.enabled = true
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Diamond Charts",
        ) {
            MainUI()
        }
    }
}