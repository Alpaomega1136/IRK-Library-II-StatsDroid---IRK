package com.alpaomega1136.statsdroid.feature.hypothesis.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisConstraints
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestResult
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestType
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.SignificanceLevel
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.TailType
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.repository.HypothesisRepository
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.validation.HypothesisInputField
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.validation.HypothesisValidationResult
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.validation.TTestInputValidator
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.validation.ZTestInputValidator
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.visualization.HypothesisVisualizationGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import kotlin.math.sqrt
import kotlin.random.Random
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class HypothesisViewModel @Inject constructor(
    private val repository: HypothesisRepository,
    private val visualizationGenerator: HypothesisVisualizationGenerator,
    private val zTestInputValidator: ZTestInputValidator,
    private val tTestInputValidator: TTestInputValidator,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(createInitialState())
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
            is HypothesisEvent.SignificanceLevelChanged -> _uiState.update {
                it.copy(significanceLevel = event.significanceLevel, result = null, visualization = null)
            }
            is HypothesisEvent.TailTypeChanged -> _uiState.update {
                it.copy(tailType = event.tailType, result = null, visualization = null)
            }
            HypothesisEvent.LoadWorkedExample -> loadWorkedExample()
            HypothesisEvent.Calculate -> calculate()
        }

        saveRestorableState(_uiState.value)
    }

    private fun createInitialState(): HypothesisUiState {
        val testType = restoreEnum(KEY_TEST_TYPE, HypothesisTestType.Z_TEST, HypothesisTestType::valueOf)
        val significanceLevel = restoreEnum(KEY_SIGNIFICANCE_LEVEL, SignificanceLevel.FIVE_PERCENT, SignificanceLevel::valueOf)
        val tailType = restoreEnum(KEY_TAIL_TYPE, TailType.TWO_TAILED, TailType::valueOf)

        return HypothesisUiState(
            selectedTestType = testType,
            input = HypothesisInputState(
                hypothesizedMean = savedStateHandle[KEY_HYPOTHESIZED_MEAN_TEXT] ?: "0.0",
                hypothesizedMeanValue = savedStateHandle[KEY_HYPOTHESIZED_MEAN_VALUE] ?: 0.0,
                sampleMean = savedStateHandle[KEY_SAMPLE_MEAN_TEXT] ?: "0.0",
                sampleMeanValue = savedStateHandle[KEY_SAMPLE_MEAN_VALUE] ?: 0.0,
                standardDeviation = savedStateHandle[KEY_STANDARD_DEVIATION] ?: "1.0",
                sampleSize = savedStateHandle[KEY_SAMPLE_SIZE] ?: "30",
            ),
            significanceLevel = significanceLevel,
            tailType = tailType,
        )
    }

    private fun <T> restoreEnum(key: String, defaultValue: T, parser: (String) -> T): T {
        return savedStateHandle.get<String>(key)?.let { runCatching { parser(it) }.getOrNull() } ?: defaultValue
    }

    private fun saveRestorableState(state: HypothesisUiState) {
        savedStateHandle[KEY_TEST_TYPE] = state.selectedTestType.name
        savedStateHandle[KEY_HYPOTHESIZED_MEAN_TEXT] = state.input.hypothesizedMean
        savedStateHandle[KEY_HYPOTHESIZED_MEAN_VALUE] = state.input.hypothesizedMeanValue
        savedStateHandle[KEY_SAMPLE_MEAN_TEXT] = state.input.sampleMean
        savedStateHandle[KEY_SAMPLE_MEAN_VALUE] = state.input.sampleMeanValue
        savedStateHandle[KEY_STANDARD_DEVIATION] = state.input.standardDeviation
        savedStateHandle[KEY_SAMPLE_SIZE] = state.input.sampleSize
        savedStateHandle[KEY_SIGNIFICANCE_LEVEL] = state.significanceLevel.name
        savedStateHandle[KEY_TAIL_TYPE] = state.tailType.name
    }

    private fun changeTestType(testType: HypothesisTestType) {
        _uiState.update { it.copy(selectedTestType = testType, input = it.input.clearErrors(), result = null, visualization = null) }
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
                visualization = null,
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
                visualization = null,
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
                visualization = null,
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
                visualization = null,
            )
        }
    }

    private fun changeStandardDeviation(value: String) {
        if (!isValidUnsignedDecimalInput(value)) return
        _uiState.update {
            it.copy(input = it.input.copy(standardDeviation = value, standardDeviationError = null), result = null, visualization = null)
        }
    }

    private fun changeSampleSize(value: String) {
        if (!isValidIntegerInput(value)) return
        _uiState.update { it.copy(input = it.input.copy(sampleSize = value, sampleSizeError = null), result = null, visualization = null) }
    }

    private fun loadWorkedExample() {
        val random = Random.Default
        val hypothesizedMean = random.nextInt(-60, 61).toDouble()
        val standardDeviation = random.nextInt(20, 121) / 10.0
        val sampleSize = random.nextInt(12, 101)
        val statisticTarget = RANDOM_STATISTIC_TARGETS.random(random)
        val sampleMean = (
            hypothesizedMean +
                statisticTarget * standardDeviation / sqrt(sampleSize.toDouble())
            ).coerceIn(
                HypothesisConstraints.MIN_MEAN,
                HypothesisConstraints.MAX_MEAN,
            )
        val significanceLevel = SignificanceLevel.entries.random(random)
        val tailType = TailType.entries.random(random)

        _uiState.update { currentState ->
            currentState.copy(
                input = HypothesisInputState(
                    hypothesizedMean = formatMean(hypothesizedMean),
                    hypothesizedMeanValue = hypothesizedMean,
                    sampleMean = formatMean(sampleMean),
                    sampleMeanValue = sampleMean,
                    standardDeviation = String.format(
                        Locale.US,
                        "%.1f",
                        standardDeviation,
                    ),
                    sampleSize = sampleSize.toString(),
                ),
                significanceLevel = significanceLevel,
                tailType = tailType,
                result = null,
                visualization = null,
            )
        }

        calculate()
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
            is HypothesisValidationResult.Valid -> applySuccessfulResult(repository.calculateZTest(validationResult.value), input)
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
            is HypothesisValidationResult.Valid -> applySuccessfulResult(repository.calculateTTest(validationResult.value), input)
            is HypothesisValidationResult.Invalid -> applyValidationErrors(validationResult.errors)
        }
    }

    private fun applySuccessfulResult(result: HypothesisTestResult, currentInput: HypothesisInputState) {
        _uiState.update {
            it.copy(
                input = currentInput.clearErrors(),
                result = result,
                visualization = visualizationGenerator.generate(result),
            )
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
                visualization = null,
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
        private const val KEY_TEST_TYPE = "hypothesis_test_type"
        private const val KEY_HYPOTHESIZED_MEAN_TEXT = "hypothesis_hypothesized_mean_text"
        private const val KEY_HYPOTHESIZED_MEAN_VALUE = "hypothesis_hypothesized_mean_value"
        private const val KEY_SAMPLE_MEAN_TEXT = "hypothesis_sample_mean_text"
        private const val KEY_SAMPLE_MEAN_VALUE = "hypothesis_sample_mean_value"
        private const val KEY_STANDARD_DEVIATION = "hypothesis_standard_deviation"
        private const val KEY_SAMPLE_SIZE = "hypothesis_sample_size"
        private const val KEY_SIGNIFICANCE_LEVEL = "hypothesis_significance_level"
        private const val KEY_TAIL_TYPE = "hypothesis_tail_type"

        private val SIGNED_DECIMAL_REGEX = Regex("""-?\d*\.?\d*""")
        private val UNSIGNED_DECIMAL_REGEX = Regex("""\d*\.?\d*""")
        private val RANDOM_STATISTIC_TARGETS = listOf(
            -2.6,
            -2.1,
            -1.5,
            -0.8,
            0.7,
            1.3,
            1.9,
            2.4,
        )
    }
}
