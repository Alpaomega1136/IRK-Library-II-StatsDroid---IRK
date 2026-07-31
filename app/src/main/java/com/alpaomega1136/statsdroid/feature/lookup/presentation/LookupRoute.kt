package com.alpaomega1136.statsdroid.feature.lookup.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LookupRoute(
    viewModel: LookupViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LookupScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}
