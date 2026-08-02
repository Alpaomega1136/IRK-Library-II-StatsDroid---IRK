package com.alpaomega1136.statsdroid.feature.clt.data.repository

import com.alpaomega1136.statsdroid.di.DefaultDispatcher
import com.alpaomega1136.statsdroid.feature.clt.data.local.CltLocalDataSource
import com.alpaomega1136.statsdroid.feature.clt.domain.model.CltSimulationRequest
import com.alpaomega1136.statsdroid.feature.clt.domain.model.CltVisualizationData
import com.alpaomega1136.statsdroid.feature.clt.domain.repository.CltRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultCltRepository @Inject constructor(
    private val localDataSource: CltLocalDataSource,
    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) : CltRepository {

    override suspend fun runSimulation(request: CltSimulationRequest): CltVisualizationData {
        return withContext(defaultDispatcher) {
            localDataSource.runSimulation(request)
        }
    }
}
