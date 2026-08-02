package com.alpaomega1136.statsdroid.feature.clt.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CltRoute(
    viewModel: CltViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CltScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}
