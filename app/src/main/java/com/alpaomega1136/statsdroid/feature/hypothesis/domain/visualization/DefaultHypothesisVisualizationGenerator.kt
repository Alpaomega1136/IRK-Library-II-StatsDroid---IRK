package com.alpaomega1136.statsdroid.feature.hypothesis.domain.visualization

import com.alpaomega1136.statsdroid.core.statistics.distribution.NormalDistributionCalculator
import com.alpaomega1136.statsdroid.core.statistics.distribution.StudentTDistributionCalculator
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.CriticalValues
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestResult
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestType
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.TailType
import javax.inject.Inject
import kotlin.math.abs

class DefaultHypothesisVisualizationGenerator @Inject constructor(
    private val normalDistributionCalculator: NormalDistributionCalculator,
    private val studentTDistributionCalculator: StudentTDistributionCalculator,
) : HypothesisVisualizationGenerator {

    override fun generate(result: HypothesisTestResult): HypothesisVisualizationData {
        val range = determineGraphRange(result)
        val minimumStatistic = -range
        val maximumStatistic = range
        val displayedStatistic = result.testStatistic.coerceIn(minimumStatistic, maximumStatistic)

        return HypothesisVisualizationData(
            curvePoints = generateCurvePoints(result, minimumStatistic, maximumStatistic),
            minimumStatistic = minimumStatistic,
            maximumStatistic = maximumStatistic,
            rejectionRegions = createRejectionRegions(result.tailType, result.criticalValues, minimumStatistic, maximumStatistic),
            pValueRegions = createPValueRegions(result.tailType, result.testStatistic, minimumStatistic, maximumStatistic),
            displayedTestStatistic = displayedStatistic,
            isTestStatisticClamped = displayedStatistic != result.testStatistic,
        )
    }

    private fun determineGraphRange(result: HypothesisTestResult): Double {
        val largestCriticalValue = listOfNotNull(result.criticalValues.lower, result.criticalValues.upper).maxOfOrNull(::abs) ?: 0.0
        val statisticContribution = minOf(abs(result.testStatistic) + STATISTIC_MARGIN, DEFAULT_MAXIMUM_STATISTIC_RANGE)

        return maxOf(DEFAULT_MINIMUM_RANGE, largestCriticalValue + CRITICAL_VALUE_MARGIN, statisticContribution)
    }

    private fun generateCurvePoints(
        result: HypothesisTestResult,
        minimumStatistic: Double,
        maximumStatistic: Double,
    ): List<DistributionCurvePoint> {
        return (0..CURVE_SEGMENTS).map { index ->
            val statistic = minimumStatistic + index.toDouble() / CURVE_SEGMENTS * (maximumStatistic - minimumStatistic)
            DistributionCurvePoint(statistic = statistic, density = calculateDensity(result, statistic))
        }
    }

    private fun calculateDensity(result: HypothesisTestResult, statistic: Double): Double {
        return when (result.testType) {
            HypothesisTestType.Z_TEST -> normalDistributionCalculator.probabilityDensity(statistic)
            HypothesisTestType.T_TEST -> studentTDistributionCalculator.probabilityDensity(
                tStatistic = statistic,
                degreesOfFreedom = requireNotNull(result.degreesOfFreedom) {
                    "t-Test visualization requires degrees of freedom."
                },
            )
        }
    }

    private fun createRejectionRegions(
        tailType: TailType,
        criticalValues: CriticalValues,
        minimumStatistic: Double,
        maximumStatistic: Double,
    ): List<CurveInterval> {
        return when (tailType) {
            TailType.LEFT_TAILED -> listOf(CurveInterval(minimumStatistic, requireNotNull(criticalValues.lower)))
            TailType.RIGHT_TAILED -> listOf(CurveInterval(requireNotNull(criticalValues.upper), maximumStatistic))
            TailType.TWO_TAILED -> listOf(
                CurveInterval(minimumStatistic, requireNotNull(criticalValues.lower)),
                CurveInterval(requireNotNull(criticalValues.upper), maximumStatistic),
            )
        }
    }

    private fun createPValueRegions(
        tailType: TailType,
        testStatistic: Double,
        minimumStatistic: Double,
        maximumStatistic: Double,
    ): List<CurveInterval> {
        return when (tailType) {
            TailType.LEFT_TAILED -> listOf(CurveInterval(minimumStatistic, testStatistic.coerceIn(minimumStatistic, maximumStatistic)))
            TailType.RIGHT_TAILED -> listOf(CurveInterval(testStatistic.coerceIn(minimumStatistic, maximumStatistic), maximumStatistic))
            TailType.TWO_TAILED -> {
                val absoluteStatistic = abs(testStatistic).coerceAtMost(maximumStatistic)
                listOf(CurveInterval(minimumStatistic, -absoluteStatistic), CurveInterval(absoluteStatistic, maximumStatistic))
            }
        }
    }

    companion object {
        private const val CURVE_SEGMENTS = 400
        private const val DEFAULT_MINIMUM_RANGE = 4.0
        private const val DEFAULT_MAXIMUM_STATISTIC_RANGE = 8.0
        private const val CRITICAL_VALUE_MARGIN = 0.75
        private const val STATISTIC_MARGIN = 0.75
    }
}
