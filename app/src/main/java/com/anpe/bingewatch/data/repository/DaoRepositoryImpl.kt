package com.anpe.bingewatch.data.repository

import com.anpe.bingewatch.data.database.WatchDao
import com.anpe.bingewatch.data.entity.WatchEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DaoRepositoryImpl @Inject constructor(private val dao: WatchDao): DaoRepository {
    override fun upsertWatch(vararg entity: WatchEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.upsertWatch(*entity)
        }
    }

    override fun deleteAllWatch() {
        CoroutineScope(Dispatchers.IO).launch {
            dao.deleteAllWatch()
        }
    }

    override fun getAllWatchFlow() = dao.getAllWatchFlow()

    override fun findAllWatchByTitleFlow(): Flow<List<WatchEntity>> = dao.findAllWatchByTitleFlow()

    override fun findAllWatchByCreateTimeFlow(): Flow<List<WatchEntity>> = dao.findAllWatchByCreateTimeFlow()

    override fun findAllWatchByChangeTimeFlow(): Flow<List<WatchEntity>> = dao.findAllWatchByChangeTimeFlow()

    override suspend fun getAllWatch(): List<WatchEntity> = dao.getAllWatch()

    override suspend fun findWatch(id: Long): WatchEntity = dao.findWatch(id)

    override suspend fun findWatch(patten: String) = dao.findWatch(patten)
}