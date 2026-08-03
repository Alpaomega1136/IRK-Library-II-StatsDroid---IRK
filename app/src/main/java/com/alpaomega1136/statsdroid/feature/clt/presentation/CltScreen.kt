package com.alpaomega1136.statsdroid.feature.clt.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import com.alpaomega1136.statsdroid.R
import com.alpaomega1136.statsdroid.feature.clt.presentation.components.CltControlPanel
import com.alpaomega1136.statsdroid.feature.clt.presentation.components.CltHistogramChart
import com.alpaomega1136.statsdroid.feature.clt.presentation.components.CltSummaryCard
import com.alpaomega1136.statsdroid.ui.components.StatsExpandableInfoCard
import com.alpaomega1136.statsdroid.ui.components.StatsHeroCard
import com.alpaomega1136.statsdroid.ui.components.StatsSectionCard
import com.alpaomega1136.statsdroid.ui.theme.StatsMotion
import com.alpaomega1136.statsdroid.ui.theme.StatsSpacing
import java.util.Locale

@Composable
fun CltScreen(
    uiState: CltUiState,
    onEvent: (CltEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val previousResultAlpha by animateFloatAsState(
        targetValue = if (uiState.isRunning) 0.56f else 1f,
        animationSpec = StatsMotion.FastFloatSpec,
        label = "clt_previous_result_alpha",
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = StatsSpacing.Medium,
            vertical = StatsSpacing.Large,
        ),
        verticalArrangement = Arrangement.spacedBy(StatsSpacing.Medium),
    ) {
        item {
            StatsHeroCard(
                eyebrow = "Sampling laboratory",
                title = stringResource(R.string.clt_title),
                description = "Draw repeated random samples and watch the distribution of their means approach a normal curve.",
                icon = Icons.Default.BarChart,
                badgeText = uiState.selectedPopulationShape.displayName,
            )
        }

        item {
            CltControlPanel(
                uiState = uiState,
                onEvent = onEvent,
            )
        }

        item {
            StatsExpandableInfoCard(
                title = "What should I observe?",
                summary = "Use the same population with n = 1, 30, and 100 to see the Central Limit Theorem emerge.",
            ) {
                Text(
                    text = "1. The original population histogram does not change when n changes.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "2. The sampling distribution becomes smoother and more bell-shaped as n grows.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "3. Its center stays near μ, while its spread approaches σ/√n.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        item {
            AnimatedVisibility(
                visible = uiState.isRunning,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                StatsSectionCard(
                    title = "Simulation in progress",
                    subtitle = "Drawing ${uiState.simulationCount.displayName} samples without blocking the interface.",
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(StatsSpacing.Small)) {
                        LinearProgressIndicator()
                        Text(
                            text = "The previous result remains visible below until the new simulation is complete.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        uiState.errorMessage?.let { message ->
            item {
                StatsSectionCard(
                    title = "Simulation could not be completed",
                    subtitle = "Review the configuration and run it again.",
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }

        val result = uiState.result

        if (result == null && !uiState.isRunning) {
            item {
                StatsSectionCard(
                    title = "Ready to explore the CLT",
                    subtitle = "Choose a population shape, sample size, and number of samples.",
                ) {
                    androidx.compose.foundation.layout.Row(
                        horizontalArrangement = Arrangement.spacedBy(StatsSpacing.Small),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = "Start with n = 1, then compare it with n = 30 or n = 100 to see the sampling distribution become smoother and narrower.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }

        result?.let { visualization ->
            val simulation = visualization.simulation

            item {
                CltHistogramChart(
                    title = "1. ${stringResource(R.string.clt_population_distribution)}",
                    description = "${simulation.populationShape.displayName} population with μ = ${formatShort(simulation.theoreticalMean)} and σ = ${formatShort(simulation.populationStandardDeviation)}.",
                    histogram = visualization.populationHistogram,
                    modifier = Modifier.alpha(previousResultAlpha),
                )
            }

            item {
                CltHistogramChart(
                    title = "2. ${stringResource(R.string.clt_sampling_distribution)}",
                    description = "${simulation.numberOfSamples} sample means, each calculated from n = ${simulation.sampleSize} observations.",
                    histogram = visualization.samplingDistributionHistogram,
                    theoreticalCurve = simulation.theoreticalNormalCurve,
                    modifier = Modifier.alpha(previousResultAlpha),
                )
            }

            item {
                CltSummaryCard(
                    result = simulation,
                    modifier = Modifier.alpha(previousResultAlpha),
                )
            }
        }
    }
}

private fun formatShort(
    value: Double,
): String = String.format(Locale.US, "%.4f", value)
