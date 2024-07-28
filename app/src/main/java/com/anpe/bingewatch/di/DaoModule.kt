package com.anpe.bingewatch.di

import com.anpe.bingewatch.data.local.database.WatchDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
    @Provides
    @Singleton
    fun provideWatchDao(db: WatchDatabase) = db.getWatchDao()
}
