package com.alpaomega1136.statsdroid.feature.reference.data.local

import com.alpaomega1136.statsdroid.feature.reference.domain.model.ReferenceMaterial

interface ReferenceCatalogLocalDataSource {
    fun loadBundledMaterials(): List<ReferenceMaterial>
}
