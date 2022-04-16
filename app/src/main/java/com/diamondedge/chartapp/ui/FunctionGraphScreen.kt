package com.diamondedge.chartapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FunctionGraphScreen() {

    Scaffold(Modifier.fillMaxSize()) {
        FunctionGraph(
            { x -> (x + 1) * (x - 2) * (x - 2) },
            -1.5,
            3.5,
            modifier = Modifier.fillMaxSize()
        )
    }
}