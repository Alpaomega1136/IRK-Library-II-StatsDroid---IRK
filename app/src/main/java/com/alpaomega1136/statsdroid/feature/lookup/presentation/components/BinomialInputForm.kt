package com.alpaomega1136.statsdroid.feature.lookup.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.alpaomega1136.statsdroid.feature.lookup.presentation.BinomialInputState
import com.alpaomega1136.statsdroid.feature.lookup.presentation.LookupEvent
import com.alpaomega1136.statsdroid.ui.components.StatsPrimaryButton
import com.alpaomega1136.statsdroid.ui.components.StatsSectionCard
import com.alpaomega1136.statsdroid.ui.theme.SmallControlShape
import com.alpaomega1136.statsdroid.ui.theme.StatsSpacing
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun BinomialInputForm(
    inputState: BinomialInputState,
    onEvent: (LookupEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    StatsSectionCard(
        title = "Binomial Parameters",
        subtitle = "P(X ≤ r) for X ∼ Binomial(n, p)",
        modifier = modifier,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(StatsSpacing.Medium)) {
            OutlinedTextField(
                value = inputState.numberOfTrials,
                onValueChange = { value ->
                    onEvent(LookupEvent.BinomialTrialsChanged(value))
                },
                label = { Text(text = "Number of trials (n)") },
                placeholder = { Text(text = "1-20") },
                supportingText = {
                    inputState.numberOfTrialsError?.let { error ->
                        Text(text = error, color = MaterialTheme.colorScheme.error)
                    }
                },
                isError = inputState.numberOfTrialsError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = SmallControlShape,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = inputState.threshold,
                onValueChange = { value ->
                    onEvent(LookupEvent.BinomialThresholdChanged(value))
                },
                label = { Text(text = "Success threshold (r)") },
                placeholder = { Text(text = "0 ≤ r ≤ n") },
                supportingText = {
                    inputState.thresholdError?.let { error ->
                        Text(text = error, color = MaterialTheme.colorScheme.error)
                    }
                },
                isError = inputState.thresholdError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = SmallControlShape,
                modifier = Modifier.fillMaxWidth(),
            )

            Column(verticalArrangement = Arrangement.spacedBy(StatsSpacing.Small)) {
                Text(
                    text = String.format(
                        Locale.US,
                        "Success probability (p): %.2f",
                        inputState.successProbability,
                    ),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = MaterialTheme.colorScheme.primary,
                )

                Slider(
                    value = inputState.successProbability.toFloat(),
                    onValueChange = { value ->
                        val roundedValue =
                            (value.toDouble() * 100.0).roundToInt() / 100.0
                        onEvent(
                            LookupEvent.BinomialProbabilityChanged(roundedValue),
                        )
                    },
                    valueRange = 0.10f..0.90f,
                    steps = 79,
                    modifier = Modifier.fillMaxWidth(),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = "0.10", style = MaterialTheme.typography.labelSmall)
                    Text(text = "0.50", style = MaterialTheme.typography.labelSmall)
                    Text(text = "0.90", style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(StatsSpacing.Small))

            StatsPrimaryButton(
                text = "Calculate Probability",
                onClick = { onEvent(LookupEvent.Calculate) },
            )
        }
    }
}
