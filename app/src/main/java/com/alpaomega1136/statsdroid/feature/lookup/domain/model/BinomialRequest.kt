package com.alpaomega1136.statsdroid.feature.lookup.domain.model

data class BinomialRequest(
    val numberOfTrials: Int,
    val threshold: Int,
    val successProbability: Double,
)
