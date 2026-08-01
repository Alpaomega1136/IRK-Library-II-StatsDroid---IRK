package com.alpaomega1136.statsdroid.feature.lookup.domain.validation

import com.alpaomega1136.statsdroid.feature.lookup.domain.model.BinomialRequest
import javax.inject.Inject

class BinomialInputValidator @Inject constructor() {

    fun validate(
        numberOfTrialsText: String,
        thresholdText: String,
        successProbability: Double,
    ): LookupValidationResult<BinomialRequest> {
        val errors = mutableMapOf<LookupInputField, String>()
        val numberOfTrials = numberOfTrialsText.toIntOrNull()
        val threshold = thresholdText.toIntOrNull()

        when {
            numberOfTrialsText.isBlank() -> errors[LookupInputField.BINOMIAL_TRIALS] =
                "Number of trials is required."
            numberOfTrials == null -> errors[LookupInputField.BINOMIAL_TRIALS] =
                "Number of trials must be an integer."
            numberOfTrials !in MIN_TRIALS..MAX_TRIALS -> errors[LookupInputField.BINOMIAL_TRIALS] =
                "Number of trials must be between 1 and 20."
        }

        when {
            thresholdText.isBlank() -> errors[LookupInputField.BINOMIAL_THRESHOLD] =
                "Success threshold is required."
            threshold == null -> errors[LookupInputField.BINOMIAL_THRESHOLD] =
                "Success threshold must be an integer."
            threshold < 0 -> errors[LookupInputField.BINOMIAL_THRESHOLD] =
                "Success threshold cannot be negative."
            numberOfTrials != null &&
                numberOfTrials in MIN_TRIALS..MAX_TRIALS &&
                threshold > numberOfTrials -> errors[LookupInputField.BINOMIAL_THRESHOLD] =
                    "Success threshold cannot exceed number of trials."
        }

        if (successProbability !in ALLOWED_PROBABILITIES) {
            errors[LookupInputField.BINOMIAL_PROBABILITY] =
                "Success probability must be between 0.1 and 0.9."
        }

        if (errors.isNotEmpty()) return LookupValidationResult.Invalid(errors)

        return LookupValidationResult.Valid(
            BinomialRequest(
                numberOfTrials = requireNotNull(numberOfTrials),
                threshold = requireNotNull(threshold),
                successProbability = successProbability,
            ),
        )
    }

    companion object {
        private const val MIN_TRIALS = 1
        private const val MAX_TRIALS = 20
        private val ALLOWED_PROBABILITIES = (1..9).map { value -> value / 10.0 }
    }
}
