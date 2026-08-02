package com.alpaomega1136.statsdroid.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.alpaomega1136.statsdroid.feature.about.presentation.AboutScreen
import com.alpaomega1136.statsdroid.feature.clt.presentation.CltRoute
import com.alpaomega1136.statsdroid.feature.hypothesis.presentation.HypothesisRoute
import com.alpaomega1136.statsdroid.feature.lookup.presentation.LookupRoute

@Composable
fun StatsDroidNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Lookup.route,
        modifier = modifier,
    ) {
        composable(route = AppDestination.Lookup.route) {
            LookupRoute()
        }

        composable(route = AppDestination.Hypothesis.route) {
            HypothesisRoute()
        }

        composable(route = AppDestination.Clt.route) {
            CltRoute()
        }

        composable(route = AppDestination.About.route) {
            AboutScreen()
        }
    }
}
