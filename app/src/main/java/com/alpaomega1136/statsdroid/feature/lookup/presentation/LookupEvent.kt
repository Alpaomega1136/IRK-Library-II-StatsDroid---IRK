package com.alpaomega1136.statsdroid.feature.lookup.presentation

sealed interface LookupEvent {

    data class DistributionChanged(
        val distribution: DistributionType,
    ) : LookupEvent

    data class BinomialTrialsChanged(
        val value: String,
    ) : LookupEvent

    data class BinomialThresholdChanged(
        val value: String,
    ) : LookupEvent

    data class BinomialProbabilityChanged(
        val value: Double,
    ) : LookupEvent

    data class PoissonAverageRateChanged(
        val value: String,
    ) : LookupEvent

    data class PoissonThresholdChanged(
        val value: String,
    ) : LookupEvent

    data class NormalZTextChanged(
        val value: String,
    ) : LookupEvent

    data class NormalZSliderChanged(
        val value: Double,
    ) : LookupEvent

    data object Calculate : LookupEvent
}
