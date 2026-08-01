package com.alpaomega1136.statsdroid.feature.hypothesis.domain.model

data class ZTestRequest(
    val hypothesizedMean: Double,
    val sampleMean: Double,
    val populationStandardDeviation: Double,
    val sampleSize: Int,
    val significanceLevel: SignificanceLevel,
    val tailType: TailType,
)
