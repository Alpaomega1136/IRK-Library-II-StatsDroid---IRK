package com.alpaomega1136.statsdroid.feature.clt.presentation

import com.alpaomega1136.statsdroid.feature.clt.domain.model.CltVisualizationData
import com.alpaomega1136.statsdroid.feature.clt.domain.model.PopulationShape
import com.alpaomega1136.statsdroid.feature.clt.domain.model.SimulationCount

data class CltUiState(
    val selectedPopulationShape: PopulationShape = PopulationShape.UNIFORM,
    val sampleSize: Int = 30,
    val simulationCount: SimulationCount = SimulationCount.ONE_THOUSAND,
    val isRunning: Boolean = false,
    val result: CltVisualizationData? = null,
    val errorMessage: String? = null,
)
