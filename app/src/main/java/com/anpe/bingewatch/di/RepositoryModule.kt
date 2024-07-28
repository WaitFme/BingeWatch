package com.anpe.bingewatch.di

import com.anpe.bingewatch.data.repository.WatchRepository
import com.anpe.bingewatch.data.repository.RepositoryImpl
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
    abstract fun bindNetRepository(repositoryImpl: RepositoryImpl): WatchRepository
}