package com.anpe.bingewatch.data.repository

import com.anpe.bingewatch.data.database.WatchDao
import com.anpe.bingewatch.data.entity.WatchEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class RepositoryImpl @Inject constructor(private val dao: WatchDao): WatchRepository {
    override fun insertWatch(vararg entity: WatchEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.insertWatch(*entity)
        }
    }

    override fun updateWatch(vararg entity: WatchEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.updateWatch(*entity)
        }
    }

    override fun deleteWatch(vararg entity: WatchEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.deleteWatch(*entity)
        }
    }

    override fun deleteWatch(id: Long) {

    }

    override fun deleteAllWatch() {
        CoroutineScope(Dispatchers.IO).launch {
            dao.deleteAllWatch()
        }
    }

    override fun getAllWatch() = dao.getAllWatch()

//    override fun findWatch(pattenState: Int) = dao.findWatchFlow(pattenState)

    override suspend fun findWatch(id: Long): WatchEntity = dao.findWatch(id)

//    override fun findWatchFlow(pattenState: Int, pattenTitle: String) = dao.findWatchFlow(pattenState, pattenTitle)

    override fun findWatchTitleIsAlive(patten: String) = dao.findWatchTitleFlow(patten)
}