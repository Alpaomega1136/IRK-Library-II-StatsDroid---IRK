package com.alpaomega1136.statsdroid.feature.reference.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ReferenceRoute(
    viewModel: ReferenceViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    ReferenceScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onOpenExternalUrl = { url ->
            runCatching {
                uriHandler.openUri(url)
            }
        },
    )
}
