package com.alpaomega1136.statsdroid.feature.clt.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.feature.clt.domain.model.SimulationCount
import kotlin.math.roundToInt

@Composable
fun CltSimulationCountControl(
    selectedCount: SimulationCount,
    onCountSelected: (SimulationCount) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = SimulationCount.entries
    val selectedIndex = options.indexOf(selectedCount).coerceAtLeast(0)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Number of Samples (M)", style = MaterialTheme.typography.titleMedium)
        Text(text = selectedCount.displayName, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        Slider(
            value = selectedIndex.toFloat(),
            onValueChange = { sliderValue ->
                val index = sliderValue.roundToInt().coerceIn(0, options.lastIndex)
                onCountSelected(options[index])
            },
            valueRange = 0f..options.lastIndex.toFloat(),
            steps = options.size - 2,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            options.forEach { option ->
                Text(
                    text = option.displayName,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
        Text(
            text = "The application will calculate ${selectedCount.displayName} sample means.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
