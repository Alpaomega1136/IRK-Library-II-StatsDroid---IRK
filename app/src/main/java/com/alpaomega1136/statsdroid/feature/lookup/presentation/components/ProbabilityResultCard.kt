package com.alpaomega1136.statsdroid.feature.lookup.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alpaomega1136.statsdroid.ui.components.StatsMetricGrid
import com.alpaomega1136.statsdroid.ui.components.StatsMetricItem
import java.util.Locale

@Composable
fun ProbabilityResultCard(
    probability: Double?,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = probability != null,
        enter = fadeIn() + slideInVertically { it / 6 },
        exit = fadeOut() + slideOutVertically { it / 6 },
        modifier = modifier,
    ) {
        probability?.let { value ->
            StatsMetricGrid(
                items = listOf(
                    StatsMetricItem(
                        label = "Cumulative probability",
                        value = String.format(Locale.US, "%.6f", value),
                        subValue = "Decimal representation",
                        useMonospace = true,
                    ),
                    StatsMetricItem(
                        label = "Percentage",
                        value = String.format(Locale.US, "%.2f%%", value * 100.0),
                        subValue = "Area under the distribution",
                        useMonospace = true,
                    ),
                ),
            )
        }
    }
}
