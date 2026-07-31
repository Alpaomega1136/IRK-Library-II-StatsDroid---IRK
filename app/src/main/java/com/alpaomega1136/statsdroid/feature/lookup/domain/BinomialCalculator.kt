package com.alpaomega1136.statsdroid.feature.lookup.domain

import kotlin.math.pow

class BinomialCalculator {

    fun cumulativeProbability(
        numberOfTrials: Int,
        threshold: Int,
        successProbability: Double,
    ): Double {
        require(numberOfTrials >= 1) {
            "Number of trials must be at least 1."
        }

        require(numberOfTrials <= 20) {
            "Number of trials must be at most 20."
        }

        require(threshold in 0..numberOfTrials) {
            "Threshold must be between 0 and the number of trials."
        }

        require(successProbability in 0.0..1.0) {
            "Success probability must be between 0 and 1."
        }

        return (0..threshold).sumOf { numberOfSuccesses ->
            probabilityMass(
                numberOfTrials = numberOfTrials,
                numberOfSuccesses = numberOfSuccesses,
                successProbability = successProbability,
            )
        }.coerceIn(0.0, 1.0)
    }

    private fun probabilityMass(
        numberOfTrials: Int,
        numberOfSuccesses: Int,
        successProbability: Double,
    ): Double {
        val combinations = combination(
            n = numberOfTrials,
            k = numberOfSuccesses,
        )

        val successTerm = successProbability.pow(numberOfSuccesses)
        val failureTerm = (1.0 - successProbability)
            .pow(numberOfTrials - numberOfSuccesses)

        return combinations * successTerm * failureTerm
    }

    private fun combination(
        n: Int,
        k: Int,
    ): Double {
        val optimizedK = minOf(k, n - k)

        if (optimizedK == 0) {
            return 1.0
        }

        var result = 1.0

        for (index in 1..optimizedK) {
            result *= (n - optimizedK + index).toDouble()
            result /= index.toDouble()
        }

        return result
    }
}
