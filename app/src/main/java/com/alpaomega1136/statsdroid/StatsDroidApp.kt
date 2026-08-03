package com.alpaomega1136.statsdroid

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alpaomega1136.statsdroid.navigation.AppDestination
import com.alpaomega1136.statsdroid.navigation.StatsDroidNavHost
import com.alpaomega1136.statsdroid.ui.components.StatsBottomNavigation

@Composable
fun StatsDroidApp() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            StatsBottomNavigation(
                destinations = AppDestination.bottomBarItems,
                currentRoute = currentRoute,
                onNavigateToDestination = { destination ->
                    navController.navigate(destination.route) {
                        popUpTo(
                            navController.graph
                                .findStartDestination()
                                .id,
                        ) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        },
    ) { innerPadding ->
        StatsDroidNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
        )
    }
}
