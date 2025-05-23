package com.anpe.bingewatch.ui.host.screen.settings

import com.anpe.bingewatch.data.entity.WatchEntity

data class SettingsState(
    val dialogStatus: Boolean = false,
    val data: List<WatchEntity> = listOf()
)