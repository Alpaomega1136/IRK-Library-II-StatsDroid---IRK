package com.alpaomega1136.statsdroid.feature.lookup.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.feature.lookup.presentation.DistributionType
import com.alpaomega1136.statsdroid.ui.components.StatsSectionCard
import com.alpaomega1136.statsdroid.ui.theme.StatsSpacing
import java.util.Locale

@Composable
fun ProbabilityVisualizationCard(
    distribution: DistributionType,
    probability: Double,
    modifier: Modifier = Modifier,
) {
    val boundedProbability = probability.coerceIn(0.0, 1.0)
    val eventLabel = when (distribution) {
        DistributionType.BINOMIAL -> "P(X ≤ r)"
        DistributionType.POISSON -> "P(X ≤ r)"
        DistributionType.STANDARD_NORMAL -> "P(Z ≤ z)"
    }

    StatsSectionCard(
        title = "Result visualization",
        subtitle = eventLabel,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(StatsSpacing.Medium),
        ) {
            Box(
                modifier = Modifier.size(164.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    progress = { boundedProbability.toFloat() },
                    modifier = Modifier.size(152.dp),
                    strokeWidth = 14.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = String.format(
                            Locale.US,
                            "%.2f%%",
                            boundedProbability * 100.0,
                        ),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = eventLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                ProbabilityLegend(
                    label = "Cumulative",
                    value = boundedProbability,
                )
                ProbabilityLegend(
                    label = "Remaining",
                    value = 1.0 - boundedProbability,
                    alignEnd = true,
                )
            }
        }
    }
}

@Composable
private fun ProbabilityLegend(
    label: String,
    value: Double,
    alignEnd: Boolean = false,
) {
    Column(
        horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = String.format(Locale.US, "%.4f", value),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
