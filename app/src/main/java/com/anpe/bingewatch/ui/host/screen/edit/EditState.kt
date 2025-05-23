package com.anpe.bingewatch.ui.host.screen.edit

data class EditState(
    val title: String = "",
    val remarks: String = "",
    val titleAlive: Boolean = false,
    val currentEpisode: String = "",
    val totalEpisode: String = "",
    val createTime: Long = 0L,
    val createStateCode: Int = 0
)