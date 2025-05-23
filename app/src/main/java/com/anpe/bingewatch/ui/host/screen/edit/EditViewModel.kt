package com.anpe.bingewatch.ui.host.screen.edit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anpe.bingewatch.data.entity.WatchEntity
import com.anpe.bingewatch.data.repository.WatchRepository
import com.anpe.bingewatch.utils.Tools.Companion.getWatchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(private val repository: WatchRepository) : ViewModel() {
    private val _viewEvents = Channel<EditEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    private val _editState: MutableStateFlow<EditState> = MutableStateFlow(EditState())
    val editState = _editState.asStateFlow()

    private fun insertWatch(title: String, remarks: String, ce: Int, te: Int, createTime: Long) {
        val entity = WatchEntity(
            title = title,
            currentEpisode = ce,
            totalEpisode = te,
            watchState = getWatchState(ce, te),
            createTime = createTime,
            changeTime = createTime,
            remarks = remarks,
            isDelete = false
        )

        repository.upsertWatch(entity)
    }

    private fun createData() {
        viewModelScope.launch {
            try {
                insertWatch(
                    editState.value.title,
                    editState.value.remarks,
                    editState.value.currentEpisode.toInt(),
                    editState.value.totalEpisode.toInt(),
                    editState.value.createTime
                )
                _viewEvents.send(EditEvent.PopBack)
            } catch (e: NumberFormatException) {
                Log.d("TAG", "DialogContent: $e")
            }
        }
    }

    fun dispatch(action: EditAction) {
        when (action) {
            EditAction.CreateData -> createData()
        }
    }

    fun changeTitle(title: String) {
        viewModelScope.launch {
            _editState.emit(_editState.value.copy(title = title, titleAlive = repository.findWatchTitleIsAlive(title).isNotEmpty()))
        }
    }

    fun changeCE(ce: String) {
        viewModelScope.launch {
            _editState.emit(_editState.value.copy(currentEpisode = ce))
        }
    }

    fun changeTE(te: String) {
        viewModelScope.launch {
            _editState.emit(_editState.value.copy(totalEpisode = te))
        }
    }

    fun changeCreateTime(createTime: Long) {
        viewModelScope.launch {
            _editState.emit(_editState.value.copy(createTime = createTime))
        }
    }
}