package com.alpaomega1136.statsdroid.feature.reference.presentation

import android.graphics.Bitmap
import com.alpaomega1136.statsdroid.feature.reference.domain.model.ReferenceMaterial

data class ReferenceUiState(
    val isLoadingLibrary: Boolean = true,
    val isRefreshingLibrary: Boolean = false,
    val bundledMaterials: List<ReferenceMaterial> = emptyList(),
    val scrapedMaterials: List<ReferenceMaterial> = emptyList(),
    val latestAcademicYear: String? = null,
    val libraryWarning: String? = null,
    val query: String = "",
    val selectedMaterial: ReferenceMaterial? = null,
    val isLoadingPage: Boolean = false,
    val bitmap: Bitmap? = null,
    val pageIndex: Int = 0,
    val pageCount: Int = 0,
    val readerError: String? = null,
) {
    val filteredBundledMaterials: List<ReferenceMaterial>
        get() = bundledMaterials.filterByQuery(query)

    val filteredScrapedMaterials: List<ReferenceMaterial>
        get() = scrapedMaterials.filterByQuery(query)

    val canGoPrevious: Boolean
        get() = !isLoadingPage && pageIndex > 0

    val canGoNext: Boolean
        get() = !isLoadingPage &&
            pageCount > 0 &&
            pageIndex < pageCount - 1

    val isReaderOpen: Boolean
        get() = selectedMaterial != null
}

private fun List<ReferenceMaterial>.filterByQuery(
    query: String,
): List<ReferenceMaterial> {
    val normalizedQuery = query.trim()
    if (normalizedQuery.isEmpty()) {
        return this
    }

    return filter { material ->
        material.title.contains(normalizedQuery, ignoreCase = true) ||
            material.description.contains(normalizedQuery, ignoreCase = true) ||
            material.sourceName.contains(normalizedQuery, ignoreCase = true) ||
            material.academicYear.orEmpty()
                .contains(normalizedQuery, ignoreCase = true)
    }
}
