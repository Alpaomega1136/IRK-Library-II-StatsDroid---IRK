package com.alpaomega1136.statsdroid.feature.lookup.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.alpaomega1136.statsdroid.feature.lookup.presentation.LookupEvent
import com.alpaomega1136.statsdroid.feature.lookup.presentation.PoissonInputState
import com.alpaomega1136.statsdroid.ui.components.StatsPrimaryButton
import com.alpaomega1136.statsdroid.ui.components.StatsSectionCard
import com.alpaomega1136.statsdroid.ui.theme.SmallControlShape
import com.alpaomega1136.statsdroid.ui.theme.StatsSpacing

@Composable
fun PoissonInputForm(
    inputState: PoissonInputState,
    onEvent: (LookupEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    StatsSectionCard(
        title = "Poisson Parameters",
        subtitle = "P(X \u2264 r) for X \u223C Poisson(\u03BC)",
        modifier = modifier,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(StatsSpacing.Medium)) {
            OutlinedTextField(
                value = inputState.averageRate,
                onValueChange = { value ->
                    onEvent(LookupEvent.PoissonAverageRateChanged(value))
                },
                label = { Text(text = "Average Rate (\u03BC)") },
                placeholder = { Text(text = "0 < \u03BC \u2264 100") },
                supportingText = {
                    inputState.averageRateError?.let { error ->
                        Text(text = error, color = MaterialTheme.colorScheme.error)
                    } ?: Text(text = "Decimal values allowed (e.g. 2.5).")
                },
                isError = inputState.averageRateError != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next,
                ),
                singleLine = true,
                shape = SmallControlShape,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = inputState.threshold,
                onValueChange = { value ->
                    onEvent(LookupEvent.PoissonThresholdChanged(value))
                },
                label = { Text(text = "Success Threshold (r)") },
                placeholder = { Text(text = "r \u2265 0") },
                supportingText = {
                    inputState.thresholdError?.let { error ->
                        Text(text = error, color = MaterialTheme.colorScheme.error)
                    } ?: Text(text = "Supports large thresholds (> 30) with optimized convergence.")
                },
                isError = inputState.thresholdError != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                singleLine = true,
                shape = SmallControlShape,
                modifier = Modifier.fillMaxWidth(),
            )

            StatsPrimaryButton(
                text = "Calculate Probability",
                onClick = { onEvent(LookupEvent.Calculate) },
            )
        }
    }
}
