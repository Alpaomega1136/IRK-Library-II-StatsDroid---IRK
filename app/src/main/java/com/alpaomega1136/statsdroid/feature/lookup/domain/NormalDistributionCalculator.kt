package com.alpaomega1136.statsdroid.feature.lookup.domain

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

class NormalDistributionCalculator {

    fun probabilityDensity(z: Double): Double {
        require(z.isFinite()) {
            "Z-score must be a finite number."
        }

        return NORMALIZATION_CONSTANT * exp(-(z * z) / 2.0)
    }

    fun cumulativeProbability(z: Double): Double {
        require(z.isFinite()) {
            "Z-score must be a finite number."
        }

        if (z == 0.0) {
            return 0.5
        }

        val absoluteZ = abs(z)
        val t = 1.0 / (1.0 + APPROXIMATION_FACTOR * absoluteZ)
        val polynomial =
            COEFFICIENT_1 * t +
                COEFFICIENT_2 * t.pow(2.0) +
                COEFFICIENT_3 * t.pow(3.0) +
                COEFFICIENT_4 * t.pow(4.0) +
                COEFFICIENT_5 * t.pow(5.0)

        val positiveCumulative =
            1.0 - probabilityDensity(absoluteZ) * polynomial

        return if (z > 0.0) {
            positiveCumulative
        } else {
            1.0 - positiveCumulative
        }.coerceIn(0.0, 1.0)
    }

    fun probabilityBetween(
        lowerZ: Double,
        upperZ: Double,
    ): Double {
        require(lowerZ.isFinite() && upperZ.isFinite()) {
            "Z-scores must be finite numbers."
        }

        require(lowerZ <= upperZ) {
            "Lower Z-score must not exceed upper Z-score."
        }

        return (
            cumulativeProbability(upperZ) -
                cumulativeProbability(lowerZ)
            ).coerceIn(0.0, 1.0)
    }

    companion object {
        private val NORMALIZATION_CONSTANT =
            1.0 / sqrt(2.0 * PI)

        private const val APPROXIMATION_FACTOR = 0.2316419

        private const val COEFFICIENT_1 = 0.319381530
        private const val COEFFICIENT_2 = -0.356563782
        private const val COEFFICIENT_3 = 1.781477937
        private const val COEFFICIENT_4 = -1.821255978
        private const val COEFFICIENT_5 = 1.330274429
    }
}
