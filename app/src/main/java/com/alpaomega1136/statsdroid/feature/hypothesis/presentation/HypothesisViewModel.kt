package com.alpaomega1136.statsdroid.feature.hypothesis.presentation

import androidx.lifecycle.ViewModel
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisConstraints
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestType
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.repository.HypothesisRepository
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.validation.HypothesisInputField
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.validation.HypothesisValidationResult
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.validation.TTestInputValidator
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.validation.ZTestInputValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class HypothesisViewModel @Inject constructor(
    private val repository: HypothesisRepository,
    private val zTestInputValidator: ZTestInputValidator,
    private val tTestInputValidator: TTestInputValidator,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HypothesisUiState())
    val uiState: StateFlow<HypothesisUiState> = _uiState.asStateFlow()

    fun onEvent(event: HypothesisEvent) {
        when (event) {
            is HypothesisEvent.TestTypeChanged -> changeTestType(event.testType)
            is HypothesisEvent.HypothesizedMeanChanged -> changeHypothesizedMean(event.value)
            is HypothesisEvent.HypothesizedMeanSliderChanged -> changeHypothesizedMeanSlider(event.value)
            is HypothesisEvent.SampleMeanChanged -> changeSampleMean(event.value)
            is HypothesisEvent.SampleMeanSliderChanged -> changeSampleMeanSlider(event.value)
            is HypothesisEvent.StandardDeviationChanged -> changeStandardDeviation(event.value)
            is HypothesisEvent.SampleSizeChanged -> changeSampleSize(event.value)
            is HypothesisEvent.SignificanceLevelChanged -> _uiState.update { it.copy(significanceLevel = event.significanceLevel, result = null) }
            is HypothesisEvent.TailTypeChanged -> _uiState.update { it.copy(tailType = event.tailType, result = null) }
            HypothesisEvent.Calculate -> calculate()
        }
    }

    private fun changeTestType(testType: HypothesisTestType) {
        _uiState.update { it.copy(selectedTestType = testType, input = it.input.clearErrors(), result = null) }
    }

    private fun changeHypothesizedMean(value: String) {
        if (!isValidSignedDecimalInput(value)) return
        val parsedValue = value.toDoubleOrNull()
        _uiState.update {
            it.copy(
                input = it.input.copy(
                    hypothesizedMean = value,
                    hypothesizedMeanValue = if (parsedValue != null && parsedValue in HypothesisConstraints.MIN_MEAN..HypothesisConstraints.MAX_MEAN) {
                        parsedValue
                    } else {
                        it.input.hypothesizedMeanValue
                    },
                    hypothesizedMeanError = null,
                ),
                result = null,
            )
        }
    }

    private fun changeSampleMean(value: String) {
        if (!isValidSignedDecimalInput(value)) return
        val parsedValue = value.toDoubleOrNull()
        _uiState.update {
            it.copy(
                input = it.input.copy(
                    sampleMean = value,
                    sampleMeanValue = if (parsedValue != null && parsedValue in HypothesisConstraints.MIN_MEAN..HypothesisConstraints.MAX_MEAN) {
                        parsedValue
                    } else {
                        it.input.sampleMeanValue
                    },
                    sampleMeanError = null,
                ),
                result = null,
            )
        }
    }

    private fun changeHypothesizedMeanSlider(value: Double) {
        val boundedValue = value.coerceIn(HypothesisConstraints.MIN_MEAN, HypothesisConstraints.MAX_MEAN)
        _uiState.update {
            it.copy(
                input = it.input.copy(
                    hypothesizedMean = formatMean(boundedValue),
                    hypothesizedMeanValue = boundedValue,
                    hypothesizedMeanError = null,
                ),
                result = null,
            )
        }
    }

    private fun changeSampleMeanSlider(value: Double) {
        val boundedValue = value.coerceIn(HypothesisConstraints.MIN_MEAN, HypothesisConstraints.MAX_MEAN)
        _uiState.update {
            it.copy(
                input = it.input.copy(
                    sampleMean = formatMean(boundedValue),
                    sampleMeanValue = boundedValue,
                    sampleMeanError = null,
                ),
                result = null,
            )
        }
    }

    private fun changeStandardDeviation(value: String) {
        if (!isValidUnsignedDecimalInput(value)) return
        _uiState.update { it.copy(input = it.input.copy(standardDeviation = value, standardDeviationError = null), result = null) }
    }

    private fun changeSampleSize(value: String) {
        if (!isValidIntegerInput(value)) return
        _uiState.update { it.copy(input = it.input.copy(sampleSize = value, sampleSizeError = null), result = null) }
    }

    private fun calculate() {
        when (_uiState.value.selectedTestType) {
            HypothesisTestType.Z_TEST -> calculateZTest()
            HypothesisTestType.T_TEST -> calculateTTest()
        }
    }

    private fun calculateZTest() {
        val state = _uiState.value
        val input = state.input
        when (
            val validationResult = zTestInputValidator.validate(
                hypothesizedMeanText = input.hypothesizedMean,
                sampleMeanText = input.sampleMean,
                populationStandardDeviationText = input.standardDeviation,
                sampleSizeText = input.sampleSize,
                significanceLevel = state.significanceLevel,
                tailType = state.tailType,
            )
        ) {
            is HypothesisValidationResult.Valid -> _uiState.update {
                it.copy(input = input.clearErrors(), result = repository.calculateZTest(validationResult.value))
            }
            is HypothesisValidationResult.Invalid -> applyValidationErrors(validationResult.errors)
        }
    }

    private fun calculateTTest() {
        val state = _uiState.value
        val input = state.input
        when (
            val validationResult = tTestInputValidator.validate(
                hypothesizedMeanText = input.hypothesizedMean,
                sampleMeanText = input.sampleMean,
                sampleStandardDeviationText = input.standardDeviation,
                sampleSizeText = input.sampleSize,
                significanceLevel = state.significanceLevel,
                tailType = state.tailType,
            )
        ) {
            is HypothesisValidationResult.Valid -> _uiState.update {
                it.copy(input = input.clearErrors(), result = repository.calculateTTest(validationResult.value))
            }
            is HypothesisValidationResult.Invalid -> applyValidationErrors(validationResult.errors)
        }
    }

    private fun applyValidationErrors(errors: Map<HypothesisInputField, String>) {
        _uiState.update {
            it.copy(
                input = it.input.copy(
                    hypothesizedMeanError = errors[HypothesisInputField.HYPOTHESIZED_MEAN],
                    sampleMeanError = errors[HypothesisInputField.SAMPLE_MEAN],
                    standardDeviationError = errors[HypothesisInputField.STANDARD_DEVIATION],
                    sampleSizeError = errors[HypothesisInputField.SAMPLE_SIZE],
                ),
                result = null,
            )
        }
    }

    private fun HypothesisInputState.clearErrors(): HypothesisInputState {
        return copy(
            hypothesizedMeanError = null,
            sampleMeanError = null,
            standardDeviationError = null,
            sampleSizeError = null,
        )
    }

    private fun isValidSignedDecimalInput(value: String): Boolean = value.isEmpty() || value == "-" || SIGNED_DECIMAL_REGEX.matches(value)
    private fun isValidUnsignedDecimalInput(value: String): Boolean = value.isEmpty() || UNSIGNED_DECIMAL_REGEX.matches(value)
    private fun isValidIntegerInput(value: String): Boolean = value.isEmpty() || value.all(Char::isDigit)
    private fun formatMean(value: Double): String = String.format(Locale.US, "%.1f", value)

    companion object {
        private val SIGNED_DECIMAL_REGEX = Regex("""-?\d*\.?\d*""")
        private val UNSIGNED_DECIMAL_REGEX = Regex("""\d*\.?\d*""")
    }
}
