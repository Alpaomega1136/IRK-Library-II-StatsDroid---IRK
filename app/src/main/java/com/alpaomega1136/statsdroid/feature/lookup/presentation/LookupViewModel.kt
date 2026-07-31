package com.alpaomega1136.statsdroid.feature.lookup.presentation

import androidx.lifecycle.ViewModel
import com.alpaomega1136.statsdroid.feature.lookup.domain.BinomialCalculator
import com.alpaomega1136.statsdroid.feature.lookup.domain.NormalDistributionCalculator
import com.alpaomega1136.statsdroid.feature.lookup.domain.PoissonCalculator
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LookupViewModel : ViewModel() {

    private val binomialCalculator = BinomialCalculator()
    private val poissonCalculator = PoissonCalculator()
    private val normalDistributionCalculator = NormalDistributionCalculator()

    private val _uiState = MutableStateFlow(LookupUiState())
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
                        parsedValue in MIN_Z_SCORE..MAX_Z_SCORE
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
        val boundedValue = value.coerceIn(MIN_Z_SCORE, MAX_Z_SCORE)
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
        val numberOfTrials = currentInput.numberOfTrials.toIntOrNull()
        val threshold = currentInput.threshold.toIntOrNull()

        val trialsError = when {
            numberOfTrials == null ->
                "Number of trials is required."

            numberOfTrials !in MIN_BINOMIAL_TRIALS..MAX_BINOMIAL_TRIALS ->
                "Number of trials must be between 1 and 20."

            else -> null
        }

        val thresholdError = when {
            threshold == null ->
                "Success threshold is required."

            threshold < 0 ->
                "Success threshold cannot be negative."

            numberOfTrials != null && threshold > numberOfTrials ->
                "Success threshold cannot exceed number of trials."

            else -> null
        }

        if (trialsError != null || thresholdError != null) {
            _uiState.update { currentState ->
                currentState.copy(
                    binomialInput = currentInput.copy(
                        numberOfTrialsError = trialsError,
                        thresholdError = thresholdError,
                    ),
                    calculationResult = null,
                )
            }
            return
        }

        val result = binomialCalculator.cumulativeProbability(
            numberOfTrials = checkNotNull(numberOfTrials),
            threshold = checkNotNull(threshold),
            successProbability = currentInput.successProbability,
        )

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

    private fun calculatePoisson() {
        val currentInput = _uiState.value.poissonInput
        val averageRate = currentInput.averageRate.toDoubleOrNull()
        val threshold = currentInput.threshold.toIntOrNull()

        val averageRateError = when {
            averageRate == null ->
                "Average rate is required."

            !averageRate.isFinite() ->
                "Average rate must be a finite number."

            averageRate <= 0.0 ->
                "Average rate must be greater than 0."

            averageRate > PoissonCalculator.MAX_AVERAGE_RATE ->
                "Average rate must not exceed 100."

            else -> null
        }

        val thresholdError = when {
            threshold == null ->
                "Success threshold is required."

            threshold < 0 ->
                "Success threshold cannot be negative."

            else -> null
        }

        if (averageRateError != null || thresholdError != null) {
            _uiState.update { currentState ->
                currentState.copy(
                    poissonInput = currentInput.copy(
                        averageRateError = averageRateError,
                        thresholdError = thresholdError,
                    ),
                    calculationResult = null,
                )
            }
            return
        }

        val result = poissonCalculator.cumulativeProbability(
            averageRate = checkNotNull(averageRate),
            threshold = checkNotNull(threshold),
        )

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

    private fun calculateStandardNormal() {
        val currentInput = _uiState.value.normalInput
        val zScore = currentInput.zScoreText.toDoubleOrNull()

        val zScoreError = when {
            zScore == null ->
                "Z-score is required."

            !zScore.isFinite() ->
                "Z-score must be a finite number."

            zScore !in MIN_Z_SCORE..MAX_Z_SCORE ->
                "Z-score must be between -5.0 and 5.0."

            else -> null
        }

        if (zScoreError != null) {
            _uiState.update { currentState ->
                currentState.copy(
                    normalInput = currentInput.copy(
                        zScoreError = zScoreError,
                    ),
                    calculationResult = null,
                )
            }
            return
        }

        val result = normalDistributionCalculator
            .cumulativeProbability(checkNotNull(zScore))

        _uiState.update { currentState ->
            currentState.copy(
                normalInput = currentInput.copy(
                    zScoreValue = zScore,
                    zScoreError = null,
                ),
                calculationResult = result,
            )
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
        val BINOMIAL_PROBABILITIES =
            (1..9).map { value -> value / 10.0 }

        private const val MIN_BINOMIAL_TRIALS = 1
        private const val MAX_BINOMIAL_TRIALS = 20

        const val MIN_Z_SCORE = -5.0
        const val MAX_Z_SCORE = 5.0

        private val DECIMAL_INPUT_REGEX =
            Regex("""\d*\.?\d*""")

        private val SIGNED_DECIMAL_INPUT_REGEX =
            Regex("""-?\d*\.?\d*""")
    }
}
