package com.alpaomega1136.statsdroid.feature.lookup.domain.repository

import com.alpaomega1136.statsdroid.feature.lookup.domain.model.BinomialRequest
import com.alpaomega1136.statsdroid.feature.lookup.domain.model.PoissonRequest
import com.alpaomega1136.statsdroid.feature.lookup.domain.model.StandardNormalRequest

interface LookupRepository {

    fun calculateBinomialCumulative(
        request: BinomialRequest,
    ): Double

    fun calculatePoissonCumulative(
        request: PoissonRequest,
    ): Double

    fun calculateStandardNormalCumulative(
        request: StandardNormalRequest,
    ): Double
}
