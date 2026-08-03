package com.alpaomega1136.statsdroid.feature.reference.data.local

import com.alpaomega1136.statsdroid.feature.reference.domain.model.ReferenceMaterial
import com.alpaomega1136.statsdroid.feature.reference.domain.model.ReferencePdfPage

interface ReferenceDocumentLocalDataSource {
    fun renderPdfPage(
        material: ReferenceMaterial,
        pageIndex: Int,
    ): ReferencePdfPage
}
