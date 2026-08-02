package com.alpaomega1136.statsdroid.feature.clt.domain.model

import com.alpaomega1136.statsdroid.core.statistics.model.DensityCurvePoint

data class CltSimulationResult(
    val populationShape: PopulationShape,
    val sampleSize: Int,
    val numberOfSamples: Int,
    val populationPreviewValues: List<Double>,
    val sampleMeans: List<Double>,
    val theoreticalMean: Double,
    val populationStandardDeviation: Double,
    val populationDisplayMinimum: Double,
    val populationDisplayMaximum: Double,
    val empiricalMean: Double,
    val empiricalSamplingStandardDeviation: Double,
    val theoreticalStandardError: Double,
    val theoreticalNormalCurve: List<DensityCurvePoint>,
)
