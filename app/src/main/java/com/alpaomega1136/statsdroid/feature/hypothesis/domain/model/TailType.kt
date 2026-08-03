package com.alpaomega1136.statsdroid.feature.hypothesis.domain.model

enum class TailType(
    val displayName: String,
    val alternativeSymbol: String,
) {
    TWO_TAILED(displayName = "Two-tailed", alternativeSymbol = "≠"),
    RIGHT_TAILED(displayName = "Right-tailed", alternativeSymbol = ">"),
    LEFT_TAILED(displayName = "Left-tailed", alternativeSymbol = "<"),
}
