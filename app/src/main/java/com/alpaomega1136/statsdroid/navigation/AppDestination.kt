package com.alpaomega1136.statsdroid.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    data object Lookup : AppDestination(
        route = "lookup",
        label = "Lookup",
        icon = Icons.Default.Search,
    )

    data object Hypothesis : AppDestination(
        route = "hypothesis",
        label = "Hypothesis",
        icon = Icons.Default.CheckCircle,
    )

    data object About : AppDestination(
        route = "about",
        label = "About",
        icon = Icons.Default.Info,
    )

    companion object {
        val bottomBarItems = listOf(
            Lookup,
            Hypothesis,
            About,
        )
    }
}
