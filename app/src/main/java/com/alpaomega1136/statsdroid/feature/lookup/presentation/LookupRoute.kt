package com.alpaomega1136.statsdroid.feature.lookup.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LookupRoute(
    viewModel: LookupViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LookupScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}
