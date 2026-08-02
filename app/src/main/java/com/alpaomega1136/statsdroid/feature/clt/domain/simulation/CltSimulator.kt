package com.alpaomega1136.statsdroid.feature.clt.domain.simulation

import com.alpaomega1136.statsdroid.core.statistics.distribution.NormalDistributionCalculator
import com.alpaomega1136.statsdroid.core.statistics.model.DensityCurvePoint
import com.alpaomega1136.statsdroid.feature.clt.domain.model.CltSimulationRequest
import com.alpaomega1136.statsdroid.feature.clt.domain.model.CltSimulationResult
import javax.inject.Inject
import kotlin.math.sqrt
import kotlin.random.Random

class CltSimulator @Inject constructor(
    private val samplerFactory: PopulationSamplerFactory,
    private val normalDistributionCalculator: NormalDistributionCalculator,
) {

    fun simulate(
        request: CltSimulationRequest,
        random: Random = Random.Default,
        populationPreviewSize: Int = DEFAULT_POPULATION_PREVIEW_SIZE,
    ): CltSimulationResult {
        validateRequest(request, populationPreviewSize)

        val sampler = samplerFactory.create(request.populationShape)
        val populationPreviewValues = List(populationPreviewSize) { sampler.sample(random) }
        val sampleMeans = generateSampleMeans(
            sampler = sampler,
            sampleSize = request.sampleSize,
            numberOfSamples = request.simulationCount.value,
            random = random,
        )
        val theoreticalMean = sampler.theoreticalMean
        val populationStandardDeviation = sampler.theoreticalStandardDeviation
        val theoreticalStandardError = populationStandardDeviation / sqrt(request.sampleSize.toDouble())
        val empiricalMean = sampleMeans.average()
        val empiricalSamplingStandardDeviation = calculateStandardDeviation(sampleMeans, empiricalMean)

        return CltSimulationResult(
            populationShape = request.populationShape,
            sampleSize = request.sampleSize,
            numberOfSamples = request.simulationCount.value,
            populationPreviewValues = populationPreviewValues,
            sampleMeans = sampleMeans,
            theoreticalMean = theoreticalMean,
            populationStandardDeviation = populationStandardDeviation,
            empiricalMean = empiricalMean,
            empiricalSamplingStandardDeviation = empiricalSamplingStandardDeviation,
            theoreticalStandardError = theoreticalStandardError,
            theoreticalNormalCurve = generateTheoreticalNormalCurve(theoreticalMean, theoreticalStandardError),
        )
    }

    private fun generateSampleMeans(
        sampler: PopulationSampler,
        sampleSize: Int,
        numberOfSamples: Int,
        random: Random,
    ): List<Double> {
        return List(numberOfSamples) {
            var sum = 0.0
            repeat(sampleSize) {
                sum += sampler.sample(random)
            }
            sum / sampleSize.toDouble()
        }
    }

    private fun calculateStandardDeviation(values: List<Double>, mean: Double): Double {
        val variance = values.sumOf {
            val difference = it - mean
            difference * difference
        } / values.size.toDouble()

        return sqrt(variance)
    }

    private fun generateTheoreticalNormalCurve(mean: Double, standardError: Double): List<DensityCurvePoint> {
        val minimumX = mean - NORMAL_CURVE_STANDARD_ERROR_RANGE * standardError
        val maximumX = mean + NORMAL_CURVE_STANDARD_ERROR_RANGE * standardError

        return (0..NORMAL_CURVE_SEGMENTS).map { index ->
            val x = minimumX + index.toDouble() / NORMAL_CURVE_SEGMENTS * (maximumX - minimumX)
            val zScore = (x - mean) / standardError
            DensityCurvePoint(x = x, density = normalDistributionCalculator.probabilityDensity(zScore) / standardError)
        }
    }

    private fun validateRequest(request: CltSimulationRequest, populationPreviewSize: Int) {
        require(request.sampleSize in MIN_SAMPLE_SIZE..MAX_SAMPLE_SIZE) {
            "Sample size must be between 1 and 100."
        }
        require(populationPreviewSize >= 1) {
            "Population preview size must be at least 1."
        }
    }

    companion object {
        const val MIN_SAMPLE_SIZE = 1
        const val MAX_SAMPLE_SIZE = 100

        private const val DEFAULT_POPULATION_PREVIEW_SIZE = 10_000
        private const val NORMAL_CURVE_STANDARD_ERROR_RANGE = 4.0
        private const val NORMAL_CURVE_SEGMENTS = 200
    }
}
