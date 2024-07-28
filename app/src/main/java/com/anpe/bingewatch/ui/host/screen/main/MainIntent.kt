package com.anpe.bingewatch.ui.host.screen.main

import androidx.room.Entity
import com.anpe.bingewatch.data.entity.WatchNewEntity

sealed class MainIntent {
    data object GetData : MainIntent()
    data class InsertData(val entity: WatchNewEntity): MainIntent()
    data class DeleteData(val entity: WatchNewEntity): MainIntent()
    data class UpdateData(val entity: WatchNewEntity): MainIntent()
    data class FindTitleAlive(val title: String): MainIntent()
    data object DeleteAllData: MainIntent()
}
