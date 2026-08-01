package com.alpaomega1136.statsdroid.feature.lookup.domain.validation

import com.alpaomega1136.statsdroid.feature.lookup.domain.model.StandardNormalRequest
import javax.inject.Inject

class StandardNormalInputValidator @Inject constructor() {

    fun validate(zScoreText: String): LookupValidationResult<StandardNormalRequest> {
        val errors = mutableMapOf<LookupInputField, String>()
        val zScore = zScoreText.toDoubleOrNull()

        when {
            zScoreText.isBlank() || zScoreText == "-" -> errors[LookupInputField.NORMAL_Z_SCORE] =
                "Z-score is required."
            zScore == null -> errors[LookupInputField.NORMAL_Z_SCORE] =
                "Z-score must be a valid number."
            !zScore.isFinite() -> errors[LookupInputField.NORMAL_Z_SCORE] =
                "Z-score must be a finite number."
            zScore !in MIN_Z_SCORE..MAX_Z_SCORE -> errors[LookupInputField.NORMAL_Z_SCORE] =
                "Z-score must be between -5.0 and 5.0."
        }

        if (errors.isNotEmpty()) return LookupValidationResult.Invalid(errors)
        return LookupValidationResult.Valid(StandardNormalRequest(zScore = requireNotNull(zScore)))
    }

    companion object {
        const val MIN_Z_SCORE = -5.0
        const val MAX_Z_SCORE = 5.0
    }
}
