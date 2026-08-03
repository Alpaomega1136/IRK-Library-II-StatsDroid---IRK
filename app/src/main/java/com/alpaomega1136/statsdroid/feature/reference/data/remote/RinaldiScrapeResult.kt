package com.alpaomega1136.statsdroid.feature.reference.data.remote

import com.alpaomega1136.statsdroid.feature.reference.domain.model.ReferenceMaterial

data class RinaldiScrapeResult(
    val materials: List<ReferenceMaterial>,
    val latestAcademicYear: String?,
)
