package com.alpaomega1136.statsdroid.feature.lookup.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.feature.lookup.presentation.LookupEvent
import com.alpaomega1136.statsdroid.feature.lookup.presentation.PoissonInputState

@Composable
fun PoissonInputForm(
    inputState: PoissonInputState,
    onEvent: (LookupEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Poisson Parameters",
            style = MaterialTheme.typography.titleMedium,
        )

        Text(
            text = "Calculate the cumulative probability P(X <= r).",
            style = MaterialTheme.typography.bodyMedium,
        )

        OutlinedTextField(
            value = inputState.averageRate,
            onValueChange = { value ->
                onEvent(LookupEvent.PoissonAverageRateChanged(value))
            },
            label = {
                Text(text = "Average rate (mu)")
            },
            placeholder = {
                Text(text = "Greater than 0, maximum 100")
            },
            supportingText = {
                inputState.averageRateError?.let { error ->
                    Text(text = error)
                } ?: Text(
                    text = "Decimal values are allowed, for example 2.5.",
                )
            },
            isError = inputState.averageRateError != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next,
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = inputState.threshold,
            onValueChange = { value ->
                onEvent(LookupEvent.PoissonThresholdChanged(value))
            },
            label = {
                Text(text = "Success threshold (r)")
            },
            placeholder = {
                Text(text = "0 or greater")
            },
            supportingText = {
                inputState.thresholdError?.let { error ->
                    Text(text = error)
                } ?: Text(
                    text = "The application supports values of at least 30.",
                )
            },
            isError = inputState.thresholdError != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Button(
            onClick = {
                onEvent(LookupEvent.Calculate)
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Calculate probability")
        }
    }
}
