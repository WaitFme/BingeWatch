package com.anpe.bingewatch.core.repository

import com.anpe.bingewatch.core.data.database.WatchDao
import com.anpe.bingewatch.core.data.entity.WatchEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DaoRepositoryImpl @Inject constructor(private val dao: WatchDao): DaoRepository {
    override fun upsertWatch(vararg entity: WatchEntity) {
        val entities = entity.map {
            it.copy(changeTime = System.currentTimeMillis())
        }
        CoroutineScope(Dispatchers.IO).launch {
            dao.upsertWatch(*entities.toTypedArray())
        }
    }

    override fun deleteAllWatch() {
        CoroutineScope(Dispatchers.IO).launch {
            dao.deleteAllWatch()
        }
    }

    override fun deleteWatch(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val entity = dao.findWatch(id).copy(
                isDelete = true,
                changeTime = System.currentTimeMillis()
            )
            dao.upsertWatch(entity)
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