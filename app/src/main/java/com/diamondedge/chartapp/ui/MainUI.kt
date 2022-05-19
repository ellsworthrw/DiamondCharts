package com.diamondedge.chartapp.ui

import androidx.annotation.StringRes
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.diamondedge.chartapp.R
import com.diamondedge.chartapp.ui.theme.AppTheme
import com.google.accompanist.insets.navigationBarsPadding
import java.util.Locale

@Composable
fun MainUI() {
    AppTheme {
        val tabs = remember { NavTabs.values() }
        val navController = rememberNavController()

        Scaffold(
            backgroundColor = MaterialTheme.colors.primarySurface,
            bottomBar = { BottomBar(navController = navController, tabs = tabs) }
        ) { innerPadding ->
            NavGraph(
                navController = navController, innerPadding
            )
        }
    }
}

private enum class NavTabs(@StringRes val title: Int, val icon: ImageVector, val route: String) {
    Home(R.string.home_tab, Icons.Outlined.Home, MainDestinations.HOME_ROUTE),
    Chart1(R.string.chart1_tab, Icons.Outlined.Create, MainDestinations.CHART1_ROUTE),
    Charts(R.string.charts_tab, Icons.Outlined.Place, MainDestinations.CHARTS_ROUTE)
}

@Composable
private fun BottomBar(navController: NavController, tabs: Array<NavTabs>) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavTabs.Home.route

    val routes = remember { NavTabs.values().map { it.route } }
    if (currentRoute in routes) {
        BottomNavigation {
            tabs.forEach { tab ->
                BottomNavigationItem(
                    icon = { Icon(imageVector = tab.icon, contentDescription = null) },
                    label = { Text(stringResource(tab.title).uppercase(Locale.getDefault())) },
                    selected = currentRoute == tab.route,
                    onClick = {
                        if (tab.route != currentRoute) {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    },
                    alwaysShowLabel = true,
                    selectedContentColor = MaterialTheme.colors.secondary,
                    unselectedContentColor = LocalContentColor.current,
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        }
    }
}
