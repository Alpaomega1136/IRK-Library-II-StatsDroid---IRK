package com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisDecision
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestResult
import com.alpaomega1136.statsdroid.ui.components.StatsExpandableInfoCard
import java.util.Locale

@Composable
fun HypothesisInterpretationCard(
    result: HypothesisTestResult,
    modifier: Modifier = Modifier,
) {
    val comparison = if (result.pValue <= result.significanceLevel) "≤" else ">"
    val conclusion = when (result.decision) {
        HypothesisDecision.REJECT_NULL ->
            "The sample provides sufficient evidence against H₀ at the selected significance level."
        HypothesisDecision.FAIL_TO_REJECT_NULL ->
            "The sample does not provide sufficient evidence to reject H₀ at the selected significance level."
    }

    StatsExpandableInfoCard(
        title = "Why was this decision made?",
        summary = String.format(
            Locale.US,
            "p-value %.6f %s α %.2f",
            result.pValue,
            comparison,
            result.significanceLevel,
        ),
        modifier = modifier,
    ) {
        Text(
            text = conclusion,
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = "The test statistic is compared with the rejection region, while the p-value expresses how extreme the observed statistic is under H₀.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "Failing to reject H₀ does not prove that H₀ is true; it means the current evidence is not strong enough to reject it.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
