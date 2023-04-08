package com.diamondedge.chartapp.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object MainDestinations {
    const val BAR_CHART_ROUTE = "bar"
    const val AREA_CHART_ROUTE = "area_chart"
    const val PIE_CHART_ROUTE = "charts"
    const val SCRUBBING_ROUTE = "scrub"
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    innerPadding: PaddingValues,
    startDestination: String = MainDestinations.BAR_CHART_ROUTE,
) {
    val actions = remember(navController) { MainActions(navController) }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        Modifier.padding(innerPadding)
    ) {
        composable(MainDestinations.BAR_CHART_ROUTE) {
            SampleGraphScreen(3)
        }
        composable(MainDestinations.AREA_CHART_ROUTE) {
            SampleGraphScreen(4)
        }
        composable(MainDestinations.PIE_CHART_ROUTE) {
            SampleGraphScreen(5)
        }
        composable(MainDestinations.SCRUBBING_ROUTE) {
            ScrubbingScreen()
        }
    }
}

class MainActions(navController: NavHostController) {
    val upPress: () -> Unit = {
        navController.navigateUp()
    }
}
