package com.alpaomega1136.statsdroid.feature.clt.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.R
import com.alpaomega1136.statsdroid.feature.clt.presentation.CltEvent
import com.alpaomega1136.statsdroid.feature.clt.presentation.CltUiState

@Composable
fun CltControlPanel(
    uiState: CltUiState,
    onEvent: (CltEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            CltPopulationSelector(
                selectedShape = uiState.selectedPopulationShape,
                onShapeSelected = { onEvent(CltEvent.PopulationShapeChanged(it)) },
            )
            CltSampleSizeControl(
                sampleSize = uiState.sampleSize,
                onSampleSizeChanged = { onEvent(CltEvent.SampleSizeChanged(it)) },
            )
            CltSimulationCountControl(
                selectedCount = uiState.simulationCount,
                onCountSelected = { onEvent(CltEvent.SimulationCountChanged(it)) },
            )
            Button(
                onClick = { onEvent(CltEvent.Simulate) },
                enabled = !uiState.isRunning,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (uiState.isRunning) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text(text = stringResource(if (uiState.result == null) R.string.clt_simulate else R.string.clt_run_again))
                }
            }
        }
    }
}
