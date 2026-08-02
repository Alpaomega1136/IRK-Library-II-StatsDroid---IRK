package com.alpaomega1136.statsdroid.feature.hypothesis.data.local

import com.alpaomega1136.statsdroid.feature.hypothesis.domain.calculator.TTestCalculator
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.calculator.ZTestCalculator
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestResult
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.TTestRequest
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.ZTestRequest
import javax.inject.Inject

class DefaultHypothesisLocalDataSource @Inject constructor(
    private val zTestCalculator: ZTestCalculator,
    private val tTestCalculator: TTestCalculator,
) : HypothesisLocalDataSource {

    override fun calculateZTest(request: ZTestRequest): HypothesisTestResult = zTestCalculator.calculate(request)

    override fun calculateTTest(request: TTestRequest): HypothesisTestResult = tTestCalculator.calculate(request)
}
