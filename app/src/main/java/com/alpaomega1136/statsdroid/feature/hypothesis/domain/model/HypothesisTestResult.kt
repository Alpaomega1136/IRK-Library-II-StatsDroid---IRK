package com.alpaomega1136.statsdroid.feature.hypothesis.domain.model

data class HypothesisTestResult(
    val testType: HypothesisTestType,
    val testStatistic: Double,
    val pValue: Double,
    val significanceLevel: Double,
    val tailType: TailType,
    val criticalValues: CriticalValues,
    val decision: HypothesisDecision,
    val degreesOfFreedom: Int? = null,
)
