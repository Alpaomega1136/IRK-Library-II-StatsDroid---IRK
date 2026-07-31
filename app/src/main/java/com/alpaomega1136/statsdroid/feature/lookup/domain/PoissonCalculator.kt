package com.alpaomega1136.statsdroid.feature.lookup.domain

import kotlin.math.exp

class PoissonCalculator {

    fun cumulativeProbability(
        averageRate: Double,
        threshold: Int,
    ): Double {
        require(averageRate > 0.0) {
            "Average rate must be greater than 0."
        }

        require(averageRate <= MAX_AVERAGE_RATE) {
            "Average rate must not exceed $MAX_AVERAGE_RATE."
        }

        require(threshold >= 0) {
            "Threshold must not be negative."
        }

        var currentProbability = exp(-averageRate)
        var cumulativeProbability = currentProbability

        for (occurrences in 1..threshold) {
            currentProbability *= averageRate / occurrences
            cumulativeProbability += currentProbability

            if (cumulativeProbability >= 1.0) {
                return 1.0
            }
        }

        return cumulativeProbability.coerceIn(0.0, 1.0)
    }

    companion object {
        const val MAX_AVERAGE_RATE = 100.0
    }
}
