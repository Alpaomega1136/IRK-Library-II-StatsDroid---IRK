package com.alpaomega1136.statsdroid.feature.lookup.domain.model

data class PoissonRequest(
    val averageRate: Double,
    val threshold: Int,
)
