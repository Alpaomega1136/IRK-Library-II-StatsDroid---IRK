package com.alpaomega1136.statsdroid.feature.hypothesis.domain.validation

sealed interface HypothesisValidationResult<out T> {
    data class Valid<T>(val value: T) : HypothesisValidationResult<T>
    data class Invalid(val errors: Map<HypothesisInputField, String>) : HypothesisValidationResult<Nothing>
}

enum class HypothesisInputField {
    HYPOTHESIZED_MEAN,
    SAMPLE_MEAN,
    STANDARD_DEVIATION,
    SAMPLE_SIZE,
}
