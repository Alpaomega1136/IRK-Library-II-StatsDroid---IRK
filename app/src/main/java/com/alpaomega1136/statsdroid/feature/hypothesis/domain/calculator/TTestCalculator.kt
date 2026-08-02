package com.alpaomega1136.statsdroid.feature.hypothesis.domain.calculator

import com.alpaomega1136.statsdroid.core.statistics.distribution.StudentTDistributionCalculator
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.CriticalValues
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisDecision
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestResult
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestType
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.TTestRequest
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.TailType
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.sqrt

class TTestCalculator @Inject constructor(
    private val distributionCalculator: StudentTDistributionCalculator,
) {

    fun calculate(request: TTestRequest): HypothesisTestResult {
        validateRequest(request)

        val degreesOfFreedom = request.degreesOfFreedom
        val standardError = request.sampleStandardDeviation / sqrt(request.sampleSize.toDouble())
        val testStatistic = (request.sampleMean - request.hypothesizedMean) / standardError
        val pValue = calculatePValue(
            testStatistic = testStatistic,
            degreesOfFreedom = degreesOfFreedom,
            tailType = request.tailType,
        )

        return HypothesisTestResult(
            testType = HypothesisTestType.T_TEST,
            testStatistic = testStatistic,
            pValue = pValue,
            significanceLevel = request.significanceLevel.value,
            tailType = request.tailType,
            criticalValues = calculateCriticalValues(
                significanceLevel = request.significanceLevel.value,
                degreesOfFreedom = degreesOfFreedom,
                tailType = request.tailType,
            ),
            decision = if (pValue <= request.significanceLevel.value) {
                HypothesisDecision.REJECT_NULL
            } else {
                HypothesisDecision.FAIL_TO_REJECT_NULL
            },
            degreesOfFreedom = degreesOfFreedom,
        )
    }

    private fun calculatePValue(
        testStatistic: Double,
        degreesOfFreedom: Int,
        tailType: TailType,
    ): Double {
        val pValue = when (tailType) {
            TailType.LEFT_TAILED -> distributionCalculator.cumulativeProbability(
                tStatistic = testStatistic,
                degreesOfFreedom = degreesOfFreedom,
            )
            TailType.RIGHT_TAILED -> distributionCalculator.survivalProbability(
                tStatistic = testStatistic,
                degreesOfFreedom = degreesOfFreedom,
            )
            TailType.TWO_TAILED -> 2.0 * distributionCalculator.survivalProbability(
                tStatistic = abs(testStatistic),
                degreesOfFreedom = degreesOfFreedom,
            )
        }

        return pValue.coerceIn(0.0, 1.0)
    }

    private fun calculateCriticalValues(
        significanceLevel: Double,
        degreesOfFreedom: Int,
        tailType: TailType,
    ): CriticalValues {
        return when (tailType) {
            TailType.LEFT_TAILED -> CriticalValues(
                lower = distributionCalculator.inverseCumulativeProbability(
                    probability = significanceLevel,
                    degreesOfFreedom = degreesOfFreedom,
                ),
            )
            TailType.RIGHT_TAILED -> CriticalValues(
                upper = distributionCalculator.inverseSurvivalProbability(
                    probability = significanceLevel,
                    degreesOfFreedom = degreesOfFreedom,
                ),
            )
            TailType.TWO_TAILED -> {
                val probabilityPerTail = significanceLevel / 2.0
                CriticalValues(
                    lower = distributionCalculator.inverseCumulativeProbability(
                        probability = probabilityPerTail,
                        degreesOfFreedom = degreesOfFreedom,
                    ),
                    upper = distributionCalculator.inverseSurvivalProbability(
                        probability = probabilityPerTail,
                        degreesOfFreedom = degreesOfFreedom,
                    ),
                )
            }
        }
    }

    private fun validateRequest(request: TTestRequest) {
        require(request.hypothesizedMean.isFinite()) {
            "Hypothesized mean must be finite."
        }
        require(request.sampleMean.isFinite()) {
            "Sample mean must be finite."
        }
        require(request.sampleStandardDeviation.isFinite()) {
            "Sample standard deviation must be finite."
        }
        require(request.sampleStandardDeviation > 0.0) {
            "Sample standard deviation must be greater than 0."
        }
        require(request.sampleSize >= 2) {
            "Sample size must be at least 2."
        }
    }
}
