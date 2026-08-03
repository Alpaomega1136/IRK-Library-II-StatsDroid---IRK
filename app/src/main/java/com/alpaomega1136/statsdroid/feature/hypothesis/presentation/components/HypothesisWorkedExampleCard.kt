package com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestType
import com.alpaomega1136.statsdroid.ui.components.StatsExpandableInfoCard
import com.alpaomega1136.statsdroid.ui.theme.StatsSpacing

@Composable
fun HypothesisWorkedExampleCard(
    testType: HypothesisTestType,
    onLoadExample: () -> Unit,
    modifier: Modifier = Modifier,
) {
    StatsExpandableInfoCard(
        title = "Worked example",
        summary = "Compare how the same sample can lead to a different Z-Test and t-Test decision.",
        modifier = modifier,
    ) {
        Text(
            text = "The example uses μ₀ = 50, x̄ = 52, standard deviation = 5, n = 25, α = 0.05, and a two-tailed alternative.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = when (testType) {
                HypothesisTestType.Z_TEST ->
                    "With a known population standard deviation, the Z statistic is 2.00 and the two-tailed decision should reject H₀."
                HypothesisTestType.T_TEST ->
                    "With a sample standard deviation and df = 24, the t statistic is also 2.00, but the wider t tails should fail to reject H₀."
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedButton(
            onClick = onLoadExample,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(StatsSpacing.Small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                )
                Text(text = "Load and run this example")
            }
        }
    }
}
