package com.anpe.bingewatch.ui.host.screen.home

import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anpe.bingewatch.constant.Constants
import com.anpe.bingewatch.data.dto.request.Request
import com.anpe.bingewatch.data.mapper.WatchMapper.Companion.toDto
import com.anpe.bingewatch.data.mapper.WatchMapper.Companion.toEntity
import com.anpe.bingewatch.data.repository.DaoRepository
import com.anpe.bingewatch.data.repository.NetRepository
import com.anpe.bingewatch.utils.DataStoreManager
import com.anpe.bingewatch.utils.Tools.Companion.getWatchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val daoRepo: DaoRepository,
    private val netRepo: NetRepository,
    private val dataStore: DataStoreManager
) : ViewModel() {
    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val _viewAction = Channel<HomeAction>(Channel.BUFFERED)
    val viewAction = _viewAction.consumeAsFlow()

    private val _viewEvent: MutableSharedFlow<HomeEvent> = MutableSharedFlow()
    val viewEvent = _viewEvent.asSharedFlow()

    private val _viewState: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
    val homeState = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            viewAction.collect {
                when (it) {
                    is HomeAction.RefreshData -> refreshData()
                    is HomeAction.ChangeTabIndex -> changeTabIndex(it.index)
                    is HomeAction.IncreaseEpi -> increaseEpi(it.id)
                    is HomeAction.DecreaseEpi -> decreaseEpi(it.id)
                    is HomeAction.ChangeCurrentEpi -> changeCurrentEpi(it.cEpi)
                    is HomeAction.ChangeTotalEpi -> changeTotalEpi(it.tEpi)
                    is HomeAction.UpdateData -> updateData(it.id)
                    is HomeAction.DeleteData -> deleteData(it.id)
                    is HomeAction.ShowDialog -> showDialog(it.id)
                    is HomeAction.NaviScreen -> _viewEvent.emit(HomeEvent.NaviScreen(it.route))
                    is HomeAction.DismissDialog -> dismissDialog()
                    HomeAction.SyncData -> syncData()
                }
            }
        }
    }

    private fun refreshData() {
        viewModelScope.launch {
            val sortType = dataStore.readIntPreference("sort_type").first()

            if (sortType == 0) {
                daoRepo.findAllWatchByTitleFlow().collect { watch ->
                    _viewState.emit(homeState.value.copy(data = watch))
                }
            } else if (sortType == 1) {
                daoRepo.findAllWatchByCreateTimeFlow().collect { watch ->
                    _viewState.emit(homeState.value.copy(data = watch))
                }
            } else {
                daoRepo.findAllWatchByChangeTimeFlow().collect { watch ->
                    _viewState.emit(homeState.value.copy(data = watch))
                }
            }
        }
    }

    private fun changeTabIndex(index: Int) {
        viewModelScope.launch {
            _viewState.value = _viewState.value.copy(selectTab = index)
        }
    }

    private fun increaseEpi(id: Long) {
        viewModelScope.launch {
            val watch = daoRepo.findWatch(id)
            if (watch.currentEpisode < watch.totalEpisode) {
                val newEpisode = watch.currentEpisode + 1

                daoRepo.upsertWatch(watch.copy(
                    currentEpisode = newEpisode,
                    changeTime = System.currentTimeMillis(),
                    watchState = if (newEpisode >= watch.totalEpisode) 2 else 0
                ))
            }
        }
    }

    private fun decreaseEpi(id: Long) {
        viewModelScope.launch {
            val watch = daoRepo.findWatch(id)
            if (watch.currentEpisode > 0) {
                val newEpisode = watch.currentEpisode - 1

                daoRepo.upsertWatch(watch.copy(
                    currentEpisode = newEpisode,
                    changeTime = System.currentTimeMillis(),
                    watchState = if (newEpisode <= 0) 1 else 0
                ))
            }
        }
    }

    private fun showDialog(id: Long) {
        viewModelScope.launch {
            val watch = daoRepo.findWatch(id)
            _viewState.emit(homeState.value.copy(id = watch.id))
            _viewState.emit(homeState.value.copy(title = watch.title))
            _viewState.emit(homeState.value.copy(currentEpi = TextFieldValue(text = watch.currentEpisode.toString())))
            _viewState.emit(homeState.value.copy(totalEpi = TextFieldValue(text = watch.totalEpisode.toString())))
            _viewEvent.emit(HomeEvent.ShowDialog)
        }
    }

    private fun dismissDialog() {
        viewModelScope.launch {
            _viewEvent.emit(HomeEvent.CloseDialog)
            _viewState.emit(homeState.value.copy(id = -1))
            _viewState.emit(homeState.value.copy(title = ""))
            _viewState.emit(homeState.value.copy(currentEpi = TextFieldValue()))
            _viewState.emit(homeState.value.copy(totalEpi = TextFieldValue()))
        }
    }

    private fun changeCurrentEpi(cEpi: TextFieldValue) {
        viewModelScope.launch {
            if (cEpi.text.isNotEmpty() && cEpi.text.toInt() > homeState.value.totalEpi.text.toInt()) {
                _viewState.emit(_viewState.value.copy(currentEpi = homeState.value.totalEpi))
            } else {
                _viewState.emit(_viewState.value.copy(currentEpi = cEpi))
            }
        }
    }

    private fun changeTotalEpi(tEpi: TextFieldValue) {
        viewModelScope.launch {
            if (tEpi.text.isNotEmpty()) {
                if (tEpi.text.toInt() < homeState.value.currentEpi.text.toInt()) {
                    _viewState.emit(_viewState.value.copy(currentEpi = tEpi))
                }
            }
            _viewState.emit(_viewState.value.copy(totalEpi = tEpi))
        }
    }

    private fun updateData(id: Long) {
        viewModelScope.launch {
            val watch = daoRepo.findWatch(id)
            if (homeState.value.currentEpi.text.isEmpty() || homeState.value.totalEpi.text.isEmpty()) {
                _viewState.emit(homeState.value.copy(errorMessage = "Input cannot be empty"))
                return@launch
            }
            val nCEpi = homeState.value.currentEpi.text.toInt()
            val nTEpi = homeState.value.totalEpi.text.toInt()

            daoRepo.upsertWatch(watch.copy(
                currentEpisode = nCEpi,
                totalEpisode = nTEpi,
                changeTime = System.currentTimeMillis(),
                watchState = getWatchState(nCEpi, nTEpi)
            ))
            dismissDialog()
        }
    }

    private fun deleteData(id: Long) {
        viewModelScope.launch {
            val entity = daoRepo.findWatch(id).copy(isDelete = true, changeTime = System.currentTimeMillis())
            daoRepo.upsertWatch(entity)
            dismissDialog()
        }
    }

    private fun syncData() {
        viewModelScope.launch {
            val url = dataStore.readPreference("url").first()

            if (url.isEmpty()) {
                _viewEvent.emit(HomeEvent.ShowToast("请填写服务器地址"))
                return@launch
            }

            try {
                val allWatch = daoRepo.getAllWatch()
                if (allWatch.isNotEmpty()) {
                    netRepo.upload(
                        url = "${url}/api/upload",
                        request = Request(
                            data = allWatch.map {
                                it.toDto()
                            }
                        )
                    )
                }

                val response = netRepo.sync("${url}${Constants.SYNC_URL}")
                if (response.data.isEmpty()) {
                    return@launch
                }
                response.data.forEach { remoteWatch ->
                    val localWatchs = daoRepo.findWatch(remoteWatch.title)

                    if (localWatchs.isEmpty()) {
                        daoRepo.upsertWatch(remoteWatch.toEntity())
                    } else {
                        localWatchs.forEach { localWatch ->
                            if (remoteWatch.changeTime > localWatch.changeTime) {
                                val entity = remoteWatch.toEntity().copy(id = localWatch.id)
                                daoRepo.upsertWatch(entity)
                            }
                        }
                    }
                }
                _viewEvent.emit(HomeEvent.ShowToast("同步完成"))
            } catch (e: Exception) {
                Log.e(TAG, "sync: ${e.printStackTrace()}")
                _viewEvent.emit(HomeEvent.ShowToast("服务器地址无效"))
            }
        }
    }

    suspend fun dispatch(action: HomeAction) {
        _viewAction.send(action)
    }
}