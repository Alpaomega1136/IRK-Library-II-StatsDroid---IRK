package com.alpaomega1136.statsdroid.feature.hypothesis.presentation

import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestResult
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestType
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.SignificanceLevel
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.TailType
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.visualization.HypothesisVisualizationData

data class HypothesisInputState(
    val hypothesizedMean: String = "0.0",
    val hypothesizedMeanValue: Double = 0.0,
    val sampleMean: String = "0.0",
    val sampleMeanValue: Double = 0.0,
    val standardDeviation: String = "1.0",
    val sampleSize: String = "30",
    val hypothesizedMeanError: String? = null,
    val sampleMeanError: String? = null,
    val standardDeviationError: String? = null,
    val sampleSizeError: String? = null,
)

data class HypothesisUiState(
    val selectedTestType: HypothesisTestType = HypothesisTestType.Z_TEST,
    val input: HypothesisInputState = HypothesisInputState(),
    val significanceLevel: SignificanceLevel = SignificanceLevel.FIVE_PERCENT,
    val tailType: TailType = TailType.TWO_TAILED,
    val result: HypothesisTestResult? = null,
    val visualization: HypothesisVisualizationData? = null,
)
