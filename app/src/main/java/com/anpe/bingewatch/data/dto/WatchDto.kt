package com.anpe.bingewatch.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WatchDto(
    @Json(name = "title")
    val title: String,
    @Json(name = "remarks")
    val remarks: String,
    @Json(name = "currentEpisode")
    val currentEpisode: Int,
    @Json(name = "totalEpisode")
    val totalEpisode: Int,
    @Json(name = "state")
    val state: Int,
    @Json(name = "createTime")
    val createTime: Long,
    @Json(name = "changeTime")
    val changeTime: Long,
    @Json(name = "isDelete")
    val isDelete: Boolean,
)
