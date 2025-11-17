package com.anpe.bingewatch.ui.host.screen.settings

import androidx.compose.ui.text.input.TextFieldValue
import com.anpe.bingewatch.core.data.entity.WatchEntity

data class SettingsState(
    val serverAddress: String = "",
    val serverAddress1: TextFieldValue = TextFieldValue(""),
    val sortType: Int = 0,
    val data: List<WatchEntity> = listOf()
)