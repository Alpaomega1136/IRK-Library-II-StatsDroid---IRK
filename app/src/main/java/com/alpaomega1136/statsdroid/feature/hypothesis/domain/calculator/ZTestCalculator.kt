package com.alpaomega1136.statsdroid.feature.hypothesis.domain.calculator

import com.alpaomega1136.statsdroid.core.statistics.distribution.NormalDistributionCalculator
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.CriticalValues
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisDecision
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestResult
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestType
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.TailType
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.ZTestRequest
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.sqrt

class ZTestCalculator @Inject constructor(
    private val normalDistributionCalculator: NormalDistributionCalculator,
) {

    fun calculate(request: ZTestRequest): HypothesisTestResult {
        validateRequest(request)

        val standardError = request.populationStandardDeviation / sqrt(request.sampleSize.toDouble())
        val testStatistic = (request.sampleMean - request.hypothesizedMean) / standardError
        val pValue = calculatePValue(testStatistic = testStatistic, tailType = request.tailType)

        return HypothesisTestResult(
            testType = HypothesisTestType.Z_TEST,
            testStatistic = testStatistic,
            pValue = pValue,
            significanceLevel = request.significanceLevel.value,
            tailType = request.tailType,
            criticalValues = calculateCriticalValues(
                significanceLevel = request.significanceLevel.value,
                tailType = request.tailType,
            ),
            decision = if (pValue <= request.significanceLevel.value) {
                HypothesisDecision.REJECT_NULL
            } else {
                HypothesisDecision.FAIL_TO_REJECT_NULL
            },
            degreesOfFreedom = null,
        )
    }

    private fun calculatePValue(testStatistic: Double, tailType: TailType): Double {
        val cumulativeProbability = normalDistributionCalculator.cumulativeProbability(testStatistic)
        val pValue = when (tailType) {
            TailType.LEFT_TAILED -> cumulativeProbability
            TailType.RIGHT_TAILED -> 1.0 - cumulativeProbability
            TailType.TWO_TAILED -> 2.0 * (
                1.0 - normalDistributionCalculator.cumulativeProbability(abs(testStatistic))
                )
        }

        return pValue.coerceIn(0.0, 1.0)
    }

    private fun calculateCriticalValues(
        significanceLevel: Double,
        tailType: TailType,
    ): CriticalValues {
        return when (tailType) {
            TailType.LEFT_TAILED -> CriticalValues(
                lower = normalDistributionCalculator.inverseCumulativeProbability(significanceLevel),
            )
            TailType.RIGHT_TAILED -> CriticalValues(
                upper = normalDistributionCalculator.inverseCumulativeProbability(1.0 - significanceLevel),
            )
            TailType.TWO_TAILED -> {
                val probabilityPerTail = significanceLevel / 2.0
                CriticalValues(
                    lower = normalDistributionCalculator.inverseCumulativeProbability(probabilityPerTail),
                    upper = normalDistributionCalculator.inverseCumulativeProbability(1.0 - probabilityPerTail),
                )
            }
        }
    }

    private fun validateRequest(request: ZTestRequest) {
        require(request.hypothesizedMean.isFinite()) {
            "Hypothesized mean must be finite."
        }
        require(request.sampleMean.isFinite()) {
            "Sample mean must be finite."
        }
        require(request.populationStandardDeviation.isFinite()) {
            "Population standard deviation must be finite."
        }
        require(request.populationStandardDeviation > 0.0) {
            "Population standard deviation must be greater than 0."
        }
        require(request.sampleSize >= 1) {
            "Sample size must be at least 1."
        }
    }
}
