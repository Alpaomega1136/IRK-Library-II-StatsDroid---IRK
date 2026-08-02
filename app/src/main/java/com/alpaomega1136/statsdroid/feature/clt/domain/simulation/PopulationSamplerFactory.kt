package com.alpaomega1136.statsdroid.feature.clt.domain.simulation

import com.alpaomega1136.statsdroid.feature.clt.domain.model.PopulationShape
import javax.inject.Inject
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random

class PopulationSamplerFactory @Inject constructor() {

    fun create(shape: PopulationShape): PopulationSampler {
        return when (shape) {
            PopulationShape.UNIFORM -> UniformPopulationSampler
            PopulationShape.EXPONENTIAL -> ExponentialPopulationSampler
            PopulationShape.BIMODAL -> BimodalPopulationSampler
        }
    }
}

private object UniformPopulationSampler : PopulationSampler {
    private val boundary = sqrt(3.0)

    override val theoreticalMean: Double = 0.0
    override val theoreticalStandardDeviation: Double = 1.0
    override val recommendedDisplayRange: ClosedFloatingPointRange<Double> = -boundary..boundary

    override fun sample(random: Random): Double = random.nextDouble(from = -boundary, until = boundary)
}

private object ExponentialPopulationSampler : PopulationSampler {
    override val theoreticalMean: Double = 1.0
    override val theoreticalStandardDeviation: Double = 1.0
    override val recommendedDisplayRange: ClosedFloatingPointRange<Double> = 0.0..8.0

    override fun sample(random: Random): Double = -ln(1.0 - random.nextDouble())
}

private object BimodalPopulationSampler : PopulationSampler {
    private const val LEFT_MEAN = -2.0
    private const val RIGHT_MEAN = 2.0
    private const val COMPONENT_STANDARD_DEVIATION = 0.5

    override val theoreticalMean: Double = 0.0
    override val theoreticalStandardDeviation: Double = sqrt(4.25)
    override val recommendedDisplayRange: ClosedFloatingPointRange<Double> = -4.0..4.0

    override fun sample(random: Random): Double {
        val componentMean = if (random.nextBoolean()) LEFT_MEAN else RIGHT_MEAN
        return componentMean + COMPONENT_STANDARD_DEVIATION * nextStandardGaussian(random)
    }
}

private fun nextStandardGaussian(random: Random): Double {
    val firstUniform = 1.0 - random.nextDouble()
    val secondUniform = random.nextDouble()

    return sqrt(-2.0 * ln(firstUniform)) * cos(2.0 * PI * secondUniform)
}
