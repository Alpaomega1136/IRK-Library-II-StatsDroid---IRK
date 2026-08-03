package com.alpaomega1136.statsdroid.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisDecision
import com.alpaomega1136.statsdroid.ui.theme.ResultBannerShape
import com.alpaomega1136.statsdroid.ui.theme.StatsSpacing
import java.util.Locale

@Composable
fun StatsDecisionBanner(
    decision: HypothesisDecision,
    pValue: Double,
    significanceLevel: Double,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = decision,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "decision_banner_transition",
        modifier = modifier,
    ) { currentDecision ->
        val rejectNull = currentDecision == HypothesisDecision.REJECT_NULL
        val containerColor = if (rejectNull) {
            MaterialTheme.colorScheme.errorContainer
        } else {
            MaterialTheme.colorScheme.secondaryContainer
        }
        val contentColor = if (rejectNull) {
            MaterialTheme.colorScheme.onErrorContainer
        } else {
            MaterialTheme.colorScheme.onSecondaryContainer
        }
        val accentColor = if (rejectNull) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.secondary
        }
        val title = if (rejectNull) {
            "Reject the null hypothesis (H₀)"
        } else {
            "Fail to reject the null hypothesis (H₀)"
        }
        val comparison = if (rejectNull) {
            "less than or equal to"
        } else {
            "greater than"
        }
        val explanation =
            "The p-value (${String.format(Locale.US, "%.4f", pValue)}) is $comparison α (${String.format(Locale.US, "%.2f", significanceLevel)}). " +
                if (rejectNull) {
                    "The result provides statistically significant evidence against H₀."
                } else {
                    "The result does not provide enough evidence to reject H₀."
                }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = ResultBannerShape,
            colors = CardDefaults.cardColors(containerColor = containerColor),
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(StatsSpacing.Large),
            ) {
                val compact = maxWidth < 380.dp

                if (compact) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(StatsSpacing.Medium),
                    ) {
                        DecisionIcon(
                            rejectNull = rejectNull,
                            title = title,
                            accentColor = accentColor,
                        )
                        DecisionCopy(
                            title = title,
                            explanation = explanation,
                            contentColor = contentColor,
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(StatsSpacing.Medium),
                    ) {
                        DecisionIcon(
                            rejectNull = rejectNull,
                            title = title,
                            accentColor = accentColor,
                        )
                        DecisionCopy(
                            title = title,
                            explanation = explanation,
                            contentColor = contentColor,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DecisionIcon(
    rejectNull: Boolean,
    title: String,
    accentColor: Color,
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .padding(2.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = if (rejectNull) Icons.Default.Cancel else Icons.Default.CheckCircle,
            contentDescription = title,
            tint = accentColor,
            modifier = Modifier.size(34.dp),
        )
    }
}

@Composable
private fun DecisionCopy(
    title: String,
    explanation: String,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(StatsSpacing.ExtraSmall),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = contentColor,
        )
        Text(
            text = explanation,
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor,
        )
    }
}
