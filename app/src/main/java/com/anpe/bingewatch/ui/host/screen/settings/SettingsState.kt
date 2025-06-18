package com.anpe.bingewatch.ui.host.screen.settings

import androidx.compose.ui.text.input.TextFieldValue
import com.anpe.bingewatch.data.entity.WatchEntity

data class SettingsState(
    val dialogStatus: Boolean = false,
    val serverAddress: String = "",
    val serverAddress1: TextFieldValue = TextFieldValue(""),
    val data: List<WatchEntity> = listOf()
)