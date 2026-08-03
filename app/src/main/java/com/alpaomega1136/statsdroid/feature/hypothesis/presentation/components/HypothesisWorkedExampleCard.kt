package com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestType
import com.alpaomega1136.statsdroid.ui.components.StatsSectionCard
import com.alpaomega1136.statsdroid.ui.theme.StatsSpacing

@Composable
fun HypothesisWorkedExampleCard(
    testType: HypothesisTestType,
    onLoadExample: () -> Unit,
    modifier: Modifier = Modifier,
) {
    StatsSectionCard(
        title = "Random example",
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(StatsSpacing.Medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "Generate a new valid ${testType.displayName} scenario and run it immediately.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
        }

        OutlinedButton(
            onClick = onLoadExample,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
            )
            Text(
                text = "Generate example",
                modifier = Modifier.weight(1f),
            )
        }
    }
}
