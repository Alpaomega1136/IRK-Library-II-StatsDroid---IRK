package com.alpaomega1136.statsdroid.feature.hypothesis.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisConstraints
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestType
import com.alpaomega1136.statsdroid.feature.hypothesis.presentation.HypothesisEvent
import com.alpaomega1136.statsdroid.feature.hypothesis.presentation.HypothesisInputState
import com.alpaomega1136.statsdroid.ui.components.StatsSectionCard
import com.alpaomega1136.statsdroid.ui.theme.SmallControlShape
import com.alpaomega1136.statsdroid.ui.theme.StatsSpacing

@Composable
fun HypothesisInputForm(
    testType: HypothesisTestType,
    inputState: HypothesisInputState,
    onEvent: (HypothesisEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val standardDeviationLabel = when (testType) {
        HypothesisTestType.Z_TEST -> "Population Std Dev (\u03C3)"
        HypothesisTestType.T_TEST -> "Sample Std Dev (s)"
    }
    val minimumSampleSize = when (testType) {
        HypothesisTestType.Z_TEST -> HypothesisConstraints.MIN_Z_SAMPLE_SIZE
        HypothesisTestType.T_TEST -> HypothesisConstraints.MIN_T_SAMPLE_SIZE
    }

    StatsSectionCard(
        title = "Sample & Population Parameters",
        subtitle = "Input observed data and population baseline",
        modifier = modifier,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(StatsSpacing.Medium)) {
            MeanSliderField(
                label = "Hypothesized Mean (\u03BC\u2080)",
                valueText = inputState.hypothesizedMean,
                sliderValue = inputState.hypothesizedMeanValue,
                errorMessage = inputState.hypothesizedMeanError,
                onTextChanged = { onEvent(HypothesisEvent.HypothesizedMeanChanged(it)) },
                onSliderChanged = { onEvent(HypothesisEvent.HypothesizedMeanSliderChanged(it)) },
            )

            MeanSliderField(
                label = "Sample Mean (x\u0304)",
                valueText = inputState.sampleMean,
                sliderValue = inputState.sampleMeanValue,
                errorMessage = inputState.sampleMeanError,
                onTextChanged = { onEvent(HypothesisEvent.SampleMeanChanged(it)) },
                onSliderChanged = { onEvent(HypothesisEvent.SampleMeanSliderChanged(it)) },
            )

            OutlinedTextField(
                value = inputState.standardDeviation,
                onValueChange = { onEvent(HypothesisEvent.StandardDeviationChanged(it)) },
                label = { Text(text = standardDeviationLabel) },
                placeholder = { Text(text = "0 < s \u2264 50") },
                supportingText = {
                    Text(
                        text = inputState.standardDeviationError ?: "Must be greater than 0 and at most 50.",
                        color = if (inputState.standardDeviationError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                isError = inputState.standardDeviationError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = SmallControlShape,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = inputState.sampleSize,
                onValueChange = { onEvent(HypothesisEvent.SampleSizeChanged(it)) },
                label = { Text(text = "Sample Size (n)") },
                placeholder = { Text(text = "$minimumSampleSize-500") },
                supportingText = {
                    val sampleSize = inputState.sampleSize.toIntOrNull()
                    val degreesOfFreedom = sampleSize?.takeIf { testType == HypothesisTestType.T_TEST && it >= 2 }?.minus(1)
                    Text(
                        text = inputState.sampleSizeError
                            ?: degreesOfFreedom?.let { "Degrees of freedom (df): $it" }
                            ?: "Sample size must be between $minimumSampleSize and 500.",
                        color = if (inputState.sampleSizeError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                isError = inputState.sampleSizeError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = SmallControlShape,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
