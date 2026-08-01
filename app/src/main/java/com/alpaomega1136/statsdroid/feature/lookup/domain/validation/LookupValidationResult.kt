package com.alpaomega1136.statsdroid.feature.lookup.domain.validation

sealed interface LookupValidationResult<out T> {
    data class Valid<T>(val value: T) : LookupValidationResult<T>
    data class Invalid(val errors: Map<LookupInputField, String>) : LookupValidationResult<Nothing>
}

enum class LookupInputField {
    BINOMIAL_TRIALS,
    BINOMIAL_THRESHOLD,
    BINOMIAL_PROBABILITY,
    POISSON_AVERAGE_RATE,
    POISSON_THRESHOLD,
    NORMAL_Z_SCORE,
}
