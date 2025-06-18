package com.anpe.bingewatch.di

import com.anpe.bingewatch.data.repository.DaoRepository
import com.anpe.bingewatch.data.repository.DaoRepositoryImpl
import com.anpe.bingewatch.data.repository.NetRepository
import com.anpe.bingewatch.data.repository.NetRepositoryImpl
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
    abstract fun bindDaoRepository(repositoryImpl: DaoRepositoryImpl): DaoRepository

    @Binds
    @Singleton
    abstract fun bindNetRepository(netRepositoryImpl: NetRepositoryImpl): NetRepository
}