package com.alpaomega1136.statsdroid.di

import com.alpaomega1136.statsdroid.feature.lookup.data.local.DefaultLookupLocalDataSource
import com.alpaomega1136.statsdroid.feature.lookup.data.local.LookupLocalDataSource
import com.alpaomega1136.statsdroid.feature.lookup.data.repository.DefaultLookupRepository
import com.alpaomega1136.statsdroid.feature.lookup.domain.repository.LookupRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LookupModule {

    @Binds
    @Singleton
    abstract fun bindLookupLocalDataSource(
        implementation: DefaultLookupLocalDataSource,
    ): LookupLocalDataSource

    @Binds
    @Singleton
    abstract fun bindLookupRepository(
        implementation: DefaultLookupRepository,
    ): LookupRepository
}
