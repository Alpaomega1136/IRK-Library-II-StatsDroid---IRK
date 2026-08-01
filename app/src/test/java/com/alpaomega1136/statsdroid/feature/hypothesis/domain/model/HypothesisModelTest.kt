package com.alpaomega1136.statsdroid.feature.hypothesis.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HypothesisModelTest {

    @Test
    fun `t test degrees of freedom equals sample size minus one`() {
        val request = TTestRequest(
            hypothesizedMean = 50.0,
            sampleMean = 52.0,
            sampleStandardDeviation = 5.0,
            sampleSize = 25,
            significanceLevel = SignificanceLevel.FIVE_PERCENT,
            tailType = TailType.TWO_TAILED,
        )

        assertEquals(24, request.degreesOfFreedom)
    }

    @Test
    fun `z test result does not require degrees of freedom`() {
        val result = HypothesisTestResult(
            testType = HypothesisTestType.Z_TEST,
            testStatistic = 1.96,
            pValue = 0.05,
            significanceLevel = 0.05,
            tailType = TailType.TWO_TAILED,
            criticalValues = CriticalValues(lower = -1.96, upper = 1.96),
            decision = HypothesisDecision.REJECT_NULL,
            degreesOfFreedom = null,
        )

        assertNull(result.degreesOfFreedom)
    }

    @Test
    fun `five percent significance stores correct numeric value`() {
        assertEquals(0.05, SignificanceLevel.FIVE_PERCENT.value, TOLERANCE)
    }

    companion object {
        private const val TOLERANCE = 1e-10
    }
}
