package com.anpe.bingewatch.ui.host.screen.home

import androidx.compose.ui.text.input.TextFieldValue
import com.anpe.bingewatch.data.entity.WatchEntity

data class HomeState(
    val id: Long = 0,
    val title: String = "",
    val currentEpi: TextFieldValue = TextFieldValue(),
    val totalEpi: TextFieldValue = TextFieldValue(),
    val selectTab: Int = 0,
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false,
    val showDialog: Boolean = false,
    val data: List<WatchEntity> = listOf(),
    val errorMessage: String = ""
)
