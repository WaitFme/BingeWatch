package com.anpe.bingewatch.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class MainViewModel(application: Application) : AndroidViewModel(application) {
    /*private val repository: WatchRepository = WatchRepository(application)

    private val sp = application.getSharedPreferences(
        "${application.packageName}_settings",
        Context.MODE_PRIVATE
    )

    val channel = Channel<MainEvent>(Channel.UNLIMITED)

    private val _viewState = MutableStateFlow(
        ViewState(
            visible = false,
            onShowRequest = ::onShowRequest,
            onDismissRequest = ::onDismissRequest
        )
    )
    val viewState: StateFlow<ViewState> = _viewState

    private val _watchFlow = MutableStateFlow<List<WatchEntity>>(listOf())
    val watchFlow: StateFlow<List<WatchEntity>> get() = _watchFlow

    private val _watchTitleIsAlive = MutableStateFlow(false)
    val watchTitleIsAlive: StateFlow<Boolean> get() = _watchTitleIsAlive

    private val _labelType = MutableStateFlow(sp.getBoolean("ALWAYS_SHOW_LABEL", false))
    val labelType: StateFlow<Boolean> get() = _labelType

    private val _sortType = MutableStateFlow(sp.getInt("SORT_TYPE", -1).let {
        when (it) {
            0 -> SortType.TITLE
            1 -> SortType.CREATE_TIME
            else -> SortType.CHANGE_TIME
        }
    })
    val sortType: StateFlow<SortType> get() = _sortType

    init {
        getIndex(0)
        channelHandler(channel)
    }

    private fun channelHandler(channel: Channel<MainEvent>) {
        viewModelScope.launch {
            channel.consumeAsFlow().collect {
                when (it) {
                    is MainEvent.GetIndex -> getIndex(it.type)
                }
            }
        }
    }

    private fun onShowRequest() {
        viewModelScope.launch {
            _viewState.emit(value = _viewState.value.copy(visible = true))
        }
    }

    private fun onDismissRequest() {
        viewModelScope.launch {
            _viewState.emit(value = _viewState.value.copy(visible = false))
        }
    }

    fun insertWatch(vararg entity: WatchEntity) = repository.insertWatch(*entity)

    fun updateWatch(vararg entity: WatchEntity) = repository.updateWatch(*entity)

    fun deleteWatch(vararg entity: WatchEntity) = repository.deleteWatch(*entity)

    fun deleteAllWatch() = repository.deleteAllWatch()

    suspend fun getAllWatch() = repository.getAllWatch()

    fun findWatch(pattenState: Int) {
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

    fun findWatch(pattenState: Int, pattenString: String) {
        viewModelScope.launch {
            repository.findWatchFlow(pattenState, pattenString).collect {
                _watchFlow.emit(it)
            }
        }
    }

    fun findWatchAlive(title: String) {
        viewModelScope.launch {
            repository.findWatchTitleIsAlive(title).collect {
                _watchTitleIsAlive.emit(it.isNotEmpty())
            }
        }
    }

    fun setLabelType(type: Boolean) {
        viewModelScope.launch {
            _labelType.emit(type)
            sp.edit().putBoolean("ALWAYS_SHOW_LABEL", type).apply()
        }
    }

    fun sortType(sortType: SortType) {
        viewModelScope.launch {
            _sortType.emit(sortType)
            val buff = watchFlow
            _watchFlow.emit(when (sortType) {
                SortType.TITLE -> buff.value.sortedBy { it.title }
                SortType.CREATE_TIME -> buff.value.sortedBy { it.createTime }
                SortType.CHANGE_TIME -> buff.value.sortedBy { it.changeTime }
            })
            sp.edit().putInt("SORT_TYPE", let {
                when (sortType) {
                    SortType.TITLE -> 0
                    SortType.CREATE_TIME -> 1
                    SortType.CHANGE_TIME -> 2
                }
            }).apply()
        }
    }*/
}