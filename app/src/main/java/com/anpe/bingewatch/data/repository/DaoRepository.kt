package com.anpe.bingewatch.data.repository

import com.anpe.bingewatch.data.entity.WatchEntity
import kotlinx.coroutines.flow.Flow

interface DaoRepository {
    fun upsertWatch(vararg entity: WatchEntity)

    fun deleteAllWatch()

    fun deleteWatch(id: Long)

    fun getAllWatchFlow(): Flow<List<WatchEntity>>

    fun findAllWatchByTitleFlow(): Flow<List<WatchEntity>>

    fun findAllWatchByCreateTimeFlow(): Flow<List<WatchEntity>>

    fun findAllWatchByChangeTimeFlow(): Flow<List<WatchEntity>>

    suspend fun getAllWatch(): List<WatchEntity>

    suspend fun findWatch(id: Long): WatchEntity

    suspend fun findWatch(patten: String): List<WatchEntity>
}