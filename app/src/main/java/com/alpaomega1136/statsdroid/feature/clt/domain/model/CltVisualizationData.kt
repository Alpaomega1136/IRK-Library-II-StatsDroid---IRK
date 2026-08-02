package com.alpaomega1136.statsdroid.feature.clt.domain.model

import com.alpaomega1136.statsdroid.core.statistics.model.HistogramData

data class CltVisualizationData(
    val simulation: CltSimulationResult,
    val populationHistogram: HistogramData,
    val samplingDistributionHistogram: HistogramData,
)
