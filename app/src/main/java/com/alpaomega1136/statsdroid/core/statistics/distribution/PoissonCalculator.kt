package com.alpaomega1136.statsdroid.core.statistics.distribution

import javax.inject.Inject
import kotlin.math.exp

class PoissonCalculator @Inject constructor() {

    fun cumulativeProbability(
        averageRate: Double,
        threshold: Int,
    ): Double {
        require(averageRate.isFinite()) {
            "Average rate must be finite."
        }

        require(averageRate > 0.0) {
            "Average rate must be greater than zero."
        }

        require(averageRate <= MAX_AVERAGE_RATE) {
            "Average rate exceeds the supported range."
        }

        require(threshold >= 0) {
            "Threshold cannot be negative."
        }

        var currentProbability = exp(-averageRate)
        var cumulativeProbability = currentProbability

        if (threshold == 0) {
            return cumulativeProbability
        }

        for (occurrences in 1..threshold) {
            currentProbability *= averageRate / occurrences.toDouble()
            cumulativeProbability += currentProbability

            if (cumulativeProbability >= 1.0 - CONVERGENCE_TOLERANCE) {
                return 1.0
            }

            val hasPassedDistributionPeak = occurrences > averageRate
            val contributionIsNegligible = currentProbability < CONVERGENCE_TOLERANCE

            if (hasPassedDistributionPeak && contributionIsNegligible) {
                break
            }
        }

        return cumulativeProbability.coerceIn(0.0, 1.0)
    }

    companion object {
        const val MAX_AVERAGE_RATE = 100.0
        private const val CONVERGENCE_TOLERANCE = 1e-15
    }
}

