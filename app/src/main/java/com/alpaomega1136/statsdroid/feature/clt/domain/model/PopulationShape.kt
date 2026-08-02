package com.alpaomega1136.statsdroid.feature.clt.domain.model

enum class PopulationShape(
    val displayName: String,
    val description: String,
) {
    UNIFORM(
        displayName = "Uniform",
        description = "All values within the range have equal probability.",
    ),
    EXPONENTIAL(
        displayName = "Exponential / Skewed",
        description = "A strongly right-skewed population distribution.",
    ),
    BIMODAL(
        displayName = "Bimodal",
        description = "A population distribution with two distinct peaks.",
    ),
}
