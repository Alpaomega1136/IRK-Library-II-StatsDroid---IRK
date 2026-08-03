package com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.CriticalValues
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestResult
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestType
import com.alpaomega1136.statsdroid.ui.components.StatsDecisionBanner
import com.alpaomega1136.statsdroid.ui.components.StatsMetricGrid
import com.alpaomega1136.statsdroid.ui.components.StatsMetricItem
import com.alpaomega1136.statsdroid.ui.theme.StatsSpacing
import java.util.Locale

@Composable
fun HypothesisResultCard(
    result: HypothesisTestResult?,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = result != null,
        enter = fadeIn() + slideInVertically { it / 6 },
        exit = fadeOut() + slideOutVertically { it / 6 },
        modifier = modifier,
    ) {
        result?.let { testResult ->
            val statisticLabel = when (testResult.testType) {
                HypothesisTestType.Z_TEST -> "Z statistic"
                HypothesisTestType.T_TEST -> "t statistic"
            }

            val metrics = buildList {
                add(
                    StatsMetricItem(
                        label = statisticLabel,
                        value = String.format(Locale.US, "%.4f", testResult.testStatistic),
                        subValue = testResult.testType.displayName,
                        useMonospace = true,
                    ),
                )
                add(
                    StatsMetricItem(
                        label = "p-value",
                        value = formatProbability(testResult.pValue),
                        subValue = "Probability of an equally or more extreme result",
                        useMonospace = true,
                    ),
                )
                add(
                    StatsMetricItem(
                        label = "Critical value(s)",
                        value = formatCriticalValues(testResult.criticalValues),
                        subValue = "Boundary of the rejection region",
                        useMonospace = true,
                    ),
                )

                testResult.degreesOfFreedom?.let { degreesOfFreedom ->
                    add(
                        StatsMetricItem(
                            label = "Degrees of freedom",
                            value = degreesOfFreedom.toString(),
                            subValue = "df = n - 1",
                            useMonospace = true,
                        ),
                    )
                } ?: add(
                    StatsMetricItem(
                        label = "Significance level (α)",
                        value = String.format(Locale.US, "%.2f", testResult.significanceLevel),
                        subValue = "Decision threshold",
                        useMonospace = true,
                    ),
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(StatsSpacing.Medium),
            ) {
                StatsDecisionBanner(
                    decision = testResult.decision,
                    pValue = testResult.pValue,
                    significanceLevel = testResult.significanceLevel,
                )

                StatsMetricGrid(items = metrics)
            }
        }
    }
}

private fun formatCriticalValues(
    criticalValues: CriticalValues,
): String {
    val lower = criticalValues.lower
    val upper = criticalValues.upper

    return when {
        lower != null && upper != null -> "${formatShort(lower)} / ${formatShort(upper)}"
        lower != null -> formatShort(lower)
        upper != null -> formatShort(upper)
        else -> "-"
    }
}

private fun formatProbability(
    value: Double,
): String {
    return if (value in 0.0..<0.0001) {
        String.format(Locale.US, "%.3e", value)
    } else {
        String.format(Locale.US, "%.4f", value)
    }
}

private fun formatShort(
    value: Double,
): String = String.format(Locale.US, "%.3f", value)
