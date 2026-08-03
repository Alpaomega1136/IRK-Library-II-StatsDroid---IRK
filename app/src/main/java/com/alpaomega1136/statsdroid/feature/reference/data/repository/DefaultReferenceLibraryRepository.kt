package com.alpaomega1136.statsdroid.feature.reference.data.repository

import com.alpaomega1136.statsdroid.di.DefaultDispatcher
import com.alpaomega1136.statsdroid.feature.reference.data.local.ReferenceCatalogLocalDataSource
import com.alpaomega1136.statsdroid.feature.reference.data.local.ReferenceDocumentLocalDataSource
import com.alpaomega1136.statsdroid.feature.reference.data.remote.RinaldiMaterialRemoteDataSource
import com.alpaomega1136.statsdroid.feature.reference.domain.model.ReferenceLibrary
import com.alpaomega1136.statsdroid.feature.reference.domain.model.ReferenceMaterial
import com.alpaomega1136.statsdroid.feature.reference.domain.model.ReferencePdfPage
import com.alpaomega1136.statsdroid.feature.reference.domain.repository.ReferenceLibraryRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultReferenceLibraryRepository @Inject constructor(
    private val catalogLocalDataSource: ReferenceCatalogLocalDataSource,
    private val documentLocalDataSource: ReferenceDocumentLocalDataSource,
    private val remoteDataSource: RinaldiMaterialRemoteDataSource,
    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) : ReferenceLibraryRepository {

    override suspend fun loadLibrary(
        refreshRemote: Boolean,
    ): ReferenceLibrary {
        return withContext(defaultDispatcher) {
            val bundledMaterials = catalogLocalDataSource
                .loadBundledMaterials()

            val remoteResult = runCatching {
                remoteDataSource.loadLatestMaterials(
                    forceRefresh = refreshRemote,
                )
            }

            ReferenceLibrary(
                bundledMaterials = bundledMaterials,
                scrapedMaterials = remoteResult
                    .getOrNull()
                    ?.materials
                    .orEmpty(),
                latestAcademicYear = remoteResult
                    .getOrNull()
                    ?.latestAcademicYear,
                warningMessage = remoteResult
                    .exceptionOrNull()
                    ?.let {
                        "Online materials could not be refreshed. Bundled materials are still available."
                    },
            )
        }
    }

    override suspend fun renderPdfPage(
        material: ReferenceMaterial,
        pageIndex: Int,
    ): ReferencePdfPage {
        return withContext(defaultDispatcher) {
            documentLocalDataSource.renderPdfPage(
                material = material,
                pageIndex = pageIndex,
            )
        }
    }
}
