package com.anpe.bingewatch.ui.host.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anpe.bingewatch.data.entity.WatchEntity
import com.anpe.bingewatch.data.repository.WatchRepository
import com.anpe.bingewatch.utils.SortType
import com.anpe.bingewatch.utils.Tools.Companion.change
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: WatchRepository) : ViewModel() {
    val channel = Channel<MainIntent>(Channel.UNLIMITED)

    private val _watchFlow = MutableStateFlow<List<WatchEntity>>(listOf())
    val watchFlow: StateFlow<List<WatchEntity>> get() = _watchFlow

    private val _watchTitleIsAlive = MutableStateFlow(false)
    val watchTitleIsAlive: StateFlow<Boolean> get() = _watchTitleIsAlive

    private val _currentWatchFlow = MutableStateFlow<WatchEntity?>(null)
    val currentWatchFlow = _currentWatchFlow.asStateFlow()

    private val _sortType = MutableStateFlow(SortType.CHANGE_TIME)
    val sortType: StateFlow<SortType> get() = _sortType

    init {
        getAllWatch()
        channelHandler(channel)
    }

    private fun channelHandler(channel: Channel<MainIntent>) {
        viewModelScope.launch {
            channel.consumeAsFlow().collect {
                when (it) {
                    MainIntent.DeleteAllData -> repository.deleteAllWatch()
                    is MainIntent.DeleteData -> deleteWatch(it.entity)
                    MainIntent.GetData -> getAllWatch()
                    is MainIntent.InsertData -> repository.insertWatch(it.entity)
                    is MainIntent.UpdateData -> repository.updateWatch(it.entity)
                    is MainIntent.FindTitleAlive -> findWatchAlive(it.title)
                    is MainIntent.EpiDecrease -> epiChange(it.id, -1)
                    is MainIntent.EpiIncrease -> epiChange(it.id, 1)
                    is MainIntent.CreateWatch -> createWatch(
                        it.title,
                        it.remarks,
                        it.currentEpi,
                        it.totalEpi
                    )

                    is MainIntent.UpdateWatch -> updateWatch(
                        it.id,
                        it.watchState,
                        it.currentEpi,
                        it.totalEpi
                    )

                    is MainIntent.DeleteWatch -> deleteWatch(it.id)
                    is MainIntent.UpdateCurrentWatch -> updateCurrentWatch(it.id)
                }
            }
        }
    }

    private fun updateCurrentWatch(id: Long) {
        viewModelScope.launch {
            _currentWatchFlow.emit(repository.findWatch(id))
        }
    }

    private fun updateWatch(id: Long, watchState: Int, currentEpi: Int, totalEpi: Int) {
        viewModelScope.launch {
            val watchStateNew = when (currentEpi) {
                0 -> {
                    1
                }

                totalEpi -> {
                    2
                }

                else -> {
                    0
                }
            }

            val entity = repository.findWatch(id)

            val newEntity = entity.change(
                currentEpisode = currentEpi,
                totalEpisode = totalEpi,
                watchState = if (watchState != -1) watchState else watchStateNew,
                changeTime = System.currentTimeMillis(),
            )

            repository.updateWatch(newEntity)
        }
    }

    private fun createWatch(title: String, remarks: String, currentEpi: Int, totalEpi: Int) {
        viewModelScope.launch {
            val time = System.currentTimeMillis()

            val entity = WatchEntity(
                title = title,
                currentEpisode = currentEpi,
                totalEpisode = totalEpi,
                watchState = when (currentEpi) {
                    0 -> {
                        1
                    }

                    totalEpi -> {
                        2
                    }

                    else -> {
                        0
                    }
                },
                createTime = time,
                changeTime = time,
                remarks = remarks,
                isDelete = false
            )

            repository.insertWatch(entity)
        }
    }

    private fun epiChange(id: Long, num: Int) {
        viewModelScope.launch {
            val time = System.currentTimeMillis()

            val entity = repository.findWatch(id)

            val currentEpisode = if (num > 0 && entity.currentEpisode < entity.totalEpisode) {
                entity.currentEpisode + num
            } else if (num < 0 && entity.currentEpisode > 0) {
                entity.currentEpisode + num
            } else {
                entity.currentEpisode
            }

            val watchingState: Int = when (currentEpisode) {
                0 -> 1
                entity.totalEpisode -> 2
                else -> 0
            }

            val newEntity = entity.change(
                currentEpisode = currentEpisode,
                watchState = watchingState,
                changeTime = time
            )

            channel.send(MainIntent.UpdateData(newEntity))
        }
    }

    private fun deleteWatch(id: Long) {
        viewModelScope.launch {
            val entity = repository.findWatch(id)

            repository.updateWatch(entity.change(isDelete = true))
        }
    }

    private fun deleteWatch(entity: WatchEntity) {
        viewModelScope.launch {
            val newEntity = entity.change(isDelete = true)
            repository.updateWatch(newEntity)
        }
    }

    private fun getAllWatch() {
        viewModelScope.launch {
            repository.getAllWatch().collect { watch->
                _watchFlow.emit(watch.sortedBy { it.changeTime }.reversed())
            }
        }
    }

    private fun findWatchAlive(title: String) {
        viewModelScope.launch {
            repository.findWatchTitleIsAlive(title).collect {
                _watchTitleIsAlive.emit(it.isNotEmpty())
            }
        }
    }
}