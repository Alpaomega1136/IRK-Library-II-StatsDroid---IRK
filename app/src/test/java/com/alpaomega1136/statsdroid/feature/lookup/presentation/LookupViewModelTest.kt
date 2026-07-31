package com.alpaomega1136.statsdroid.feature.lookup.presentation

import com.alpaomega1136.statsdroid.feature.lookup.domain.model.BinomialRequest
import com.alpaomega1136.statsdroid.feature.lookup.domain.model.PoissonRequest
import com.alpaomega1136.statsdroid.feature.lookup.domain.model.StandardNormalRequest
import com.alpaomega1136.statsdroid.feature.lookup.domain.repository.LookupRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class LookupViewModelTest {

    private lateinit var viewModel: LookupViewModel

    @Before
    fun setUp() {
        viewModel = LookupViewModel(
            repository = FakeLookupRepository(),
        )
    }

    @Test
    fun `valid binomial input uses repository result`() {
        viewModel.onEvent(LookupEvent.BinomialTrialsChanged("3"))
        viewModel.onEvent(LookupEvent.BinomialThresholdChanged("1"))
        viewModel.onEvent(LookupEvent.Calculate)

        assertEquals(
            0.5,
            viewModel.uiState.value.calculationResult!!,
            TOLERANCE,
        )
    }

    @Test
    fun `valid poisson input uses repository result`() {
        viewModel.onEvent(
            LookupEvent.DistributionChanged(DistributionType.POISSON),
        )
        viewModel.onEvent(LookupEvent.PoissonAverageRateChanged("1.0"))
        viewModel.onEvent(LookupEvent.PoissonThresholdChanged("1"))
        viewModel.onEvent(LookupEvent.Calculate)

        assertEquals(
            0.75,
            viewModel.uiState.value.calculationResult!!,
            TOLERANCE,
        )
    }

    @Test
    fun `invalid poisson input does not call repository result`() {
        viewModel.onEvent(
            LookupEvent.DistributionChanged(DistributionType.POISSON),
        )
        viewModel.onEvent(LookupEvent.PoissonAverageRateChanged("0"))
        viewModel.onEvent(LookupEvent.PoissonThresholdChanged("1"))
        viewModel.onEvent(LookupEvent.Calculate)

        assertNotNull(viewModel.uiState.value.poissonInput.averageRateError)
        assertNull(viewModel.uiState.value.calculationResult)
    }

    companion object {
        private const val TOLERANCE = 1e-10
    }
}

private class FakeLookupRepository : LookupRepository {

    override fun calculateBinomialCumulative(
        request: BinomialRequest,
    ): Double {
        return 0.5
    }

    override fun calculatePoissonCumulative(
        request: PoissonRequest,
    ): Double {
        return 0.75
    }

    override fun calculateStandardNormalCumulative(
        request: StandardNormalRequest,
    ): Double {
        return 0.5
    }
}
