package com.diamondedge.chartapp

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.diamondedge.chartapp.ui.MainUI
import com.diamondedge.charts.ChartsLogging

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ChartsLogging.enabled = true
    // Attaches a Compose canvas to the #composeApplication element declared in index.html.
    ComposeViewport(viewportContainerId = "composeApplication", configure = {}) {
        MainUI()
    }
}