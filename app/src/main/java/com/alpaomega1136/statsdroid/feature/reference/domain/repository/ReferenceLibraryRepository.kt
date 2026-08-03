package com.alpaomega1136.statsdroid.feature.reference.domain.repository

import com.alpaomega1136.statsdroid.feature.reference.domain.model.ReferenceLibrary
import com.alpaomega1136.statsdroid.feature.reference.domain.model.ReferenceMaterial
import com.alpaomega1136.statsdroid.feature.reference.domain.model.ReferencePdfPage

interface ReferenceLibraryRepository {
    suspend fun loadLibrary(
        refreshRemote: Boolean,
    ): ReferenceLibrary

    suspend fun renderPdfPage(
        material: ReferenceMaterial,
        pageIndex: Int,
    ): ReferencePdfPage
}
