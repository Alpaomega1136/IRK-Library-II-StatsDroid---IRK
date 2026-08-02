package com.alpaomega1136.statsdroid.feature.clt.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun CltScreen(
    uiState: CltUiState,
    onEvent: (CltEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = "Central Limit Theorem Visualizer")
        Text(text = "Population: ${uiState.selectedPopulationShape.displayName}")
        Text(text = "Sample size: ${uiState.sampleSize}")
        Text(text = "Number of samples: ${uiState.simulationCount.displayName}")

        Button(
            onClick = { onEvent(CltEvent.Simulate) },
            enabled = !uiState.isRunning,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Simulate / Draw Samples")
        }

        if (uiState.isRunning) {
            CircularProgressIndicator()
        }

        uiState.errorMessage?.let { message ->
            Text(text = message)
        }

        uiState.result?.simulation?.let { result ->
            Text(text = String.format(Locale.US, "Theoretical mean: %.4f", result.theoreticalMean))
            Text(text = String.format(Locale.US, "Empirical mean: %.4f", result.empiricalMean))
            Text(text = String.format(Locale.US, "Standard error: %.4f", result.theoreticalStandardError))
        }
    }
}
