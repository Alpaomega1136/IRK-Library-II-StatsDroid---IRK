package com.alpaomega1136.statsdroid.feature.lookup.data.local

import com.alpaomega1136.statsdroid.core.statistics.distribution.BinomialCalculator
import com.alpaomega1136.statsdroid.core.statistics.distribution.NormalDistributionCalculator
import com.alpaomega1136.statsdroid.core.statistics.distribution.PoissonCalculator
import com.alpaomega1136.statsdroid.feature.lookup.domain.model.BinomialRequest
import com.alpaomega1136.statsdroid.feature.lookup.domain.model.PoissonRequest
import com.alpaomega1136.statsdroid.feature.lookup.domain.model.StandardNormalRequest
import javax.inject.Inject

class DefaultLookupLocalDataSource @Inject constructor(
    private val binomialCalculator: BinomialCalculator,
    private val poissonCalculator: PoissonCalculator,
    private val normalCalculator: NormalDistributionCalculator,
) : LookupLocalDataSource {

    override fun calculateBinomialCumulative(
        request: BinomialRequest,
    ): Double {
        return binomialCalculator.cumulativeProbability(
            numberOfTrials = request.numberOfTrials,
            threshold = request.threshold,
            successProbability = request.successProbability,
        )
    }

    override fun calculatePoissonCumulative(
        request: PoissonRequest,
    ): Double {
        return poissonCalculator.cumulativeProbability(
            averageRate = request.averageRate,
            threshold = request.threshold,
        )
    }

    override fun calculateStandardNormalCumulative(
        request: StandardNormalRequest,
    ): Double {
        return normalCalculator.cumulativeProbability(
            z = request.zScore,
        )
    }
}
