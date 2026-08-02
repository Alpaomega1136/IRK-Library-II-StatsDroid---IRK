package com.alpaomega1136.statsdroid.feature.clt.domain.model

data class CltSimulationRequest(
    val populationShape: PopulationShape,
    val sampleSize: Int,
    val simulationCount: SimulationCount,
)
