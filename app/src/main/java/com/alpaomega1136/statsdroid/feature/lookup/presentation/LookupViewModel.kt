package com.alpaomega1136.statsdroid.feature.lookup.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.alpaomega1136.statsdroid.feature.lookup.domain.repository.LookupRepository
import com.alpaomega1136.statsdroid.feature.lookup.domain.validation.BinomialInputValidator
import com.alpaomega1136.statsdroid.feature.lookup.domain.validation.LookupInputField
import com.alpaomega1136.statsdroid.feature.lookup.domain.validation.LookupValidationResult
import com.alpaomega1136.statsdroid.feature.lookup.domain.validation.PoissonInputValidator
import com.alpaomega1136.statsdroid.feature.lookup.domain.validation.StandardNormalInputValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class LookupViewModel @Inject constructor(
    private val repository: LookupRepository,
    private val binomialInputValidator: BinomialInputValidator,
    private val poissonInputValidator: PoissonInputValidator,
    private val standardNormalInputValidator: StandardNormalInputValidator,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<LookupUiState> = _uiState.asStateFlow()

    fun onEvent(event: LookupEvent) {
        when (event) {
            is LookupEvent.DistributionChanged ->
                changeDistribution(event.distribution)

            is LookupEvent.BinomialTrialsChanged ->
                changeBinomialTrials(event.value)

            is LookupEvent.BinomialThresholdChanged ->
                changeBinomialThreshold(event.value)

            is LookupEvent.BinomialProbabilityChanged ->
                changeBinomialProbability(event.value)

            is LookupEvent.PoissonAverageRateChanged ->
                changePoissonAverageRate(event.value)

            is LookupEvent.PoissonThresholdChanged ->
                changePoissonThreshold(event.value)

            is LookupEvent.NormalZTextChanged ->
                changeNormalZText(event.value)

            is LookupEvent.NormalZSliderChanged ->
                changeNormalZSlider(event.value)

            LookupEvent.Calculate ->
                calculate()
        }

        saveRestorableState(_uiState.value)
    }

    private fun createInitialState(): LookupUiState {
        val selectedDistribution = savedStateHandle
            .get<String>(KEY_DISTRIBUTION)
            ?.let { runCatching { DistributionType.valueOf(it) }.getOrNull() }
            ?: DistributionType.BINOMIAL

        return LookupUiState(
            selectedDistribution = selectedDistribution,
            binomialInput = BinomialInputState(
                numberOfTrials = savedStateHandle[KEY_BINOMIAL_TRIALS] ?: "",
                threshold = savedStateHandle[KEY_BINOMIAL_THRESHOLD] ?: "",
                successProbability = savedStateHandle[KEY_BINOMIAL_PROBABILITY] ?: 0.5,
            ),
            poissonInput = PoissonInputState(
                averageRate = savedStateHandle[KEY_POISSON_RATE] ?: "",
                threshold = savedStateHandle[KEY_POISSON_THRESHOLD] ?: "",
            ),
            normalInput = NormalInputState(
                zScoreText = savedStateHandle[KEY_NORMAL_Z_TEXT] ?: "0.0",
                zScoreValue = savedStateHandle[KEY_NORMAL_Z_VALUE] ?: 0.0,
            ),
            normalCurvePoints = repository.generateStandardNormalCurve(
                minZ = StandardNormalInputValidator.MIN_Z_SCORE,
                maxZ = StandardNormalInputValidator.MAX_Z_SCORE,
                step = NORMAL_CURVE_STEP,
            ),
        )
    }

    private fun saveRestorableState(state: LookupUiState) {
        savedStateHandle[KEY_DISTRIBUTION] = state.selectedDistribution.name
        savedStateHandle[KEY_BINOMIAL_TRIALS] = state.binomialInput.numberOfTrials
        savedStateHandle[KEY_BINOMIAL_THRESHOLD] = state.binomialInput.threshold
        savedStateHandle[KEY_BINOMIAL_PROBABILITY] = state.binomialInput.successProbability
        savedStateHandle[KEY_POISSON_RATE] = state.poissonInput.averageRate
        savedStateHandle[KEY_POISSON_THRESHOLD] = state.poissonInput.threshold
        savedStateHandle[KEY_NORMAL_Z_TEXT] = state.normalInput.zScoreText
        savedStateHandle[KEY_NORMAL_Z_VALUE] = state.normalInput.zScoreValue
    }

    private fun changeDistribution(distribution: DistributionType) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedDistribution = distribution,
                calculationResult = null,
            )
        }
    }

    private fun changeBinomialTrials(value: String) {
        if (!isValidIntegerInput(value)) return

        _uiState.update { currentState ->
            currentState.copy(
                binomialInput = currentState.binomialInput.copy(
                    numberOfTrials = value,
                    numberOfTrialsError = null,
                ),
                calculationResult = null,
            )
        }
    }

    private fun changeBinomialThreshold(value: String) {
        if (!isValidIntegerInput(value)) return

        _uiState.update { currentState ->
            currentState.copy(
                binomialInput = currentState.binomialInput.copy(
                    threshold = value,
                    thresholdError = null,
                ),
                calculationResult = null,
            )
        }
    }

    private fun changeBinomialProbability(value: Double) {
        if (value !in BINOMIAL_PROBABILITIES) return

        _uiState.update { currentState ->
            currentState.copy(
                binomialInput = currentState.binomialInput.copy(
                    successProbability = value,
                ),
                calculationResult = null,
            )
        }
    }

    private fun changePoissonAverageRate(value: String) {
        if (!isValidDecimalInput(value)) return

        _uiState.update { currentState ->
            currentState.copy(
                poissonInput = currentState.poissonInput.copy(
                    averageRate = value,
                    averageRateError = null,
                ),
                calculationResult = null,
            )
        }
    }

    private fun changePoissonThreshold(value: String) {
        if (!isValidIntegerInput(value)) return

        _uiState.update { currentState ->
            currentState.copy(
                poissonInput = currentState.poissonInput.copy(
                    threshold = value,
                    thresholdError = null,
                ),
                calculationResult = null,
            )
        }
    }

    private fun changeNormalZText(value: String) {
        if (!isValidSignedDecimalInput(value)) return

        val parsedValue = value.toDoubleOrNull()

        _uiState.update { currentState ->
            currentState.copy(
                normalInput = currentState.normalInput.copy(
                    zScoreText = value,
                    zScoreValue = if (
                        parsedValue != null &&
                        parsedValue in StandardNormalInputValidator.MIN_Z_SCORE..
                        StandardNormalInputValidator.MAX_Z_SCORE
                    ) {
                        parsedValue
                    } else {
                        currentState.normalInput.zScoreValue
                    },
                    zScoreError = null,
                ),
                calculationResult = null,
            )
        }
    }

    private fun changeNormalZSlider(value: Double) {
        val boundedValue = value.coerceIn(
            StandardNormalInputValidator.MIN_Z_SCORE,
            StandardNormalInputValidator.MAX_Z_SCORE,
        )
        val formattedValue = String.format(Locale.US, "%.2f", boundedValue)

        _uiState.update { currentState ->
            currentState.copy(
                normalInput = currentState.normalInput.copy(
                    zScoreText = formattedValue,
                    zScoreValue = boundedValue,
                    zScoreError = null,
                ),
                calculationResult = null,
            )
        }
    }

    private fun calculate() {
        when (_uiState.value.selectedDistribution) {
            DistributionType.BINOMIAL -> calculateBinomial()
            DistributionType.POISSON -> calculatePoisson()
            DistributionType.STANDARD_NORMAL -> calculateStandardNormal()
        }
    }

    private fun calculateBinomial() {
        val currentInput = _uiState.value.binomialInput
        when (
            val validationResult = binomialInputValidator.validate(
                numberOfTrialsText = currentInput.numberOfTrials,
                thresholdText = currentInput.threshold,
                successProbability = currentInput.successProbability,
            )
        ) {
            is LookupValidationResult.Valid -> {
                val result = repository.calculateBinomialCumulative(validationResult.value)
                _uiState.update { currentState ->
                    currentState.copy(
                        binomialInput = currentInput.copy(
                            numberOfTrialsError = null,
                            thresholdError = null,
                        ),
                        calculationResult = result,
                    )
                }
            }

            is LookupValidationResult.Invalid -> {
                val errors = validationResult.errors
                _uiState.update { currentState ->
                    currentState.copy(
                        binomialInput = currentInput.copy(
                            numberOfTrialsError = errors[LookupInputField.BINOMIAL_TRIALS],
                            thresholdError = errors[LookupInputField.BINOMIAL_THRESHOLD],
                        ),
                        calculationResult = null,
                    )
                }
            }
        }
    }

    private fun calculatePoisson() {
        val currentInput = _uiState.value.poissonInput
        when (
            val validationResult = poissonInputValidator.validate(
                averageRateText = currentInput.averageRate,
                thresholdText = currentInput.threshold,
            )
        ) {
            is LookupValidationResult.Valid -> {
                val result = repository.calculatePoissonCumulative(validationResult.value)
                _uiState.update { currentState ->
                    currentState.copy(
                        poissonInput = currentInput.copy(
                            averageRateError = null,
                            thresholdError = null,
                        ),
                        calculationResult = result,
                    )
                }
            }

            is LookupValidationResult.Invalid -> {
                val errors = validationResult.errors
                _uiState.update { currentState ->
                    currentState.copy(
                        poissonInput = currentInput.copy(
                            averageRateError = errors[LookupInputField.POISSON_AVERAGE_RATE],
                            thresholdError = errors[LookupInputField.POISSON_THRESHOLD],
                        ),
                        calculationResult = null,
                    )
                }
            }
        }
    }

    private fun calculateStandardNormal() {
        val currentInput = _uiState.value.normalInput
        when (val validationResult = standardNormalInputValidator.validate(currentInput.zScoreText)) {
            is LookupValidationResult.Valid -> {
                val request = validationResult.value
                val result = repository.calculateStandardNormalCumulative(request)
                _uiState.update { currentState ->
                    currentState.copy(
                        normalInput = currentInput.copy(
                            zScoreValue = request.zScore,
                            zScoreError = null,
                        ),
                        calculationResult = result,
                    )
                }
            }

            is LookupValidationResult.Invalid -> {
                val errors = validationResult.errors
                _uiState.update { currentState ->
                    currentState.copy(
                        normalInput = currentInput.copy(
                            zScoreError = errors[LookupInputField.NORMAL_Z_SCORE],
                        ),
                        calculationResult = null,
                    )
                }
            }
        }
    }

    private fun isValidIntegerInput(value: String): Boolean {
        return value.isEmpty() || value.all(Char::isDigit)
    }

    private fun isValidDecimalInput(value: String): Boolean {
        return value.isEmpty() || DECIMAL_INPUT_REGEX.matches(value)
    }

    private fun isValidSignedDecimalInput(value: String): Boolean {
        return value.isEmpty() ||
            value == "-" ||
            SIGNED_DECIMAL_INPUT_REGEX.matches(value)
    }

    companion object {
        private const val NORMAL_CURVE_STEP = 0.05
        private const val KEY_DISTRIBUTION = "lookup_distribution"
        private const val KEY_BINOMIAL_TRIALS = "lookup_binomial_trials"
        private const val KEY_BINOMIAL_THRESHOLD = "lookup_binomial_threshold"
        private const val KEY_BINOMIAL_PROBABILITY = "lookup_binomial_probability"
        private const val KEY_POISSON_RATE = "lookup_poisson_rate"
        private const val KEY_POISSON_THRESHOLD = "lookup_poisson_threshold"
        private const val KEY_NORMAL_Z_TEXT = "lookup_normal_z_text"
        private const val KEY_NORMAL_Z_VALUE = "lookup_normal_z_value"

        val BINOMIAL_PROBABILITIES =
            (1..9).map { value -> value / 10.0 }

        private val DECIMAL_INPUT_REGEX =
            Regex("""\d*\.?\d*""")

        private val SIGNED_DECIMAL_INPUT_REGEX =
            Regex("""-?\d*\.?\d*""")
    }
}
