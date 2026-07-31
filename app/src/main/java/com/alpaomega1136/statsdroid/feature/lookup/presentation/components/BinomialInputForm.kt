package com.alpaomega1136.statsdroid.feature.lookup.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.feature.lookup.presentation.BinomialInputState
import com.alpaomega1136.statsdroid.feature.lookup.presentation.LookupEvent
import java.util.Locale

private val binomialProbabilityOptions =
    (1..9).map { value -> value / 10.0 }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BinomialInputForm(
    inputState: BinomialInputState,
    onEvent: (LookupEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isProbabilityMenuExpanded by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Binomial Parameters",
            style = MaterialTheme.typography.titleMedium,
        )

        Text(
            text = "Calculate the cumulative probability P(X <= r).",
            style = MaterialTheme.typography.bodyMedium,
        )

        OutlinedTextField(
            value = inputState.numberOfTrials,
            onValueChange = { value ->
                onEvent(LookupEvent.BinomialTrialsChanged(value))
            },
            label = {
                Text(text = "Number of trials (n)")
            },
            placeholder = {
                Text(text = "1-20")
            },
            supportingText = {
                inputState.numberOfTrialsError?.let { error ->
                    Text(text = error)
                }
            },
            isError = inputState.numberOfTrialsError != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = inputState.threshold,
            onValueChange = { value ->
                onEvent(LookupEvent.BinomialThresholdChanged(value))
            },
            label = {
                Text(text = "Success threshold (r)")
            },
            placeholder = {
                Text(text = "0 <= r <= n")
            },
            supportingText = {
                inputState.thresholdError?.let { error ->
                    Text(text = error)
                }
            },
            isError = inputState.thresholdError != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        ExposedDropdownMenuBox(
            expanded = isProbabilityMenuExpanded,
            onExpandedChange = {
                isProbabilityMenuExpanded = !isProbabilityMenuExpanded
            },
        ) {
            OutlinedTextField(
                value = String.format(
                    Locale.US,
                    "%.1f",
                    inputState.successProbability,
                ),
                onValueChange = {},
                readOnly = true,
                label = {
                    Text(text = "Probability of success (p)")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = isProbabilityMenuExpanded,
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = true,
                    )
                    .fillMaxWidth(),
            )

            ExposedDropdownMenu(
                expanded = isProbabilityMenuExpanded,
                onDismissRequest = {
                    isProbabilityMenuExpanded = false
                },
            ) {
                binomialProbabilityOptions.forEach { probability ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = String.format(
                                    Locale.US,
                                    "%.1f",
                                    probability,
                                ),
                            )
                        },
                        onClick = {
                            onEvent(
                                LookupEvent.BinomialProbabilityChanged(
                                    probability,
                                ),
                            )
                            isProbabilityMenuExpanded = false
                        },
                    )
                }
            }
        }

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
