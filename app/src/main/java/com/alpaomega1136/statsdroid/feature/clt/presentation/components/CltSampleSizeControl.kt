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
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.feature.clt.domain.simulation.CltSimulator
import kotlin.math.roundToInt

@Composable
fun CltSampleSizeControl(
    sampleSize: Int,
    onSampleSizeChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Sample Size (n)", style = MaterialTheme.typography.titleMedium)
        Text(text = sampleSize.toString(), style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        Slider(
            value = sampleSize.toFloat(),
            onValueChange = { onSampleSizeChanged(it.roundToInt()) },
            valueRange = CltSimulator.MIN_SAMPLE_SIZE.toFloat()..CltSimulator.MAX_SAMPLE_SIZE.toFloat(),
            steps = 98,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "1", style = MaterialTheme.typography.labelSmall)
            Text(text = "50", style = MaterialTheme.typography.labelSmall)
            Text(text = "100", style = MaterialTheme.typography.labelSmall)
        }
        Text(
            text = "Each simulated sample contains $sampleSize observations.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
