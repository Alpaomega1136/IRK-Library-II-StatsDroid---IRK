package com.alpaomega1136.statsdroid.feature.reference.data.remote

interface RinaldiMaterialRemoteDataSource {
    fun loadLatestMaterials(
        forceRefresh: Boolean,
    ): RinaldiScrapeResult
}
