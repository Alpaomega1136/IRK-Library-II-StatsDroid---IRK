package com.alpaomega1136.statsdroid.feature.clt.domain.model

enum class SimulationCount(
    val value: Int,
    val displayName: String,
) {
    ONE_HUNDRED(value = 100, displayName = "100"),
    FIVE_HUNDRED(value = 500, displayName = "500"),
    ONE_THOUSAND(value = 1_000, displayName = "1,000"),
    FIVE_THOUSAND(value = 5_000, displayName = "5,000"),
}
