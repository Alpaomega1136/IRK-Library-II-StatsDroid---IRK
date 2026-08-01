package com.alpaomega1136.statsdroid.feature.hypothesis.domain.calculator

import com.alpaomega1136.statsdroid.core.statistics.distribution.NormalDistributionCalculator
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisDecision
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.HypothesisTestType
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.SignificanceLevel
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.TailType
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.model.ZTestRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class ZTestCalculatorTest {

    private lateinit var calculator: ZTestCalculator

    @Before
    fun setUp() {
        calculator = ZTestCalculator(NormalDistributionCalculator())
    }

    @Test
    fun `test statistic is calculated correctly`() {
        val result = calculator.calculate(createRequest(sampleMean = 52.0))

        assertEquals(2.0, result.testStatistic, STRICT_TOLERANCE)
        assertEquals(HypothesisTestType.Z_TEST, result.testType)
        assertNull(result.degreesOfFreedom)
    }

    @Test
    fun `two tailed test calculates correct p value`() {
        assertEquals(
            0.04550026,
            calculator.calculate(createRequest(sampleMean = 52.0)).pValue,
            APPROXIMATION_TOLERANCE,
        )
    }

    @Test
    fun `two tailed test rejects null hypothesis`() {
        assertEquals(
            HypothesisDecision.REJECT_NULL,
            calculator.calculate(createRequest(sampleMean = 52.0)).decision,
        )
    }

    @Test
    fun `right tailed test calculates correct p value`() {
        val result = calculator.calculate(createRequest(sampleMean = 52.0, tailType = TailType.RIGHT_TAILED))

        assertEquals(0.02275013, result.pValue, APPROXIMATION_TOLERANCE)
    }

    @Test
    fun `left tailed negative statistic rejects null hypothesis`() {
        val result = calculator.calculate(createRequest(sampleMean = 48.0, tailType = TailType.LEFT_TAILED))

        assertEquals(-2.0, result.testStatistic, STRICT_TOLERANCE)
        assertEquals(0.02275013, result.pValue, APPROXIMATION_TOLERANCE)
        assertEquals(HypothesisDecision.REJECT_NULL, result.decision)
    }

    @Test
    fun `two tailed test with small statistic fails to reject null`() {
        val result = calculator.calculate(createRequest(sampleMean = 51.0))

        assertEquals(1.0, result.testStatistic, STRICT_TOLERANCE)
        assertEquals(HypothesisDecision.FAIL_TO_REJECT_NULL, result.decision)
    }

    @Test
    fun `equal sample and hypothesized means produce statistic zero`() {
        val result = calculator.calculate(createRequest(sampleMean = 50.0))

        assertEquals(0.0, result.testStatistic, STRICT_TOLERANCE)
        assertEquals(1.0, result.pValue, APPROXIMATION_TOLERANCE)
    }

    @Test
    fun `two tailed critical values at five percent are approximately correct`() {
        val criticalValues = calculator.calculate(createRequest(sampleMean = 50.0)).criticalValues

        assertNotNull(criticalValues.lower)
        assertNotNull(criticalValues.upper)
        assertEquals(-1.959963984, criticalValues.lower!!, CRITICAL_VALUE_TOLERANCE)
        assertEquals(1.959963984, criticalValues.upper!!, CRITICAL_VALUE_TOLERANCE)
    }

    @Test
    fun `right tailed critical value at five percent is approximately correct`() {
        val result = calculator.calculate(createRequest(sampleMean = 50.0, tailType = TailType.RIGHT_TAILED))

        assertNull(result.criticalValues.lower)
        assertEquals(1.644853627, result.criticalValues.upper!!, CRITICAL_VALUE_TOLERANCE)
    }

    @Test
    fun `zero population standard deviation throws exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            calculator.calculate(createRequest(populationStandardDeviation = 0.0))
        }
    }

    @Test
    fun `sample size below one throws exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            calculator.calculate(createRequest(sampleSize = 0))
        }
    }

    private fun createRequest(
        hypothesizedMean: Double = 50.0,
        sampleMean: Double = 52.0,
        populationStandardDeviation: Double = 5.0,
        sampleSize: Int = 25,
        significanceLevel: SignificanceLevel = SignificanceLevel.FIVE_PERCENT,
        tailType: TailType = TailType.TWO_TAILED,
    ): ZTestRequest {
        return ZTestRequest(
            hypothesizedMean = hypothesizedMean,
            sampleMean = sampleMean,
            populationStandardDeviation = populationStandardDeviation,
            sampleSize = sampleSize,
            significanceLevel = significanceLevel,
            tailType = tailType,
        )
    }

    companion object {
        private const val STRICT_TOLERANCE = 1e-10
        private const val APPROXIMATION_TOLERANCE = 1e-6
        private const val CRITICAL_VALUE_TOLERANCE = 1e-4
    }
}
