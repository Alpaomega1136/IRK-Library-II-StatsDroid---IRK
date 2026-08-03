package com.alpaomega1136.statsdroid.di

import com.alpaomega1136.statsdroid.feature.reference.data.local.DefaultReferenceCatalogLocalDataSource
import com.alpaomega1136.statsdroid.feature.reference.data.local.DefaultReferenceDocumentLocalDataSource
import com.alpaomega1136.statsdroid.feature.reference.data.local.ReferenceCatalogLocalDataSource
import com.alpaomega1136.statsdroid.feature.reference.data.local.ReferenceDocumentLocalDataSource
import com.alpaomega1136.statsdroid.feature.reference.data.remote.DefaultRinaldiMaterialRemoteDataSource
import com.alpaomega1136.statsdroid.feature.reference.data.remote.RinaldiMaterialRemoteDataSource
import com.alpaomega1136.statsdroid.feature.reference.data.repository.DefaultReferenceLibraryRepository
import com.alpaomega1136.statsdroid.feature.reference.domain.repository.ReferenceLibraryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReferenceModule {

    @Binds
    @Singleton
    abstract fun bindReferenceCatalogLocalDataSource(
        implementation: DefaultReferenceCatalogLocalDataSource,
    ): ReferenceCatalogLocalDataSource

    @Binds
    @Singleton
    abstract fun bindReferenceDocumentLocalDataSource(
        implementation: DefaultReferenceDocumentLocalDataSource,
    ): ReferenceDocumentLocalDataSource

    @Binds
    @Singleton
    abstract fun bindRinaldiMaterialRemoteDataSource(
        implementation: DefaultRinaldiMaterialRemoteDataSource,
    ): RinaldiMaterialRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindReferenceLibraryRepository(
        implementation: DefaultReferenceLibraryRepository,
    ): ReferenceLibraryRepository
}
