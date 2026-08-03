package com.alpaomega1136.statsdroid.feature.lookup.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Functions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.alpaomega1136.statsdroid.R
import com.alpaomega1136.statsdroid.feature.lookup.presentation.components.BinomialInputForm
import com.alpaomega1136.statsdroid.feature.lookup.presentation.components.DistributionSelector
import com.alpaomega1136.statsdroid.feature.lookup.presentation.components.PoissonInputForm
import com.alpaomega1136.statsdroid.feature.lookup.presentation.components.ProbabilityResultCard
import com.alpaomega1136.statsdroid.feature.lookup.presentation.components.StandardNormalInputForm
import com.alpaomega1136.statsdroid.ui.components.StatsHeroCard
import com.alpaomega1136.statsdroid.ui.theme.StatsSpacing

@Composable
fun LookupScreen(
    uiState: LookupUiState,
    onEvent: (LookupEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = StatsSpacing.Medium,
            vertical = StatsSpacing.Large,
        ),
        verticalArrangement = Arrangement.spacedBy(StatsSpacing.Medium),
    ) {
        item {
            StatsHeroCard(
                eyebrow = "Interactive Statistics Lab",
                title = stringResource(R.string.lookup_title),
                description = stringResource(R.string.lookup_description),
                icon = Icons.Default.Functions,
            )
        }

        item {
            DistributionSelector(
                selectedDistribution = uiState.selectedDistribution,
                onDistributionSelected = { distribution ->
                    onEvent(LookupEvent.DistributionChanged(distribution))
                },
            )
        }

        item {
            AnimatedContent(
                targetState = uiState.selectedDistribution,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "distribution_form_transition",
            ) { distribution ->
                when (distribution) {
                    DistributionType.BINOMIAL -> {
                        BinomialInputForm(
                            inputState = uiState.binomialInput,
                            onEvent = onEvent,
                        )
                    }

                    DistributionType.POISSON -> {
                        PoissonInputForm(
                            inputState = uiState.poissonInput,
                            onEvent = onEvent,
                        )
                    }

                    DistributionType.STANDARD_NORMAL -> {
                        StandardNormalInputForm(
                            inputState = uiState.normalInput,
                            curvePoints = uiState.normalCurvePoints,
                            onEvent = onEvent,
                        )
                    }
                }
            }
        }

        item {
            ProbabilityResultCard(probability = uiState.calculationResult)
        }
    }
}
