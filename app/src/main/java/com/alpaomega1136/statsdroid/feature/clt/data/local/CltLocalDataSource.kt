package com.alpaomega1136.statsdroid.feature.clt.data.local

import com.alpaomega1136.statsdroid.feature.clt.domain.model.CltSimulationRequest
import com.alpaomega1136.statsdroid.feature.clt.domain.model.CltVisualizationData

interface CltLocalDataSource {
    fun runSimulation(request: CltSimulationRequest): CltVisualizationData
}
