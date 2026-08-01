package com.alpaomega1136.statsdroid.feature.lookup.domain.validation

import com.alpaomega1136.statsdroid.core.statistics.distribution.PoissonCalculator
import com.alpaomega1136.statsdroid.feature.lookup.domain.model.PoissonRequest
import javax.inject.Inject

class PoissonInputValidator @Inject constructor() {

    fun validate(
        averageRateText: String,
        thresholdText: String,
    ): LookupValidationResult<PoissonRequest> {
        val errors = mutableMapOf<LookupInputField, String>()
        val averageRate = averageRateText.toDoubleOrNull()
        val threshold = thresholdText.toIntOrNull()

        when {
            averageRateText.isBlank() -> errors[LookupInputField.POISSON_AVERAGE_RATE] =
                "Average rate is required."
            averageRate == null -> errors[LookupInputField.POISSON_AVERAGE_RATE] =
                "Average rate must be a valid number."
            !averageRate.isFinite() -> errors[LookupInputField.POISSON_AVERAGE_RATE] =
                "Average rate must be a finite number."
            averageRate <= 0.0 -> errors[LookupInputField.POISSON_AVERAGE_RATE] =
                "Average rate must be greater than 0."
            averageRate > PoissonCalculator.MAX_AVERAGE_RATE -> errors[LookupInputField.POISSON_AVERAGE_RATE] =
                "Average rate must not exceed 100."
        }

        when {
            thresholdText.isBlank() -> errors[LookupInputField.POISSON_THRESHOLD] =
                "Success threshold is required."
            threshold == null -> errors[LookupInputField.POISSON_THRESHOLD] =
                "Success threshold must be an integer."
            threshold < 0 -> errors[LookupInputField.POISSON_THRESHOLD] =
                "Success threshold cannot be negative."
        }

        if (errors.isNotEmpty()) return LookupValidationResult.Invalid(errors)

        return LookupValidationResult.Valid(
            PoissonRequest(
                averageRate = requireNotNull(averageRate),
                threshold = requireNotNull(threshold),
            ),
        )
    }
}
