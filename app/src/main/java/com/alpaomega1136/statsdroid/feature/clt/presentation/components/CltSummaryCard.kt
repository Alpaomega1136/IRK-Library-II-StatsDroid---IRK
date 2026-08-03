package com.alpaomega1136.statsdroid.feature.clt.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.alpaomega1136.statsdroid.R
import com.alpaomega1136.statsdroid.feature.clt.domain.model.CltSimulationResult
import com.alpaomega1136.statsdroid.ui.components.StatsMetricGrid
import com.alpaomega1136.statsdroid.ui.components.StatsMetricItem
import com.alpaomega1136.statsdroid.ui.components.StatsSectionCard
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
    }
}

private fun formatMetric(
    value: Double,
): String = String.format(Locale.US, "%.4f", value)
