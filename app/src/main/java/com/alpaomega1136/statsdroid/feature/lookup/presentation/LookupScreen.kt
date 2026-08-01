package com.alpaomega1136.statsdroid.feature.lookup.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.feature.lookup.presentation.components.BinomialInputForm
import com.alpaomega1136.statsdroid.feature.lookup.presentation.components.DistributionSelector
import com.alpaomega1136.statsdroid.feature.lookup.presentation.components.PoissonInputForm
import com.alpaomega1136.statsdroid.feature.lookup.presentation.components.ProbabilityResultCard
import com.alpaomega1136.statsdroid.feature.lookup.presentation.components.StandardNormalInputForm

@Composable
fun LookupScreen(
    uiState: LookupUiState,
    onEvent: (LookupEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = 20.dp,
            vertical = 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Text(
                text = "Probstat Table Lookup",
                style = MaterialTheme.typography.headlineMedium,
            )
        }

        item {
            Text(
                text = "Choose a probability distribution, enter its parameters, and calculate the cumulative probability.",
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        item {
            DistributionSelector(
                selectedDistribution = uiState.selectedDistribution,
                onDistributionSelected = { distribution ->
                    onEvent(
                        LookupEvent.DistributionChanged(distribution),
                    )
                },
            )
        }

        item {
            when (uiState.selectedDistribution) {
                DistributionType.BINOMIAL -> {
                    BinomialInputForm(
                        inputState = uiState.binomialInput,
                        onEvent = onEvent,
                    )
                }

                DistributionType.POISSON -> {
                    PoissonInputForm(
                        inputState = uiState.poissonInput,
                        onEvent = onEvent,
                    )
                }

                DistributionType.STANDARD_NORMAL -> {
                    StandardNormalInputForm(
                        inputState = uiState.normalInput,
                        curvePoints = uiState.normalCurvePoints,
                        onEvent = onEvent,
                    )
                }
            }
        }

        uiState.calculationResult?.let { result ->
            item {
                ProbabilityResultCard(probability = result)
            }
        }
    }
}
