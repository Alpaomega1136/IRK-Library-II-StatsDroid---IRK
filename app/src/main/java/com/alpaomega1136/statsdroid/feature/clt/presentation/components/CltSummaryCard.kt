package com.alpaomega1136.statsdroid.feature.clt.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.R
import com.alpaomega1136.statsdroid.feature.clt.domain.model.CltSimulationResult
import java.util.Locale
import kotlin.math.abs

@Composable
fun CltSummaryCard(
    result: CltSimulationResult,
    modifier: Modifier = Modifier,
) {
    val meanDifference = abs(result.theoreticalMean - result.empiricalMean)

    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(text = stringResource(R.string.clt_summary), style = MaterialTheme.typography.titleLarge)
            Text(text = "Population: ${result.populationShape.displayName}", style = MaterialTheme.typography.bodyMedium)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryMetric(label = "Sample Size", value = result.sampleSize.toString(), modifier = Modifier.weight(1f))
                SummaryMetric(label = "Samples", value = result.numberOfSamples.toString(), modifier = Modifier.weight(1f))
            }

            HorizontalDivider()
            SummaryMetric(label = "Theoretical Mean", value = formatMetric(result.theoreticalMean))
            SummaryMetric(label = "Empirical Mean", value = formatMetric(result.empiricalMean))
            SummaryMetric(label = "Mean Difference", value = formatMetric(meanDifference))
            HorizontalDivider()
            SummaryMetric(label = "Population Std Dev", value = formatMetric(result.populationStandardDeviation))
            SummaryMetric(label = "Theoretical Standard Error", value = formatMetric(result.theoreticalStandardError))
            SummaryMetric(label = "Empirical Sampling Std Dev", value = formatMetric(result.empiricalSamplingStandardDeviation))

            Text(
                text = "As n increases, the standard error decreases, so the sampling distribution becomes narrower around the population mean.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SummaryMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.titleMedium)
    }
}

private fun formatMetric(value: Double): String = String.format(Locale.US, "%.6f", value)
