package com.anpe.bingewatch.data.repository

import com.anpe.bingewatch.data.entity.WatchEntity
import kotlinx.coroutines.flow.Flow

interface WatchRepository {
    fun insertWatch(vararg entity: WatchEntity)

    fun updateWatch(vararg entity: WatchEntity)

    fun deleteWatch(vararg entity: WatchEntity)

    fun deleteAllWatch()

    fun deleteWatch(id: Long)

    fun getAllWatch(): Flow<List<WatchEntity>>

//    fun findWatch(pattenState: Int): Flow<List<WatchEntity>>

    suspend fun findWatch(id: Long): WatchEntity

//    fun findWatchFlow(pattenState: Int, pattenTitle: String): Flow<List<WatchEntity>>

    fun findWatchTitleIsAlive(patten: String): Flow<List<WatchEntity>>
}