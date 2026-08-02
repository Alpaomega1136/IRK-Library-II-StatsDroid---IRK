package com.alpaomega1136.statsdroid.feature.hypothesis.domain.validation

import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisConstraints
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.SignificanceLevel
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.TTestRequest
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.TailType
import javax.inject.Inject

class TTestInputValidator @Inject constructor() {

    fun validate(
        hypothesizedMeanText: String,
        sampleMeanText: String,
        sampleStandardDeviationText: String,
        sampleSizeText: String,
        significanceLevel: SignificanceLevel,
        tailType: TailType,
    ): HypothesisValidationResult<TTestRequest> {
        val errors = mutableMapOf<HypothesisInputField, String>()
        val hypothesizedMean = hypothesizedMeanText.toDoubleOrNull()
        val sampleMean = sampleMeanText.toDoubleOrNull()
        val standardDeviation = sampleStandardDeviationText.toDoubleOrNull()
        val sampleSize = sampleSizeText.toIntOrNull()

        validateMean(hypothesizedMeanText, hypothesizedMean, "Hypothesized mean is required.", HypothesisInputField.HYPOTHESIZED_MEAN, errors)
        validateMean(sampleMeanText, sampleMean, "Sample mean is required.", HypothesisInputField.SAMPLE_MEAN, errors)
        validateStandardDeviation(sampleStandardDeviationText, standardDeviation, errors)
        validateSampleSize(sampleSizeText, sampleSize, errors)

        if (errors.isNotEmpty()) return HypothesisValidationResult.Invalid(errors)
        return HypothesisValidationResult.Valid(
            TTestRequest(
                hypothesizedMean = requireNotNull(hypothesizedMean),
                sampleMean = requireNotNull(sampleMean),
                sampleStandardDeviation = requireNotNull(standardDeviation),
                sampleSize = requireNotNull(sampleSize),
                significanceLevel = significanceLevel,
                tailType = tailType,
            ),
        )
    }

    private fun validateMean(text: String, parsedValue: Double?, requiredMessage: String, field: HypothesisInputField, errors: MutableMap<HypothesisInputField, String>) {
        when {
            text.isBlank() || text == "-" -> errors[field] = requiredMessage
            parsedValue == null -> errors[field] = "Mean must be a valid number."
            !parsedValue.isFinite() -> errors[field] = "Mean must be finite."
            parsedValue !in HypothesisConstraints.MIN_MEAN..HypothesisConstraints.MAX_MEAN -> errors[field] = "Mean must be between -100 and 100."
        }
    }

    private fun validateStandardDeviation(text: String, parsedValue: Double?, errors: MutableMap<HypothesisInputField, String>) {
        when {
            text.isBlank() -> errors[HypothesisInputField.STANDARD_DEVIATION] = "Sample standard deviation is required."
            parsedValue == null -> errors[HypothesisInputField.STANDARD_DEVIATION] = "Sample standard deviation must be a valid number."
            !parsedValue.isFinite() -> errors[HypothesisInputField.STANDARD_DEVIATION] = "Sample standard deviation must be finite."
            parsedValue <= 0.0 -> errors[HypothesisInputField.STANDARD_DEVIATION] = "Sample standard deviation must be greater than 0."
            parsedValue > HypothesisConstraints.MAX_STANDARD_DEVIATION -> errors[HypothesisInputField.STANDARD_DEVIATION] = "Sample standard deviation must not exceed 50."
        }
    }

    private fun validateSampleSize(text: String, parsedValue: Int?, errors: MutableMap<HypothesisInputField, String>) {
        when {
            text.isBlank() -> errors[HypothesisInputField.SAMPLE_SIZE] = "Sample size is required."
            parsedValue == null -> errors[HypothesisInputField.SAMPLE_SIZE] = "Sample size must be an integer."
            parsedValue !in HypothesisConstraints.MIN_T_SAMPLE_SIZE..HypothesisConstraints.MAX_SAMPLE_SIZE -> errors[HypothesisInputField.SAMPLE_SIZE] = "Sample size must be between 2 and 500."
        }
    }
}
