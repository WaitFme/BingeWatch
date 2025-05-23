package com.anpe.bingewatch.ui.host.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anpe.bingewatch.data.entity.WatchEntity
import com.anpe.bingewatch.data.repository.WatchRepository
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
class SettingsViewModel @Inject constructor(private val repository: WatchRepository) : ViewModel() {
    private val _viewAction = Channel<SettingsAction>(Channel.BUFFERED)
    private val viewAction = _viewAction.consumeAsFlow()

    private val _viewEvent: MutableSharedFlow<SettingsEvent> = MutableSharedFlow()
    val viewEvent = _viewEvent.asSharedFlow()

    private val _viewState: MutableStateFlow<SettingsState> = MutableStateFlow(SettingsState())
    val settingsState = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            viewAction.collect {
                when (it) {
                    SettingsAction.ClearData -> {
                        repository.deleteAllWatch()
                        _viewState.emit(settingsState.value.copy(dialogStatus = false))
                    }
                    SettingsAction.ExportData -> {
                        _viewState.emit(settingsState.value.copy(data = repository.getAllWatch()))
                    }
                    SettingsAction.ImportData -> TODO()
                    SettingsAction.DismissDialog -> {
                        _viewState.emit(settingsState.value.copy(dialogStatus = false))
                    }
                    SettingsAction.ShowDialog -> {
                        _viewState.emit(settingsState.value.copy(dialogStatus = true))
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
            repository.upsertWatch(*watchEntity)
        }
    }
}