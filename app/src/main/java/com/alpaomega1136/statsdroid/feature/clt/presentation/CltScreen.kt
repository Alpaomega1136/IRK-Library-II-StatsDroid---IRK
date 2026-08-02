package com.alpaomega1136.statsdroid.feature.clt.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.R
import com.alpaomega1136.statsdroid.feature.clt.presentation.components.CltControlPanel
import com.alpaomega1136.statsdroid.feature.clt.presentation.components.CltHistogramChart
import com.alpaomega1136.statsdroid.feature.clt.presentation.components.CltSummaryCard
import java.util.Locale

@Composable
fun CltScreen(
    uiState: CltUiState,
    onEvent: (CltEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Text(text = stringResource(R.string.clt_title), style = MaterialTheme.typography.headlineMedium)
        }

        item {
            Text(
                text = "Draw repeated random samples and observe how the distribution of their means approaches a normal distribution.",
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        item {
            CltControlPanel(uiState = uiState, onEvent = onEvent)
        }

        uiState.errorMessage?.let { errorMessage ->
            item {
                Card {
                    Text(
                        text = errorMessage,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }

        val result = uiState.result

        if (result == null && !uiState.isRunning) {
            item {
                Card {
                    Text(
                        text = "Configure the simulation, then press \"Simulate / Draw Samples\" to generate the population and sampling distributions.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        result?.let { visualization ->
            val simulation = visualization.simulation

            item {
                CltHistogramChart(
                    title = "1. ${stringResource(R.string.clt_population_distribution)}",
                    description = "Original ${simulation.populationShape.displayName} population with mean = ${formatShort(simulation.theoreticalMean)} and std dev = ${formatShort(simulation.populationStandardDeviation)}.",
                    histogram = visualization.populationHistogram,
                )
            }

            item {
                CltHistogramChart(
                    title = "2. ${stringResource(R.string.clt_sampling_distribution)}",
                    description = "${simulation.numberOfSamples} sample means, each calculated from n = ${simulation.sampleSize} observations.",
                    histogram = visualization.samplingDistributionHistogram,
                    theoreticalCurve = simulation.theoreticalNormalCurve,
                )
            }

            item {
                CltSummaryCard(result = simulation)
            }
        }
    }
}

private fun formatShort(value: Double): String = String.format(Locale.US, "%.4f", value)
