package com.alpaomega1136.statsdroid.feature.lookup.presentation

enum class DistributionType(
    val displayName: String,
) {
    BINOMIAL(
        displayName = "Binomial Probability Sums",
    ),
    POISSON(
        displayName = "Poisson Probability Sums",
    ),
    STANDARD_NORMAL(
        displayName = "Area Under Normal Curve",
    ),
}
