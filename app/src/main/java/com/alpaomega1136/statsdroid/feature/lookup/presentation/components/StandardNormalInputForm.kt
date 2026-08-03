package com.alpaomega1136.statsdroid.feature.lookup.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.alpaomega1136.statsdroid.core.statistics.model.NormalCurvePoint
import com.alpaomega1136.statsdroid.feature.lookup.domain.validation.StandardNormalInputValidator
import com.alpaomega1136.statsdroid.feature.lookup.presentation.LookupEvent
import com.alpaomega1136.statsdroid.feature.lookup.presentation.NormalInputState
import com.alpaomega1136.statsdroid.ui.components.StatsPrimaryButton
import com.alpaomega1136.statsdroid.ui.components.StatsSectionCard
import com.alpaomega1136.statsdroid.ui.theme.SmallControlShape
import com.alpaomega1136.statsdroid.ui.theme.StatsSpacing
import java.util.Locale

@Composable
fun StandardNormalInputForm(
    inputState: NormalInputState,
    curvePoints: List<NormalCurvePoint>,
    onEvent: (LookupEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    StatsSectionCard(
        title = "Standard Normal Distribution",
        subtitle = "P(Z \u2264 z) for Z \u223C N(\u03BC = 0, \u03C3 = 1)",
        modifier = modifier,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(StatsSpacing.Medium)) {
            OutlinedTextField(
                value = inputState.zScoreText,
                onValueChange = { value -> onEvent(LookupEvent.NormalZTextChanged(value)) },
                label = { Text(text = "Z-Score") },
                placeholder = { Text(text = "-5.0 to 5.0") },
                supportingText = inputState.zScoreError?.let { error ->
                    { Text(text = error, color = MaterialTheme.colorScheme.error) }
                },
                isError = inputState.zScoreError != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done,
                ),
                singleLine = true,
                shape = SmallControlShape,
                modifier = Modifier.fillMaxWidth(),
            )

            Column {
                Text(
                    text = String.format(Locale.US, "Selected z: %.2f", inputState.zScoreValue),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                )

                Slider(
                    value = inputState.zScoreValue.toFloat(),
                    onValueChange = { value -> onEvent(LookupEvent.NormalZSliderChanged(value.toDouble())) },
                    valueRange = StandardNormalInputValidator.MIN_Z_SCORE.toFloat()..
                        StandardNormalInputValidator.MAX_Z_SCORE.toFloat(),
                    steps = 99,
                    modifier = Modifier.fillMaxWidth(),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = "-5.0", style = MaterialTheme.typography.labelSmall)
                    Text(text = "0.0", style = MaterialTheme.typography.labelSmall)
                    Text(text = "5.0", style = MaterialTheme.typography.labelSmall)
                }
            }

            StandardNormalCurve(
                points = curvePoints,
                selectedZScore = inputState.zScoreValue,
                onZScoreSelected = { value ->
                    onEvent(LookupEvent.NormalZSliderChanged(value))
                },
            )

            Text(
                text = "The shaded area represents the cumulative probability P(Z \u2264 z).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            StatsPrimaryButton(
                text = "Calculate Area",
                onClick = { onEvent(LookupEvent.Calculate) },
            )
        }
    }
}
