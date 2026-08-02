package com.alpaomega1136.statsdroid.core.statistics.histogram

import com.alpaomega1136.statsdroid.core.statistics.model.HistogramBin
import com.alpaomega1136.statsdroid.core.statistics.model.HistogramData
import com.alpaomega1136.statsdroid.core.statistics.model.HistogramRange
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

class HistogramCalculator @Inject constructor() {

    fun calculate(
        values: List<Double>,
        binCount: Int,
        range: HistogramRange? = null,
    ): HistogramData {
        require(values.isNotEmpty()) {
            "Histogram values must not be empty."
        }
        require(values.all(Double::isFinite)) {
            "Histogram values must be finite."
        }
        require(binCount in MIN_BIN_COUNT..MAX_BIN_COUNT) {
            "Histogram bin count must be between 1 and 200."
        }

        val resolvedRange = range ?: inferRange(values)
        val binWidth = (resolvedRange.maximum - resolvedRange.minimum) / binCount.toDouble()
        val counts = IntArray(binCount)

        values.forEach { value ->
            val boundedValue = value.coerceIn(resolvedRange.minimum, resolvedRange.maximum)
            val rawIndex = ((boundedValue - resolvedRange.minimum) / binWidth).toInt()
            counts[rawIndex.coerceIn(0, binCount - 1)]++
        }

        val bins = counts.mapIndexed { index, count ->
            val start = resolvedRange.minimum + index * binWidth
            val end = if (index == binCount - 1) resolvedRange.maximum else start + binWidth
            HistogramBin(
                start = start,
                end = end,
                count = count,
                density = count.toDouble() / (values.size.toDouble() * binWidth),
            )
        }

        return HistogramData(
            bins = bins,
            range = resolvedRange,
            binWidth = binWidth,
            totalCount = values.size,
            maximumDensity = bins.maxOf(HistogramBin::density),
        )
    }

    private fun inferRange(values: List<Double>): HistogramRange {
        val minimum = requireNotNull(values.minOrNull())
        val maximum = requireNotNull(values.maxOrNull())

        if (minimum == maximum) {
            val padding = maxOf(abs(minimum) * 0.1, DEFAULT_CONSTANT_PADDING)
            return HistogramRange(minimum = minimum - padding, maximum = maximum + padding)
        }

        val padding = (maximum - minimum) * AUTOMATIC_RANGE_PADDING_FRACTION
        return HistogramRange(minimum = minimum - padding, maximum = maximum + padding)
    }

    companion object {
        private const val MIN_BIN_COUNT = 1
        private const val MAX_BIN_COUNT = 200
        private const val DEFAULT_CONSTANT_PADDING = 0.5
        private const val AUTOMATIC_RANGE_PADDING_FRACTION = 0.05

        fun recommendedBinCount(valueCount: Int): Int {
            require(valueCount >= 1) {
                "Value count must be at least one."
            }
            return sqrt(valueCount.toDouble()).roundToInt().coerceIn(10, 50)
        }
    }
}
