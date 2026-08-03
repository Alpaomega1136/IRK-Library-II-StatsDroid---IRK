package com.alpaomega1136.statsdroid.feature.lookup.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alpaomega1136.statsdroid.feature.lookup.presentation.DistributionType
import com.alpaomega1136.statsdroid.feature.lookup.presentation.LookupEvent
import com.alpaomega1136.statsdroid.ui.components.StatsExpandableInfoCard
import com.alpaomega1136.statsdroid.ui.theme.StatsSpacing

private data class LookupExample(
    val label: String,
    val explanation: String,
    val events: List<LookupEvent>,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LookupQuickExamples(
    distribution: DistributionType,
    onEvent: (LookupEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val examples = when (distribution) {
        DistributionType.BINOMIAL -> listOf(
            LookupExample(
                label = "10 fair tosses",
                explanation = "Calculate the probability of at most five successes in ten trials with p = 0.5.",
                events = listOf(
                    LookupEvent.BinomialTrialsChanged("10"),
                    LookupEvent.BinomialThresholdChanged("5"),
                    LookupEvent.BinomialProbabilityChanged(0.5),
                ),
            ),
            LookupExample(
                label = "Reliable process",
                explanation = "Explore a high success probability: at most 18 successes in 20 trials with p = 0.9.",
                events = listOf(
                    LookupEvent.BinomialTrialsChanged("20"),
                    LookupEvent.BinomialThresholdChanged("18"),
                    LookupEvent.BinomialProbabilityChanged(0.9),
                ),
            ),
        )

        DistributionType.POISSON -> listOf(
            LookupExample(
                label = "Four arrivals",
                explanation = "Calculate the probability of at most six arrivals when the average rate is four.",
                events = listOf(
                    LookupEvent.PoissonAverageRateChanged("4"),
                    LookupEvent.PoissonThresholdChanged("6"),
                ),
            ),
            LookupExample(
                label = "Low traffic",
                explanation = "Explore a smaller rate: at most two events when μ = 1.5.",
                events = listOf(
                    LookupEvent.PoissonAverageRateChanged("1.5"),
                    LookupEvent.PoissonThresholdChanged("2"),
                ),
            ),
        )

        DistributionType.STANDARD_NORMAL -> listOf(
            LookupExample(
                label = "One sigma",
                explanation = "Set z = 1.00 and observe the cumulative area to the left.",
                events = listOf(LookupEvent.NormalZSliderChanged(1.0)),
            ),
            LookupExample(
                label = "95% point",
                explanation = "Set z = 1.96, the common upper point for a central 95% interval.",
                events = listOf(LookupEvent.NormalZSliderChanged(1.96)),
            ),
            LookupExample(
                label = "Left tail",
                explanation = "Set z = -1.00 to compare the left-tail cumulative probability.",
                events = listOf(LookupEvent.NormalZSliderChanged(-1.0)),
            ),
        )
    }

    StatsExpandableInfoCard(
        title = "Try a guided example",
        summary = "Load a valid scenario and calculate it instantly.",
        modifier = modifier,
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(StatsSpacing.Small),
            verticalArrangement = Arrangement.spacedBy(StatsSpacing.Small),
        ) {
            examples.forEach { example ->
                AssistChip(
                    onClick = {
                        example.events.forEach(onEvent)
                        onEvent(LookupEvent.Calculate)
                    },
                    label = { Text(text = example.label) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                        )
                    },
                )
            }
        }

        Text(
            text = examples.joinToString(separator = "\n") { example ->
                "• ${example.label}: ${example.explanation}"
            },
        )
    }
}
