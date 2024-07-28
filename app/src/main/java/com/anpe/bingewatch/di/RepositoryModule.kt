package com.anpe.bingewatch.di

import com.anpe.bingewatch.data.local.repository.WatchRepository
import com.anpe.bingewatch.data.local.repository.WatchRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindNetRepository(watchRepositoryImpl: WatchRepositoryImpl): WatchRepository
}