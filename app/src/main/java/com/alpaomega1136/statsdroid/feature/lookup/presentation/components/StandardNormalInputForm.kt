package com.alpaomega1136.statsdroid.feature.lookup.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.core.statistics.model.NormalCurvePoint
import com.alpaomega1136.statsdroid.feature.lookup.domain.validation.StandardNormalInputValidator
import com.alpaomega1136.statsdroid.feature.lookup.presentation.LookupEvent
import com.alpaomega1136.statsdroid.feature.lookup.presentation.NormalInputState
import java.util.Locale

@Composable
fun StandardNormalInputForm(
    inputState: NormalInputState,
    curvePoints: List<NormalCurvePoint>,
    onEvent: (LookupEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Standard Normal Distribution",
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = "Calculate the cumulative area P(Z <= z).",
            style = MaterialTheme.typography.bodyMedium,
        )

        StandardNormalInformationCard()

        OutlinedTextField(
            value = inputState.zScoreText,
            onValueChange = { value -> onEvent(LookupEvent.NormalZTextChanged(value)) },
            label = { Text(text = "Z-score") },
            placeholder = { Text(text = "-5.0 to 5.0") },
            supportingText = {
                inputState.zScoreError?.let { error ->
                    Text(text = error)
                } ?: Text(text = "The slider and manual input are synchronized.")
            },
            isError = inputState.zScoreError != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done,
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Text(
            text = String.format(Locale.US, "Selected z: %.2f", inputState.zScoreValue),
            style = MaterialTheme.typography.labelLarge,
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

        StandardNormalCurve(
            points = curvePoints,
            selectedZScore = inputState.zScoreValue,
        )

        Text(
            text = "The shaded region represents the cumulative area to the left of the selected z-score.",
            style = MaterialTheme.typography.bodySmall,
        )

        Button(
            onClick = { onEvent(LookupEvent.Calculate) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Calculate area")
        }
    }
}

@Composable
private fun StandardNormalInformationCard(
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "Standard Z Distribution",
                style = MaterialTheme.typography.titleSmall,
            )
            Text(text = "Mean (mu): 0.0", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Standard deviation (sigma): 1.0", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
