package com.diamondedge.chartapp.ui

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation3.runtime.NavKey
import com.diamondedge.chartapp.ui.theme.AppTheme
import com.diamondedge.charts.moduleLogging

private val log = moduleLogging()

@Composable
fun MainUI() {
    AppTheme {
        val backStack = remember { mutableStateListOf<NavKey>(Screen.BarChart) }

        Scaffold(
            bottomBar = { BottomBar(backStack) },
        ) { innerPadding ->
            AppNavDisplay(backStack, Modifier.padding(innerPadding))
        }
    }
}

private enum class NavTabs(val title: String, val screen: NavKey) {
    BarChart("Bar", Screen.BarChart),
    AreaChart("Area", Screen.AreaChart),
    DateXYChart("Date XY", Screen.DateXYChart),
    Scrubbing("Scrubbing", Screen.Scrubbing),
    PieChart("Pie", Screen.PieChart),
}

@Composable
private fun BottomBar(backStack: SnapshotStateList<NavKey>) {
    val tabs = remember { NavTabs.entries }
    val current = backStack.lastOrNull()

    // Switch to [screen] unless it is already the top of the backstack. Pushes the new destination
    // first and then drops the older entries so the Navigation 3 backstack is never momentarily empty.
    fun navigateTo(screen: NavKey) {
        if (backStack.lastOrNull() != screen) {
            log.d { "navigateTo $screen" }
            backStack.add(screen)
            while (backStack.size > 1) backStack.removeAt(0)
        }
    }

    BottomNavigation {
        tabs.forEach { tab ->
            BottomNavigationItem(
                icon = { Text(tab.title.take(1)) },
                label = { Text(tab.title.uppercase()) },
                selected = current == tab.screen,
                // `onClick` requires a full press+release. On Compose Desktop the mouse-release event
                // is intermittently not delivered, so the click never completes and the tab silently
                // fails to switch. The press IS always delivered, so we navigate from the press below
                // and keep onClick only as a backup (navigateTo is idempotent, so it can't double-nav).
                onClick = { navigateTo(tab.screen) },
                alwaysShowLabel = true,
                selectedContentColor = MaterialTheme.colors.secondary,
                unselectedContentColor = LocalContentColor.current,
                modifier = Modifier
                    .navigationBarsPadding()
                    // Initial pass + no consume: navigate on press without disturbing the item's own
                    // ripple/hover handling (which runs on the Main pass).
                    .pointerInput(tab.screen) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent(PointerEventPass.Initial)
                                if (event.type == PointerEventType.Press) {
                                    navigateTo(tab.screen)
                                }
                            }
                        }
                    },
            )
        }
    }
}
