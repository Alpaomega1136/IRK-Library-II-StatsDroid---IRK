package com.alpaomega1136.statsdroid.feature.lookup.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alpaomega1136.statsdroid.feature.lookup.presentation.DistributionType
import com.alpaomega1136.statsdroid.ui.components.StatsExpandableInfoCard
import java.util.Locale

@Composable
fun LookupResultInsight(
    distribution: DistributionType,
    probability: Double,
    modifier: Modifier = Modifier,
) {
    val eventDescription = when (distribution) {
        DistributionType.BINOMIAL ->
            "The result represents P(X ≤ r): the probability of observing at most the selected number of successes."
        DistributionType.POISSON ->
            "The result represents P(X ≤ r): the probability of observing at most the selected number of events."
        DistributionType.STANDARD_NORMAL ->
            "The result represents P(Z ≤ z): the area under the standard normal curve to the left of the selected z-score."
    }

    StatsExpandableInfoCard(
        title = "How to interpret the result",
        summary = "This is a cumulative probability equal to ${formatPercentage(probability)}.",
        modifier = modifier,
    ) {
        Text(
            text = eventDescription,
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = "Its complement is ${formatPercentage(1.0 - probability)}, which represents the probability to the right of the threshold.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun formatPercentage(value: Double): String {
    return String.format(Locale.US, "%.4f%%", value.coerceIn(0.0, 1.0) * 100.0)
}
