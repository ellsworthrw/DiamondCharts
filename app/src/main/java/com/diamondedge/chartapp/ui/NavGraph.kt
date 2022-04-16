package com.diamondedge.chartapp.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object MainDestinations {
    const val HOME_ROUTE = "home"
    const val CHART1_ROUTE = "chart1"
    const val CHARTS_ROUTE = "charts"
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    innerPadding: PaddingValues,
    startDestination: String = MainDestinations.HOME_ROUTE,
    viewModel: HomeViewModel = viewModel()
) {
    val actions = remember(navController) { MainActions(navController) }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        Modifier.padding(innerPadding)
    ) {
        composable(MainDestinations.HOME_ROUTE) {
            FunctionGraphScreen()
        }
        composable(MainDestinations.CHART1_ROUTE) {
            FunctionGraphScreen()
        }
        composable(MainDestinations.CHARTS_ROUTE) {
            LockOrientationScreen(actions.upPress)
        }
    }
}

class MainActions(navController: NavHostController) {
    val upPress: () -> Unit = {
        navController.navigateUp()
    }
}
