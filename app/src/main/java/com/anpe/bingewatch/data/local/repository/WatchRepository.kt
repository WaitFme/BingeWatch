package com.anpe.bingewatch.data.local.repository

import com.anpe.bingewatch.data.local.entity.WatchNewEntity
import kotlinx.coroutines.flow.Flow

interface WatchRepository {
    fun insertWatch(vararg entity: WatchNewEntity)

    fun updateWatch(vararg entity: WatchNewEntity)

    fun deleteWatch(vararg entity: WatchNewEntity)

    fun deleteAllWatch()

    fun getAllWatch(): Flow<List<WatchNewEntity>>

    fun findWatch(pattenState: Int): Flow<List<WatchNewEntity>>

    fun findWatchFlow(pattenState: Int, pattenTitle: String): Flow<List<WatchNewEntity>>

    fun findWatchTitleIsAlive(patten: String): Flow<List<WatchNewEntity>>
}