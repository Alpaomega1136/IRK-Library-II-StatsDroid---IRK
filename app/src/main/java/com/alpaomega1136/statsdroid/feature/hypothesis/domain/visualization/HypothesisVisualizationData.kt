package com.alpaomega1136.statsdroid.feature.hypothesis.domain.visualization

data class DistributionCurvePoint(
    val statistic: Double,
    val density: Double,
)

data class CurveInterval(
    val start: Double,
    val end: Double,
) {
    init {
        require(start <= end) {
            "Curve interval start must not exceed its end."
        }
    }
}

data class HypothesisVisualizationData(
    val curvePoints: List<DistributionCurvePoint>,
    val minimumStatistic: Double,
    val maximumStatistic: Double,
    val rejectionRegions: List<CurveInterval>,
    val pValueRegions: List<CurveInterval>,
    val displayedTestStatistic: Double,
    val isTestStatisticClamped: Boolean,
)
