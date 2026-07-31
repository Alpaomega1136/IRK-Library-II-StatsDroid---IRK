package com.alpaomega1136.statsdroid.feature.lookup.data.repository

import com.alpaomega1136.statsdroid.feature.lookup.data.local.LookupLocalDataSource
import com.alpaomega1136.statsdroid.feature.lookup.domain.model.BinomialRequest
import com.alpaomega1136.statsdroid.feature.lookup.domain.model.PoissonRequest
import com.alpaomega1136.statsdroid.feature.lookup.domain.model.StandardNormalRequest
import com.alpaomega1136.statsdroid.feature.lookup.domain.repository.LookupRepository
import javax.inject.Inject

class DefaultLookupRepository @Inject constructor(
    private val localDataSource: LookupLocalDataSource,
) : LookupRepository {

    override fun calculateBinomialCumulative(
        request: BinomialRequest,
    ): Double {
        return localDataSource.calculateBinomialCumulative(request)
    }

    override fun calculatePoissonCumulative(
        request: PoissonRequest,
    ): Double {
        return localDataSource.calculatePoissonCumulative(request)
    }

    override fun calculateStandardNormalCumulative(
        request: StandardNormalRequest,
    ): Double {
        return localDataSource.calculateStandardNormalCumulative(request)
    }
}
