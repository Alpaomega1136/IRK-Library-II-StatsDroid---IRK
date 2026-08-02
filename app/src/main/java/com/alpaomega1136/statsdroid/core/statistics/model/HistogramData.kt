package com.alpaomega1136.statsdroid.core.statistics.model

data class HistogramRange(
    val minimum: Double,
    val maximum: Double,
) {
    init {
        require(minimum.isFinite() && maximum.isFinite()) {
            "Histogram boundaries must be finite."
        }
        require(minimum < maximum) {
            "Histogram minimum must be smaller than maximum."
        }
    }
}

data class HistogramBin(
    val start: Double,
    val end: Double,
    val count: Int,
    val density: Double,
) {
    val midpoint: Double get() = (start + end) / 2.0
}

data class HistogramData(
    val bins: List<HistogramBin>,
    val range: HistogramRange,
    val binWidth: Double,
    val totalCount: Int,
    val maximumDensity: Double,
)
