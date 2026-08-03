package com.alpaomega1136.statsdroid.feature.hypothesis.presentation

import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestType
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.SignificanceLevel
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.TailType

sealed interface HypothesisEvent {
    data class TestTypeChanged(val testType: HypothesisTestType) : HypothesisEvent
    data class HypothesizedMeanChanged(val value: String) : HypothesisEvent
    data class HypothesizedMeanSliderChanged(val value: Double) : HypothesisEvent
    data class SampleMeanChanged(val value: String) : HypothesisEvent
    data class SampleMeanSliderChanged(val value: Double) : HypothesisEvent
    data class StandardDeviationChanged(val value: String) : HypothesisEvent
    data class SampleSizeChanged(val value: String) : HypothesisEvent
    data class SignificanceLevelChanged(val significanceLevel: SignificanceLevel) : HypothesisEvent
    data class TailTypeChanged(val tailType: TailType) : HypothesisEvent
    data object LoadWorkedExample : HypothesisEvent
    data object Calculate : HypothesisEvent
}
