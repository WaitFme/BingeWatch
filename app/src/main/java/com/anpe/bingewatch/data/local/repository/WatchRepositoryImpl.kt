package com.anpe.bingewatch.data.local.repository

import com.anpe.bingewatch.data.local.entity.WatchEntity
import com.anpe.bingewatch.data.local.database.WatchDao
import com.anpe.bingewatch.data.local.entity.WatchNewEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class WatchRepositoryImpl @Inject constructor(private val dao: WatchDao): WatchRepository {
    override fun insertWatch(vararg entity: WatchNewEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.insertWatch(*entity)
        }
    }

    override fun updateWatch(vararg entity: WatchNewEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.updateWatch(*entity)
        }
    }

    override fun deleteWatch(vararg entity: WatchNewEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.deleteWatch(*entity)
        }
    }

    override fun deleteAllWatch() {
        CoroutineScope(Dispatchers.IO).launch {
            dao.deleteAllWatch()
        }
    }

    override fun getAllWatch() = dao.getAllWatch()

    override fun findWatch(pattenState: Int) = dao.findWatchFlow(pattenState)

    override fun findWatchFlow(pattenState: Int, pattenTitle: String) = dao.findWatchFlow(pattenState, pattenTitle)

    override fun findWatchTitleIsAlive(patten: String) = dao.findWatchTitleFlow(patten)
}