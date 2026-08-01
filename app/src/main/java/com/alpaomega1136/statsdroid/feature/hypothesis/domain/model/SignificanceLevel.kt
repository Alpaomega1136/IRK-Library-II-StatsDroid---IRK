package com.alpaomega1136.statsdroid.feature.hypothesis.domain.model

enum class SignificanceLevel(
    val value: Double,
    val displayName: String,
) {
    ONE_PERCENT(value = 0.01, displayName = "0.01"),
    FIVE_PERCENT(value = 0.05, displayName = "0.05"),
    TEN_PERCENT(value = 0.10, displayName = "0.10"),
}
