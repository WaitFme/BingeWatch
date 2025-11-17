package com.anpe.bingewatch.ui.host.screen.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anpe.bingewatch.core.data.entity.WatchEntity
import com.anpe.bingewatch.core.repository.DaoRepository
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
    val viewState = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            dataStoreManager.readPreference("url").collect {
                _viewState.emit(viewState.value.copy(serverAddress = it))
            }
        }
        viewModelScope.launch {
            dataStoreManager.readIntPreference("sort_type").collect {
                _viewState.emit(viewState.value.copy(sortType = it))
            }
        }
        viewModelScope.launch {
            viewAction.collect {
                when (it) {
                    SettingsAction.ClearData -> {
                        daoRepo.deleteAllWatch()
                    }

                    SettingsAction.ExportData -> {
                        _viewState.emit(viewState.value.copy(data = daoRepo.getAllWatch()))
                    }

                    SettingsAction.ImportData -> { }

                    is SettingsAction.ChangeServerAddress -> {
                        viewModelScope.launch {
                            _viewState.emit(viewState.value.copy(serverAddress = it.url))
                            dataStoreManager.editPreference("url", it.url)
                        }
                    }

                    is SettingsAction.ChangeSortType -> {
                        viewModelScope.launch {
                            _viewState.emit(viewState.value.copy(sortType = it.sortType))
                            dataStoreManager.editIntPreference("sort_type", it.sortType)
                            Log.d(TAG, "sort: ${it.sortType}")
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
}