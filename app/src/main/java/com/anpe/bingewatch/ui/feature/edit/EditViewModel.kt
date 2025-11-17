package com.anpe.bingewatch.ui.host.screen.edit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anpe.bingewatch.core.data.entity.WatchEntity
import com.anpe.bingewatch.core.repository.DaoRepository
import com.anpe.bingewatch.utils.Tools.Companion.getWatchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(private val daoRepo: DaoRepository) : ViewModel() {
    private val _viewEvents = Channel<EditEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    private val _editState: MutableStateFlow<EditState> = MutableStateFlow(EditState())
    val editState = _editState.asStateFlow()

    private fun insertWatch(title: String, remarks: String, ce: Int, te: Int, createTime: Long, id: Long = 0) {
        val entity = WatchEntity(
            id = id,
            title = title,
            currentEpisode = ce,
            totalEpisode = te,
            watchState = getWatchState(ce, te),
            createTime = if (createTime == 0L) System.currentTimeMillis() else createTime,
            changeTime = if (createTime == 0L) System.currentTimeMillis() else createTime,
            remarks = remarks,
            isDelete = false
        )

        daoRepo.upsertWatch(entity)
    }

    private fun createData() {
        viewModelScope.launch {
            val watch = daoRepo.findWatch(editState.value.title)
            if (editState.value.titleAlive) {
                EditEvent.ShowToast("名称已存在！")
                return@launch
            }
            try {
                if (watch.isNotEmpty()) {
                    insertWatch(
                        editState.value.title,
                        editState.value.remarks,
                        editState.value.currentEpisode.toInt(),
                        editState.value.totalEpisode.toInt(),
                        editState.value.createTime,
                        watch.first().id
                    )
                } else {
                    insertWatch(
                        editState.value.title,
                        editState.value.remarks,
                        editState.value.currentEpisode.toInt(),
                        editState.value.totalEpisode.toInt(),
                        editState.value.createTime
                    )
                }
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
            val watch = daoRepo.findWatch(title)
            val titleAlive = if (watch.isNotEmpty()) {
                !watch.first().isDelete
            } else {
                false
            }
            _editState.emit(_editState.value.copy(title = title, titleAlive = titleAlive))
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