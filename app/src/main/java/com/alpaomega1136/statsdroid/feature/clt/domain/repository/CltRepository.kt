package com.alpaomega1136.statsdroid.feature.clt.domain.repository

import com.alpaomega1136.statsdroid.feature.clt.domain.model.CltSimulationRequest
import com.alpaomega1136.statsdroid.feature.clt.domain.model.CltVisualizationData

interface CltRepository {
    suspend fun runSimulation(request: CltSimulationRequest): CltVisualizationData
}
