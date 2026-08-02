package com.alpaomega1136.statsdroid.core.statistics.distribution

import javax.inject.Inject
import org.apache.commons.statistics.distribution.TDistribution

class StudentTDistributionCalculator @Inject constructor() {

    fun probabilityDensity(
        tStatistic: Double,
        degreesOfFreedom: Int,
    ): Double {
        validateInput(value = tStatistic, degreesOfFreedom = degreesOfFreedom)
        return createDistribution(degreesOfFreedom).density(tStatistic)
    }

    fun cumulativeProbability(
        tStatistic: Double,
        degreesOfFreedom: Int,
    ): Double {
        validateInput(value = tStatistic, degreesOfFreedom = degreesOfFreedom)
        return createDistribution(degreesOfFreedom).cumulativeProbability(tStatistic)
    }

    fun survivalProbability(
        tStatistic: Double,
        degreesOfFreedom: Int,
    ): Double {
        validateInput(value = tStatistic, degreesOfFreedom = degreesOfFreedom)
        return createDistribution(degreesOfFreedom).survivalProbability(tStatistic)
    }

    fun inverseCumulativeProbability(
        probability: Double,
        degreesOfFreedom: Int,
    ): Double {
        validateProbability(probability)
        validateDegreesOfFreedom(degreesOfFreedom)
        return createDistribution(degreesOfFreedom).inverseCumulativeProbability(probability)
    }

    fun inverseSurvivalProbability(
        probability: Double,
        degreesOfFreedom: Int,
    ): Double {
        validateProbability(probability)
        validateDegreesOfFreedom(degreesOfFreedom)
        return createDistribution(degreesOfFreedom).inverseSurvivalProbability(probability)
    }

    private fun createDistribution(degreesOfFreedom: Int): TDistribution {
        return TDistribution.of(degreesOfFreedom.toDouble())
    }

    private fun validateInput(value: Double, degreesOfFreedom: Int) {
        require(value.isFinite()) {
            "T-statistic must be a finite number."
        }
        validateDegreesOfFreedom(degreesOfFreedom)
    }

    private fun validateDegreesOfFreedom(degreesOfFreedom: Int) {
        require(degreesOfFreedom >= 1) {
            "Degrees of freedom must be at least 1."
        }
    }

    private fun validateProbability(probability: Double) {
        require(probability.isFinite()) {
            "Probability must be a finite number."
        }
        require(probability > 0.0 && probability < 1.0) {
            "Probability must be strictly between 0 and 1."
        }
    }
}
