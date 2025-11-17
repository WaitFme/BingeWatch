package com.anpe.bingewatch.core.data.dto.conmon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WatchDto(
    @SerialName("title")
    val title: String,
    @SerialName("remarks")
    val remarks: String,
    @SerialName("currentEpisode")
    val currentEpisode: Int,
    @SerialName("totalEpisode")
    val totalEpisode: Int,
    @SerialName("state")
    val state: Int,
    @SerialName("createTime")
    val createTime: Long,
    @SerialName("changeTime")
    val changeTime: Long,
    @SerialName("isDelete")
    val isDelete: Boolean,
)