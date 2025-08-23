package com.anpe.bingewatch.ui.host.screen.home

import android.os.VibrationEffect
import android.os.Vibrator
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
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val daoRepo: DaoRepository,
    private val netRepo: NetRepository,
    private val dataStore: DataStoreManager,
    private val vibrator: Vibrator
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
                    is HomeAction.IncreaseEpi -> modifyEpisode(it.id, 1)
                    is HomeAction.DecreaseEpi -> modifyEpisode(it.id, -1)
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

            val flow = when (sortType) {
                0 -> daoRepo.findAllWatchByTitleFlow()
                1 -> daoRepo.findAllWatchByCreateTimeFlow()
                else -> daoRepo.findAllWatchByChangeTimeFlow()
            }

            flow.collect { watch ->
                _viewState.update { it.copy(data = watch) }
            }
        }
    }

    private fun changeTabIndex(index: Int) {
        _viewState.update { it.copy(selectTab = index) }
    }

    private fun modifyEpisode(id: Long, delta: Int) {
        viewModelScope.launch {
            try {
                val watch = daoRepo.findWatch(id)
                val newEpisode = (watch.currentEpisode + delta).coerceIn(0, watch.totalEpisode)

                if (newEpisode != watch.currentEpisode) {
                    val updatedWatch = watch.copy(
                        currentEpisode = newEpisode,
                        watchState = when {
                            newEpisode <= 0 -> 1          // 未开始
                            newEpisode >= watch.totalEpisode -> 2  // 已完成
                            else -> 0                     // 观看中
                        }
                    )
                    daoRepo.upsertWatch(updatedWatch)
                }
            } catch (e: Exception) {
                Log.e(TAG, "modifyEpisode: ${e.printStackTrace()}")
            }
        }
    }

    private fun showDialog(id: Long) {
        viewModelScope.launch {
            val watch = daoRepo.findWatch(id)

            _viewState.update {
                it.copy(
                    id = watch.id,
                    title = watch.title,
                    currentEpi = TextFieldValue(text = watch.currentEpisode.toString()),
                    totalEpi = TextFieldValue(text = watch.totalEpisode.toString())
                )
            }
            _viewEvent.emit(HomeEvent.ShowDialog)
        }
    }

    private fun dismissDialog() {
        viewModelScope.launch {
            _viewEvent.emit(HomeEvent.CloseDialog)

            _viewState.update {
                it.copy(
                    id = -1,
                    title = "",
                    currentEpi = TextFieldValue(),
                    totalEpi = TextFieldValue()
                )
            }
        }
    }

    private fun changeCurrentEpi(cEpi: TextFieldValue) {
//
//        if (validatedEpi.text.isNotEmpty() && validatedEpi.text.toInt() > homeState.value.totalEpi.text.toInt()) {
//            _viewState.update { it.copy(currentEpi = homeState.value.totalEpi) }
//        } else {
//            _viewState.update { it.copy(currentEpi = cEpi) }
//        }
        _viewState.update { it.copy(currentEpi = cEpi) }
    }

    private fun changeTotalEpi(tEpi: TextFieldValue) {
//        if (tEpi.text.isNotEmpty()) {
//            if (tEpi.text.toInt() < homeState.value.currentEpi.text.toInt()) {
//                _viewState.update { it.copy(currentEpi = tEpi) }
//            }
//        }
//        _viewState.update { it.copy(totalEpi = tEpi) }

        _viewState.update { it.copy(currentEpi = tEpi) }
    }

    private fun updateData(id: Long) {
        val waitWatch = viewModelScope.async {
            daoRepo.findWatch(id)
        }

        try {
            if (homeState.value.currentEpi.text.isEmpty() || homeState.value.totalEpi.text.isEmpty()) {
                _viewState.update { it.copy(errorMessage = "Input cannot be empty") }
                return
            }
            val nCEpi = homeState.value.currentEpi.text.toInt()
            val nTEpi = homeState.value.totalEpi.text.toInt()

            viewModelScope.launch {
                daoRepo.upsertWatch(
                    waitWatch.await().copy(
                        currentEpisode = nCEpi,
                        totalEpisode = nTEpi,
                        watchState = getWatchState(nCEpi, nTEpi)
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "updateData: ${e.printStackTrace()}")
        }
        dismissDialog()
    }

    private fun deleteData(id: Long) {
        viewModelScope.launch {
            daoRepo.deleteWatch(id)
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

    fun vibrate(duration: Long) {
        vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}