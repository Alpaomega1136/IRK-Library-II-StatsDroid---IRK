package com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisConstraints

@Composable
fun MeanSliderField(
    label: String,
    valueText: String,
    sliderValue: Double,
    errorMessage: String?,
    onTextChanged: (String) -> Unit,
    onSliderChanged: (Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        OutlinedTextField(
            value = valueText,
            onValueChange = onTextChanged,
            label = { Text(text = label) },
            placeholder = { Text(text = "-100.0 to 100.0") },
            supportingText = errorMessage?.let { error ->
                { Text(text = error, color = MaterialTheme.colorScheme.error) }
            },
            isError = errorMessage != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Slider(
            value = sliderValue.toFloat(),
            onValueChange = { onSliderChanged(it.toDouble()) },
            valueRange = HypothesisConstraints.MIN_MEAN.toFloat()..HypothesisConstraints.MAX_MEAN.toFloat(),
            steps = 199,
            modifier = Modifier.fillMaxWidth(),
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "-100", style = MaterialTheme.typography.labelSmall)
            Text(text = "0", style = MaterialTheme.typography.labelSmall)
            Text(text = "100", style = MaterialTheme.typography.labelSmall)
        }
    }
}
