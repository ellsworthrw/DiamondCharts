package com.diamondedge.chartapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay

/** Type-safe destination keys for the Navigation 3 backstack. */
sealed interface Screen : NavKey {

    data object BarChart : Screen
    data object AreaChart : Screen
    data object DateXYChart : Screen
    data object Scrubbing : Screen
    data object PieChart : Screen
}

/**
 * Renders the top of the Navigation 3 [backStack] with the standard `navigation3-ui` [NavDisplay],
 * resolving each destination through the `navigation3-runtime` [entryProvider] DSL.
 */
@Composable
fun AppNavDisplay(
    backStack: SnapshotStateList<NavKey>,
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) },
        entryProvider = entryProvider {
            entry<Screen.BarChart> { SampleGraphScreen(3) }
            entry<Screen.AreaChart> { SampleGraphScreen(4) }
            entry<Screen.DateXYChart> { SampleGraphScreen(9) }
            entry<Screen.Scrubbing> { ScrubbingScreen() }
            entry<Screen.PieChart> { SampleGraphScreen(5) }
        },
    )
}