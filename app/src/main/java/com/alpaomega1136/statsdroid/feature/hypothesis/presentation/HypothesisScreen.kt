package com.alpaomega1136.statsdroid.feature.hypothesis.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components.HypothesisDistributionChart
import com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components.HypothesisInputForm
import com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components.HypothesisResultCard
import com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components.HypothesisTestTypeSelector
import com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components.SignificanceLevelSelector
import com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components.TailTypeSelector

@Composable
fun HypothesisScreen(
    uiState: HypothesisUiState,
    onEvent: (HypothesisEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            Text(text = "Visual Hypothesis Tester", style = MaterialTheme.typography.headlineMedium)
        }

        item {
            Text(
                text = "Compare a calculated test statistic with its critical region and determine whether the null hypothesis should be rejected.",
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        item {
            HypothesisTestTypeSelector(
                selectedTestType = uiState.selectedTestType,
                onTestTypeSelected = { onEvent(HypothesisEvent.TestTypeChanged(it)) },
            )
        }

        item {
            HypothesisInputForm(
                testType = uiState.selectedTestType,
                inputState = uiState.input,
                onEvent = onEvent,
            )
        }

        item {
            SignificanceLevelSelector(
                selectedLevel = uiState.significanceLevel,
                onLevelSelected = { onEvent(HypothesisEvent.SignificanceLevelChanged(it)) },
            )
        }

        item {
            TailTypeSelector(
                selectedTailType = uiState.tailType,
                onTailTypeSelected = { onEvent(HypothesisEvent.TailTypeChanged(it)) },
            )
        }

        item {
            Button(
                onClick = { onEvent(HypothesisEvent.Calculate) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Run hypothesis test")
            }
        }

        uiState.result?.let { result ->
            uiState.visualization?.let { visualization ->
                item {
                    HypothesisDistributionChart(
                        result = result,
                        visualization = visualization,
                    )
                }
            }

            item {
                HypothesisResultCard(result = result)
            }
        }
    }
}
