package com.alpaomega1136.statsdroid.feature.clt.data.local

import com.alpaomega1136.statsdroid.core.statistics.histogram.HistogramCalculator
import com.alpaomega1136.statsdroid.core.statistics.model.HistogramRange
import com.alpaomega1136.statsdroid.feature.clt.domain.model.CltSimulationRequest
import com.alpaomega1136.statsdroid.feature.clt.domain.model.CltSimulationResult
import com.alpaomega1136.statsdroid.feature.clt.domain.model.CltVisualizationData
import com.alpaomega1136.statsdroid.feature.clt.domain.simulation.CltSimulator
import javax.inject.Inject

class DefaultCltLocalDataSource @Inject constructor(
    private val simulator: CltSimulator,
    private val histogramCalculator: HistogramCalculator,
) : CltLocalDataSource {

    override fun runSimulation(request: CltSimulationRequest): CltVisualizationData {
        val simulation = simulator.simulate(request)
        val populationHistogram = histogramCalculator.calculate(
            values = simulation.populationPreviewValues,
            binCount = HistogramCalculator.recommendedBinCount(simulation.populationPreviewValues.size),
            range = HistogramRange(
                minimum = simulation.populationDisplayMinimum,
                maximum = simulation.populationDisplayMaximum,
            ),
        )
        val samplingHistogram = histogramCalculator.calculate(
            values = simulation.sampleMeans,
            binCount = HistogramCalculator.recommendedBinCount(simulation.sampleMeans.size),
            range = createSamplingDistributionRange(simulation),
        )

        return CltVisualizationData(
            simulation = simulation,
            populationHistogram = populationHistogram,
            samplingDistributionHistogram = samplingHistogram,
        )
    }

    private fun createSamplingDistributionRange(simulation: CltSimulationResult): HistogramRange {
        val curveMinimum = simulation.theoreticalNormalCurve.first().x
        val curveMaximum = simulation.theoreticalNormalCurve.last().x
        val empiricalMinimum = requireNotNull(simulation.sampleMeans.minOrNull())
        val empiricalMaximum = requireNotNull(simulation.sampleMeans.maxOrNull())
        val minimum = minOf(curveMinimum, empiricalMinimum)
        val maximum = maxOf(curveMaximum, empiricalMaximum)

        return if (minimum < maximum) {
            HistogramRange(minimum = minimum, maximum = maximum)
        } else {
            HistogramRange(minimum = minimum - 0.5, maximum = maximum + 0.5)
        }
    }
}
