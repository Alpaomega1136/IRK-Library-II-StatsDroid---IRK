package com.alpaomega1136.statsdroid.feature.lookup.presentation

data class BinomialInputState(
    val numberOfTrials: String = "",
    val threshold: String = "",
    val successProbability: Double = 0.5,
    val numberOfTrialsError: String? = null,
    val thresholdError: String? = null,
)

data class PoissonInputState(
    val averageRate: String = "",
    val threshold: String = "",
    val averageRateError: String? = null,
    val thresholdError: String? = null,
)

data class NormalInputState(
    val zScoreText: String = "0.0",
    val zScoreValue: Double = 0.0,
    val zScoreError: String? = null,
)

data class LookupUiState(
    val selectedDistribution: DistributionType =
        DistributionType.BINOMIAL,
    val binomialInput: BinomialInputState =
        BinomialInputState(),
    val poissonInput: PoissonInputState =
        PoissonInputState(),
    val normalInput: NormalInputState =
        NormalInputState(),
    val calculationResult: Double? = null,
)
