package com.alpaomega1136.statsdroid.di

import com.alpaomega1136.statsdroid.feature.clt.data.local.CltLocalDataSource
import com.alpaomega1136.statsdroid.feature.clt.data.local.DefaultCltLocalDataSource
import com.alpaomega1136.statsdroid.feature.clt.data.repository.DefaultCltRepository
import com.alpaomega1136.statsdroid.feature.clt.domain.repository.CltRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CltModule {

    @Binds
    @Singleton
    abstract fun bindCltLocalDataSource(implementation: DefaultCltLocalDataSource): CltLocalDataSource

    @Binds
    @Singleton
    abstract fun bindCltRepository(implementation: DefaultCltRepository): CltRepository
}
