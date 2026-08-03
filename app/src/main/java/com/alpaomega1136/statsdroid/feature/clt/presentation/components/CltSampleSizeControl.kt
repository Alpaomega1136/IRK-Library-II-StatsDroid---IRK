package com.alpaomega1136.statsdroid.feature.clt.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.feature.clt.domain.simulation.CltSimulator
import com.alpaomega1136.statsdroid.ui.theme.StatsSpacing
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CltSampleSizeControl(
    sampleSize: Int,
    onSampleSizeChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = "Sample Size (n)", style = MaterialTheme.typography.titleMedium)
        Text(
            text = sampleSize.toString(),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(StatsSpacing.Small),
            verticalArrangement = Arrangement.spacedBy(StatsSpacing.Small),
        ) {
            listOf(
                1 to "Original shape",
                30 to "CLT baseline",
                100 to "Narrow mean",
            ).forEach { (value, label) ->
                FilterChip(
                    selected = sampleSize == value,
                    onClick = { onSampleSizeChanged(value) },
                    enabled = enabled,
                    label = { Text(text = "$label · n=$value") },
                )
            }
        }

        Slider(
            value = sampleSize.toFloat(),
            onValueChange = { onSampleSizeChanged(it.roundToInt()) },
            valueRange = CltSimulator.MIN_SAMPLE_SIZE.toFloat()..CltSimulator.MAX_SAMPLE_SIZE.toFloat(),
            steps = 98,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "1", style = MaterialTheme.typography.labelSmall)
            Text(text = "50", style = MaterialTheme.typography.labelSmall)
            Text(text = "100", style = MaterialTheme.typography.labelSmall)
        }
        Text(
            text = when {
                sampleSize == 1 ->
                    "With n = 1, the sampling distribution still resembles the original population."
                sampleSize < 30 ->
                    "The sample means are beginning to smooth out, but population shape can still be visible."
                sampleSize < 80 ->
                    "A common CLT range: the sample means should look more normal and concentrate near μ."
                else ->
                    "A large n produces a smaller standard error and a visibly narrower sampling distribution."
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
