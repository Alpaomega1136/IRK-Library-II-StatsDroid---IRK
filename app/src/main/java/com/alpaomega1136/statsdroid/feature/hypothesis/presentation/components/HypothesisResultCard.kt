package com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.CriticalValues
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisDecision
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestResult
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestType
import java.util.Locale

@Composable
fun HypothesisResultCard(
    result: HypothesisTestResult,
    modifier: Modifier = Modifier,
) {
    val containerColor = when (result.decision) {
        HypothesisDecision.REJECT_NULL -> MaterialTheme.colorScheme.errorContainer
        HypothesisDecision.FAIL_TO_REJECT_NULL -> MaterialTheme.colorScheme.secondaryContainer
    }
    val statisticLabel = when (result.testType) {
        HypothesisTestType.Z_TEST -> "Z-score"
        HypothesisTestType.T_TEST -> "t-score"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = "Test Result", style = MaterialTheme.typography.titleLarge)
            Text(text = result.decision.displayName, style = MaterialTheme.typography.headlineSmall)
            HorizontalDivider()
            ResultLine(label = "Test type", value = result.testType.displayName)
            ResultLine(label = statisticLabel, value = formatNumber(result.testStatistic))
            ResultLine(label = "p-value", value = formatProbability(result.pValue))
            ResultLine(label = "Significance level", value = formatNumber(result.significanceLevel))
            ResultLine(label = "Critical value", value = formatCriticalValues(result.criticalValues))
            result.degreesOfFreedom?.let { ResultLine(label = "Degrees of freedom", value = it.toString()) }
            HorizontalDivider()
            Text(
                text = if (result.decision == HypothesisDecision.REJECT_NULL) {
                    "The p-value is less than or equal to alpha, so there is sufficient evidence to reject H0."
                } else {
                    "The p-value is greater than alpha, so there is insufficient evidence to reject H0."
                },
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun ResultLine(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

private fun formatCriticalValues(criticalValues: CriticalValues): String {
    val lower = criticalValues.lower
    val upper = criticalValues.upper

    return when {
        lower != null && upper != null -> "${formatNumber(lower)} and ${formatNumber(upper)}"
        lower != null -> formatNumber(lower)
        upper != null -> formatNumber(upper)
        else -> "-"
    }
}

private fun formatProbability(value: Double): String {
    return if (value in 0.0..<0.000001) {
        String.format(Locale.US, "%.4e", value)
    } else {
        String.format(Locale.US, "%.8f", value)
    }
}

private fun formatNumber(value: Double): String = String.format(Locale.US, "%.6f", value)
