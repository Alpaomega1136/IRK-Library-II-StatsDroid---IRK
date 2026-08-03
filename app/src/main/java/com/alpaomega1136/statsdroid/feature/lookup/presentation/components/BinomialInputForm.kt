package com.alpaomega1136.statsdroid.feature.lookup.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.feature.lookup.presentation.BinomialInputState
import com.alpaomega1136.statsdroid.feature.lookup.presentation.LookupEvent
import com.alpaomega1136.statsdroid.ui.components.StatsPrimaryButton
import com.alpaomega1136.statsdroid.ui.components.StatsSectionCard
import com.alpaomega1136.statsdroid.ui.theme.SmallControlShape
import com.alpaomega1136.statsdroid.ui.theme.StatsSpacing
import java.util.Locale

private val binomialProbabilityOptions = (1..9).map { value -> value / 10.0 }

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BinomialInputForm(
    inputState: BinomialInputState,
    onEvent: (LookupEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    StatsSectionCard(
        title = "Binomial Parameters",
        subtitle = "P(X \u2264 r) for X \u223C Binomial(n, p)",
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
                placeholder = { Text(text = "0 \u2264 r \u2264 n") },
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

            Column {
                Text(
                    text = "Success Probability (p): ${String.format(Locale.US, "%.1f", inputState.successProbability)}",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.height(StatsSpacing.Small))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(StatsSpacing.Small),
                    verticalArrangement = Arrangement.spacedBy(StatsSpacing.ExtraSmall),
                ) {
                    binomialProbabilityOptions.forEach { probability ->
                        val isSelected = Math.abs(probability - inputState.successProbability) < 1e-4

                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                onEvent(LookupEvent.BinomialProbabilityChanged(probability))
                            },
                            label = {
                                Text(
                                    text = String.format(Locale.US, "%.1f", probability),
                                    fontFamily = FontFamily.Monospace,
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            ),
                        )
                    }
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
