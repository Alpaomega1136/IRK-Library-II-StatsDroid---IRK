package com.alpaomega1136.statsdroid.feature.lookup.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun ProbabilityResultCard(
    probability: Double,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Cumulative Probability",
                style = MaterialTheme.typography.titleMedium,
            )

            Text(
                text = String.format(
                    Locale.US,
                    "%.8f",
                    probability,
                ),
                style = MaterialTheme.typography.headlineMedium,
            )

            Text(
                text = String.format(
                    Locale.US,
                    "%.4f%%",
                    probability * 100.0,
                ),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
