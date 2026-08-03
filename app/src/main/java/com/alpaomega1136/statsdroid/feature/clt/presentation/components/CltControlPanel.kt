package com.alpaomega1136.statsdroid.feature.clt.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.alpaomega1136.statsdroid.R
import com.alpaomega1136.statsdroid.feature.clt.presentation.CltEvent
import com.alpaomega1136.statsdroid.feature.clt.presentation.CltUiState
import com.alpaomega1136.statsdroid.ui.components.StatsPrimaryButton
import com.alpaomega1136.statsdroid.ui.components.StatsSectionCard
import com.alpaomega1136.statsdroid.ui.theme.StatsSpacing

@Composable
fun CltControlPanel(
    uiState: CltUiState,
    onEvent: (CltEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    StatsSectionCard(
        title = "Simulation Control Panel",
        subtitle = "Select population shape, sample size n, and number of simulations M",
        modifier = modifier,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(StatsSpacing.Medium)) {
            CltPopulationSelector(
                selectedShape = uiState.selectedPopulationShape,
                onShapeSelected = { onEvent(CltEvent.PopulationShapeChanged(it)) },
                enabled = !uiState.isRunning,
            )

            CltSampleSizeControl(
                sampleSize = uiState.sampleSize,
                onSampleSizeChanged = { onEvent(CltEvent.SampleSizeChanged(it)) },
                enabled = !uiState.isRunning,
            )

            CltSimulationCountControl(
                selectedCount = uiState.simulationCount,
                onCountSelected = { onEvent(CltEvent.SimulationCountChanged(it)) },
                enabled = !uiState.isRunning,
            )

            StatsPrimaryButton(
                text = if (uiState.isRunning) {
                    "Drawing ${uiState.simulationCount.displayName} samples..."
                } else if (uiState.result == null) {
                    stringResource(R.string.clt_simulate)
                } else {
                    stringResource(R.string.clt_run_again)
                },
                onClick = { onEvent(CltEvent.Simulate) },
                isLoading = uiState.isRunning,
                enabled = !uiState.isRunning,
                icon = Icons.Default.PlayArrow,
            )
        }
    }
}
