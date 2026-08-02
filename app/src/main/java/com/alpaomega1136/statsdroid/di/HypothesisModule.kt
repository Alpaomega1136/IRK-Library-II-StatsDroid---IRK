package com.alpaomega1136.statsdroid.di

import com.alpaomega1136.statsdroid.feature.hypothesis.data.local.DefaultHypothesisLocalDataSource
import com.alpaomega1136.statsdroid.feature.hypothesis.data.local.HypothesisLocalDataSource
import com.alpaomega1136.statsdroid.feature.hypothesis.data.repository.DefaultHypothesisRepository
import com.alpaomega1136.statsdroid.feature.hypothesis.domain.repository.HypothesisRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HypothesisModule {

    @Binds
    @Singleton
    abstract fun bindHypothesisLocalDataSource(implementation: DefaultHypothesisLocalDataSource): HypothesisLocalDataSource

    @Binds
    @Singleton
    abstract fun bindHypothesisRepository(implementation: DefaultHypothesisRepository): HypothesisRepository
}
