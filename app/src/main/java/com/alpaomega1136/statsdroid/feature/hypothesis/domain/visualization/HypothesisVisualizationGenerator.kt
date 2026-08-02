package com.alpaomega1136.statsdroid.feature.hypothesis.domain.visualization

import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestResult

interface HypothesisVisualizationGenerator {
    fun generate(result: HypothesisTestResult): HypothesisVisualizationData
}
