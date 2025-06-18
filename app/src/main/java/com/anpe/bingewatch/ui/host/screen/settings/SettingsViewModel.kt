package com.anpe.bingewatch.ui.host.screen.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anpe.bingewatch.constant.Constants
import com.anpe.bingewatch.data.dto.request.Request
import com.anpe.bingewatch.data.entity.WatchEntity
import com.anpe.bingewatch.data.mapper.WatchMapper.Companion.toDto
import com.anpe.bingewatch.data.mapper.WatchMapper.Companion.toEntity
import com.anpe.bingewatch.data.repository.DaoRepository
import com.anpe.bingewatch.data.repository.NetRepository
import com.anpe.bingewatch.ui.host.screen.home.HomeEvent
import com.anpe.bingewatch.utils.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val daoRepo: DaoRepository,
    private val netRepo: NetRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {
    companion object {
        const val TAG = "SettingsViewModel"
    }

    private val _viewAction = Channel<SettingsAction>(Channel.BUFFERED)
    private val viewAction = _viewAction.consumeAsFlow()

    private val _viewEvent: MutableSharedFlow<SettingsEvent> = MutableSharedFlow()
    val viewEvent = _viewEvent.asSharedFlow()

    private val _viewState: MutableStateFlow<SettingsState> = MutableStateFlow(SettingsState())
    val settingsState = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            viewModelScope.launch {
                dataStoreManager.readPreference("url").collect {
                    _viewState.emit(settingsState.value.copy(serverAddress = it))
                }
            }
            viewAction.collect {
                when (it) {
                    SettingsAction.ClearData -> {
                        daoRepo.deleteAllWatch()
                        _viewState.emit(settingsState.value.copy(dialogStatus = false))
                    }

                    SettingsAction.ExportData -> {
                        _viewState.emit(settingsState.value.copy(data = daoRepo.getAllWatch()))
                    }

                    SettingsAction.ImportData -> TODO()
                    SettingsAction.DismissDialog -> {
                        _viewState.emit(settingsState.value.copy(dialogStatus = false))
                    }

                    SettingsAction.ShowDialog -> {
                        _viewState.emit(settingsState.value.copy(dialogStatus = true))
                    }

                    SettingsAction.Sync -> sync()

                    SettingsAction.Upload -> upload()

                    is SettingsAction.SettingServerAddress -> {
                        viewModelScope.launch {
                            _viewState.emit(settingsState.value.copy(serverAddress = it.url))
                            dataStoreManager.editPreference("url", it.url)
                        }
                    }
                }
            }
        }
    }

    suspend fun dispatch(action: SettingsAction) {
        _viewAction.send(action)
    }

    fun uos(vararg watchEntity: WatchEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            daoRepo.upsertWatch(*watchEntity)
        }
    }

    fun upload() {
        viewModelScope.launch {
            if (settingsState.value.serverAddress.isNotEmpty()) {
                val allWatch = daoRepo.getAllWatch()

                try {
                    netRepo.upload(
                        url = "${settingsState.value.serverAddress}${Constants.UPLOAD_URL}",
                        request = Request(
                            data = allWatch.map {
                                it.toDto()
                            }
                        )
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "upload: $e")
                }
            } else {
                _viewEvent.emit(SettingsEvent.Toast("服务器地址无效"))
            }
        }
    }

    fun sync() {
        viewModelScope.launch {
            if (settingsState.value.serverAddress.isNotEmpty()) {
                try {
                    val url = "${settingsState.value.serverAddress}${Constants.SYNC_URL}"
                    val response = netRepo.sync(url)
                    response.data.forEach {
                        daoRepo.findWatchTitleIsAlive(it.title)
                        daoRepo.upsertWatch(it.toEntity())
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "sync: ${e.printStackTrace()}")
                }
            } else {
                _viewEvent.emit(SettingsEvent.Toast("服务器地址无效"))
            }
        }
    }
}