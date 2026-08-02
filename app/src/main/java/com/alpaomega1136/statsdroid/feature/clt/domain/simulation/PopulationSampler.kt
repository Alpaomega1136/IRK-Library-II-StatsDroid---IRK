package com.alpaomega1136.statsdroid.feature.clt.domain.simulation

import kotlin.random.Random

interface PopulationSampler {
    val theoreticalMean: Double
    val theoreticalStandardDeviation: Double
    val recommendedDisplayRange: ClosedFloatingPointRange<Double>

    fun sample(random: Random): Double
}
