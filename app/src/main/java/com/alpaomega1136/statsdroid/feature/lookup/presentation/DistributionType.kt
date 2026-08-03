package com.alpaomega1136.statsdroid.feature.lookup.presentation

enum class DistributionType(
    val displayName: String,
) {
    BINOMIAL(
        displayName = "Binomial",
    ),
    POISSON(
        displayName = "Poisson",
    ),
    STANDARD_NORMAL(
        displayName = "Normal",
    ),
}
