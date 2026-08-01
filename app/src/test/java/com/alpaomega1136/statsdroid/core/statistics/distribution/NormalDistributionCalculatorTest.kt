package com.alpaomega1136.statsdroid.core.statistics.distribution

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class NormalDistributionCalculatorTest {

    private lateinit var calculator: NormalDistributionCalculator

    @Before
    fun setUp() {
        calculator = NormalDistributionCalculator()
    }

    @Test
    fun `inverse cumulative probability at one half equals zero`() {
        assertEquals(
            0.0,
            calculator.inverseCumulativeProbability(probability = 0.5),
            INVERSE_TOLERANCE,
        )
    }

    @Test
    fun `inverse cumulative probability at zero point nine seven five is approximately one point nine six`() {
        assertEquals(
            1.959963984,
            calculator.inverseCumulativeProbability(probability = 0.975),
            INVERSE_TOLERANCE,
        )
    }

    @Test
    fun `inverse cumulative probabilities are symmetric`() {
        val lower = calculator.inverseCumulativeProbability(probability = 0.025)
        val upper = calculator.inverseCumulativeProbability(probability = 0.975)

        assertEquals(-upper, lower, INVERSE_TOLERANCE)
    }

    @Test
    fun `inverse cumulative probability rejects boundary probability`() {
        assertThrows(IllegalArgumentException::class.java) {
            calculator.inverseCumulativeProbability(probability = 0.0)
        }
    }

    companion object {
        private const val INVERSE_TOLERANCE = 1e-4
    }
}
