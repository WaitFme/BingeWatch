package com.anpe.bingewatch.ui.host.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anpe.bingewatch.data.entity.WatchNewEntity
import com.anpe.bingewatch.data.repository.WatchRepository
import com.anpe.bingewatch.utils.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: WatchRepository): ViewModel() {
    /*private val sp = application.getSharedPreferences(
        "${application.packageName}_settings",
        Context.MODE_PRIVATE
    )*/

    val channel = Channel<MainIntent>(Channel.UNLIMITED)

    private val _watchFlow = MutableStateFlow<List<WatchNewEntity>>(listOf())
    val watchFlow: StateFlow<List<WatchNewEntity>> get() = _watchFlow

    private val _watchTitleIsAlive = MutableStateFlow(false)
    val watchTitleIsAlive: StateFlow<Boolean> get() = _watchTitleIsAlive

    /*private val _sortType = MutableStateFlow(sp.getInt("SORT_TYPE", -1).let {
        when (it) {
            0 -> SortType.TITLE
            1 -> SortType.CREATE_TIME
            else -> SortType.CHANGE_TIME
        }
    })*/
    private val _sortType = MutableStateFlow(SortType.CHANGE_TIME)
    val sortType: StateFlow<SortType> get() = _sortType

    init {
        getAllData()
        channelHandler(channel)
    }

    private fun channelHandler(channel: Channel<MainIntent>) {
        viewModelScope.launch {
            channel.consumeAsFlow().collect {
                when (it) {
                    MainIntent.DeleteAllData -> repository.deleteAllWatch()
                    is MainIntent.DeleteData -> repository.deleteWatch(it.entity)
                    MainIntent.GetData -> getAllData()
                    is MainIntent.InsertData -> repository.insertWatch(it.entity)
                    is MainIntent.UpdateData -> repository.updateWatch(it.entity)
                    is MainIntent.FindTitleAlive -> findWatchAlive(it.title)
                }
            }
        }
    }

    private fun findWatch(pattenState: Int) {
        viewModelScope.launch {
            repository.findWatch(pattenState).collect {
                _watchFlow.emit(when (sortType.value) {
                    SortType.TITLE -> it.sortedBy { it.title }
                    SortType.CREATE_TIME -> it.sortedBy { it.createTime }
                    SortType.CHANGE_TIME -> it.sortedBy { it.changeTime }.reversed()
                })
            }
        }
    }

    private fun getAllData() {
        viewModelScope.launch {
            repository.getAllWatch().collect {
                _watchFlow.emit(it.sortedBy { it.changeTime })
            }
        }
    }

    private fun getIndex(type: Int) {
        viewModelScope.launch {
            repository.findWatch(type).collect {
                _watchFlow.emit(when (sortType.value) {
                    SortType.TITLE -> it.sortedBy { it.title }
                    SortType.CREATE_TIME -> it.sortedBy { it.createTime }
                    SortType.CHANGE_TIME -> it.sortedBy { it.changeTime }.reversed()
                })
            }
        }
    }

    private fun findWatch(pattenState: Int, pattenString: String) {
        viewModelScope.launch {
            repository.findWatchFlow(pattenState, pattenString).collect {
                _watchFlow.emit(it)
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

    private fun sortType(sortType: SortType) {
        viewModelScope.launch {
            _sortType.emit(sortType)
            val buff = watchFlow
            _watchFlow.emit(when (sortType) {
                SortType.TITLE -> buff.value.sortedBy { it.title }
                SortType.CREATE_TIME -> buff.value.sortedBy { it.createTime }
                SortType.CHANGE_TIME -> buff.value.sortedBy { it.changeTime }
            })
            /*sp.edit().putInt("SORT_TYPE", let {
                when (sortType) {
                    SortType.TITLE -> 0
                    SortType.CREATE_TIME -> 1
                    SortType.CHANGE_TIME -> 2
                }
            }).apply()*/
        }
    }
}