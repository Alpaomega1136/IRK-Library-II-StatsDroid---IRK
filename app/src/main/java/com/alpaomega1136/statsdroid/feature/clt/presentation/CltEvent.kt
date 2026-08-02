package com.alpaomega1136.statsdroid.feature.clt.presentation

import com.alpaomega1136.statsdroid.feature.clt.domain.model.PopulationShape
import com.alpaomega1136.statsdroid.feature.clt.domain.model.SimulationCount

sealed interface CltEvent {
    data class PopulationShapeChanged(val populationShape: PopulationShape) : CltEvent
    data class SampleSizeChanged(val sampleSize: Int) : CltEvent
    data class SimulationCountChanged(val simulationCount: SimulationCount) : CltEvent
    data object Simulate : CltEvent
}
