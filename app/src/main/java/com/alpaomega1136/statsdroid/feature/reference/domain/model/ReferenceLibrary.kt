package com.alpaomega1136.statsdroid.feature.reference.domain.model

data class ReferenceLibrary(
    val bundledMaterials: List<ReferenceMaterial>,
    val scrapedMaterials: List<ReferenceMaterial>,
    val latestAcademicYear: String?,
    val warningMessage: String? = null,
)
