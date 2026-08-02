package com.alpaomega1136.statsdroid.feature.hypothesis.data.repository

import com.alpaomega1136.statsdroid.feature.hypothesis.data.local.HypothesisLocalDataSource
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestResult
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.TTestRequest
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.ZTestRequest
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.repository.HypothesisRepository
import javax.inject.Inject

class DefaultHypothesisRepository @Inject constructor(
    private val localDataSource: HypothesisLocalDataSource,
) : HypothesisRepository {

    override fun calculateZTest(request: ZTestRequest): HypothesisTestResult = localDataSource.calculateZTest(request)

    override fun calculateTTest(request: TTestRequest): HypothesisTestResult = localDataSource.calculateTTest(request)
}
