package com.alpaomega1136.statsdroid.feature.hypothesis.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.R
import com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components.HypothesisDistributionChart
import com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components.HypothesisInputForm
import com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components.HypothesisInterpretationCard
import com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components.HypothesisResultCard
import com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components.HypothesisStatementCard
import com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components.HypothesisTestTypeSelector
import com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components.HypothesisWorkedExampleCard
import com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components.SignificanceLevelSelector
import com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components.TailTypeSelector
import com.alpaomega1136.statsdroid.ui.components.StatsHeroCard
import com.alpaomega1136.statsdroid.ui.components.StatsPrimaryButton
import com.alpaomega1136.statsdroid.ui.components.StatsSectionCard
import com.alpaomega1136.statsdroid.ui.theme.StatsSpacing

@Composable
fun HypothesisScreen(
    uiState: HypothesisUiState,
    onEvent: (HypothesisEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = StatsSpacing.Medium, vertical = StatsSpacing.Large),
        verticalArrangement = Arrangement.spacedBy(StatsSpacing.Medium),
    ) {
        item {
            StatsHeroCard(
                eyebrow = "Visual Hypothesis Tester",
                title = stringResource(R.string.hypothesis_title),
                description = stringResource(R.string.hypothesis_description),
                icon = Icons.Default.CheckCircle,
                badgeText = uiState.selectedTestType.displayName,
            )
        }

        item {
            HypothesisTestTypeSelector(
                selectedTestType = uiState.selectedTestType,
                onTestTypeSelected = { onEvent(HypothesisEvent.TestTypeChanged(it)) },
            )
        }

        item {
            HypothesisWorkedExampleCard(
                testType = uiState.selectedTestType,
                onLoadExample = { onEvent(HypothesisEvent.LoadWorkedExample) },
            )
        }

        item {
            HypothesisStatementCard(
                hypothesizedMeanText = uiState.input.hypothesizedMean,
                tailType = uiState.tailType,
            )
        }

        item {
            HypothesisInputForm(
                testType = uiState.selectedTestType,
                inputState = uiState.input,
                onEvent = onEvent,
            )
        }

        item {
            StatsSectionCard(
                title = "Test Configuration",
                subtitle = "Significance level and rejection tail direction",
            ) {
                SignificanceLevelSelector(
                    selectedLevel = uiState.significanceLevel,
                    onLevelSelected = { onEvent(HypothesisEvent.SignificanceLevelChanged(it)) },
                )

                TailTypeSelector(
                    selectedTailType = uiState.tailType,
                    onTailTypeSelected = { onEvent(HypothesisEvent.TailTypeChanged(it)) },
                )
            }
        }

        item {
            StatsPrimaryButton(
                text = stringResource(R.string.run_hypothesis_test),
                onClick = { onEvent(HypothesisEvent.Calculate) },
            )
        }

        uiState.result?.let { result ->
            item {
                HypothesisResultCard(result = result)
            }

            item {
                HypothesisInterpretationCard(result = result)
            }

            uiState.visualization?.let { visualization ->
                item {
                    HypothesisDistributionChart(
                        result = result,
                        visualization = visualization,
                    )
                }
            }
        }
    }
}
