package com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.TailType
import com.alpaomega1136.statsdroid.ui.components.StatsSectionCard
import com.alpaomega1136.statsdroid.ui.theme.SmallControlShape
import com.alpaomega1136.statsdroid.ui.theme.StatsSpacing

@Composable
fun HypothesisStatementCard(
    hypothesizedMeanText: String,
    tailType: TailType,
    modifier: Modifier = Modifier,
) {
    val displayedMean = hypothesizedMeanText.ifBlank { "μ₀" }
    val alternativeSymbol = when (tailType) {
        TailType.TWO_TAILED -> "≠"
        TailType.RIGHT_TAILED -> ">"
        TailType.LEFT_TAILED -> "<"
    }

    StatsSectionCard(
        title = "Hypothesis formulation",
        subtitle = "The statements update automatically as you change the baseline and tail direction.",
        modifier = modifier,
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val compact = maxWidth < 520.dp

            if (compact) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(StatsSpacing.Small),
                ) {
                    StatementPanel(
                        label = "Null hypothesis",
                        formula = "H₀: μ = $displayedMean",
                        selectedColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    StatementPanel(
                        label = "Alternative hypothesis",
                        formula = "H₁: μ $alternativeSymbol $displayedMean",
                        selectedColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(StatsSpacing.Medium),
                ) {
                    StatementPanel(
                        label = "Null hypothesis",
                        formula = "H₀: μ = $displayedMean",
                        selectedColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f),
                    )
                    StatementPanel(
                        label = "Alternative hypothesis",
                        formula = "H₁: μ $alternativeSymbol $displayedMean",
                        selectedColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun StatementPanel(
    label: String,
    formula: String,
    selectedColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = SmallControlShape,
        color = selectedColor.copy(alpha = 0.09f),
        border = BorderStroke(1.dp, selectedColor.copy(alpha = 0.32f)),
    ) {
        Column(
            modifier = Modifier.padding(StatsSpacing.Medium),
            verticalArrangement = Arrangement.spacedBy(StatsSpacing.ExtraSmall),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = formula,
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = selectedColor,
            )
        }
    }
}
