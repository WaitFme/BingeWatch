package com.anpe.bingewatch.ui.host.screen.main

import com.anpe.bingewatch.data.entity.WatchEntity

sealed class MainIntent {
    data object GetData : MainIntent()

    data object DeleteAllData: MainIntent()

    data class InsertData(val entity: WatchEntity): MainIntent()

    data class DeleteData(val entity: WatchEntity): MainIntent()

    data class UpdateData(val entity: WatchEntity): MainIntent()

    data class FindTitleAlive(val title: String): MainIntent()

    data class EpiIncrease(val id: Long): MainIntent()

    data class EpiDecrease(val id: Long): MainIntent()

    data class CreateWatch(
        val title: String,
        val remarks: String,
        val currentEpi: Int,
        val totalEpi: Int
    ): MainIntent()

    data class UpdateWatch(
        val id: Long,
        val currentEpi: Int,
        val totalEpi: Int,
        val watchState: Int
    ): MainIntent()

    data class DeleteWatch(val id: Long): MainIntent()

    data class UpdateCurrentWatch(val id: Long): MainIntent()
}
