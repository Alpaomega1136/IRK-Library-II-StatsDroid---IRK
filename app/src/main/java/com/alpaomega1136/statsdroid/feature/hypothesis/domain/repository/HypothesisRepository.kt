package com.alpaomega1136.statsdroid.feature.hypothesis.domain.repository

import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestResult
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.TTestRequest
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.ZTestRequest

interface HypothesisRepository {
    fun calculateZTest(request: ZTestRequest): HypothesisTestResult
    fun calculateTTest(request: TTestRequest): HypothesisTestResult
}
