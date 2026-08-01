package com.alpaomega1136.statsdroid.feature.lookup.data.local

import com.alpaomega1136.statsdroid.core.statistics.distribution.BinomialCalculator
import com.alpaomega1136.statsdroid.core.statistics.distribution.NormalDistributionCalculator
import com.alpaomega1136.statsdroid.core.statistics.distribution.PoissonCalculator
import com.alpaomega1136.statsdroid.core.statistics.model.NormalCurvePoint
import com.alpaomega1136.statsdroid.feature.lookup.domain.model.BinomialRequest
import com.alpaomega1136.statsdroid.feature.lookup.domain.model.PoissonRequest
import com.alpaomega1136.statsdroid.feature.lookup.domain.model.StandardNormalRequest
import javax.inject.Inject
import kotlin.math.roundToInt

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

    override fun calculateStandardNormalDensity(
        request: StandardNormalRequest,
    ): Double {
        return normalCalculator.probabilityDensity(z = request.zScore)
    }

    override fun generateStandardNormalCurve(
        minZ: Double,
        maxZ: Double,
        step: Double,
    ): List<NormalCurvePoint> {
        require(minZ.isFinite() && maxZ.isFinite()) {
            "Curve boundaries must be finite."
        }
        require(minZ < maxZ) {
            "Minimum Z-score must be smaller than maximum Z-score."
        }
        require(step.isFinite() && step > 0.0) {
            "Curve step must be greater than zero."
        }

        val numberOfSteps = ((maxZ - minZ) / step).roundToInt()

        return buildList(capacity = numberOfSteps + 1) {
            for (index in 0..numberOfSteps) {
                val zScore = if (index == numberOfSteps) maxZ else minZ + index * step
                add(
                    NormalCurvePoint(
                        zScore = zScore,
                        density = normalCalculator.probabilityDensity(z = zScore),
                    ),
                )
            }
        }
    }
}
