package com.alpaomega1136.statsdroid.feature.clt.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.R
import com.alpaomega1136.statsdroid.feature.clt.domain.model.CltSimulationResult
import com.alpaomega1136.statsdroid.ui.components.StatsMetricGrid
import com.alpaomega1136.statsdroid.ui.components.StatsMetricItem
import com.alpaomega1136.statsdroid.ui.components.StatsSectionCard
import com.alpaomega1136.statsdroid.ui.theme.StatsSpacing
import java.util.Locale
import kotlin.math.abs

@Composable
fun CltSummaryCard(
    result: CltSimulationResult,
    modifier: Modifier = Modifier,
) {
    val meanDifference = abs(result.theoreticalMean - result.empiricalMean)

    StatsSectionCard(
        title = stringResource(R.string.clt_summary),
        subtitle = "Population shape: ${result.populationShape.displayName}",
        modifier = modifier,
    ) {
        Column {
            StatsMetricGrid(
                items = listOf(
                    StatsMetricItem(
                        label = "Sample size (n)",
                        value = result.sampleSize.toString(),
                        subValue = "Observations per sample",
                        useMonospace = true,
                    ),
                    StatsMetricItem(
                        label = "Simulations (M)",
                        value = result.numberOfSamples.toString(),
                        subValue = "Sample means generated",
                        useMonospace = true,
                    ),
                    StatsMetricItem(
                        label = "Theoretical mean (μ)",
                        value = formatMetric(result.theoreticalMean),
                        subValue = "Population parameter",
                        useMonospace = true,
                    ),
                    StatsMetricItem(
                        label = "Empirical mean",
                        value = formatMetric(result.empiricalMean),
                        subValue = "Difference: ${formatMetric(meanDifference)}",
                        useMonospace = true,
                    ),
                    StatsMetricItem(
                        label = "Theoretical SE (σ/√n)",
                        value = formatMetric(result.theoreticalStandardError),
                        subValue = "Expected spread of sample means",
                        useMonospace = true,
                    ),
                    StatsMetricItem(
                        label = "Empirical sampling SD",
                        value = formatMetric(result.empiricalSamplingStandardDeviation),
                        subValue = "Observed spread of sample means",
                        useMonospace = true,
                    ),
                ),
            )

            Spacer(modifier = Modifier.height(StatsSpacing.Medium))
            EducationalInsightCard()
        }
    }
}

@Composable
private fun EducationalInsightCard(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f))
            .padding(StatsSpacing.Medium),
    ) {
        Column {
            Text(
                text = "What to observe",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            Spacer(modifier = Modifier.height(StatsSpacing.ExtraSmall))

            Text(
                text = "As n increases, σ/√n becomes smaller. The sampling distribution therefore becomes narrower around μ, even when the original population is skewed or bimodal.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

private fun formatMetric(
    value: Double,
): String = String.format(Locale.US, "%.4f", value)
