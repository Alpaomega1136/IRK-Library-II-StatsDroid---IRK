package com.alpaomega1136.statsdroid.feature.hypothesis.domain.model

data class TTestRequest(
    val hypothesizedMean: Double,
    val sampleMean: Double,
    val sampleStandardDeviation: Double,
    val sampleSize: Int,
    val significanceLevel: SignificanceLevel,
    val tailType: TailType,
) {
    val degreesOfFreedom: Int get() = sampleSize - 1
}
