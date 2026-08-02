package com.alpaomega1136.statsdroid.feature.hypothesis.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HypothesisRoute(
    viewModel: HypothesisViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HypothesisScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}
