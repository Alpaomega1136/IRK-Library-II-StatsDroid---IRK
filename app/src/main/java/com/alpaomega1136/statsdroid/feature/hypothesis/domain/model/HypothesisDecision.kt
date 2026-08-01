package com.alpaomega1136.statsdroid.feature.hypothesis.domain.model

enum class HypothesisDecision(val displayName: String) {
    REJECT_NULL(displayName = "Reject H0"),
    FAIL_TO_REJECT_NULL(displayName = "Fail to Reject H0"),
}
