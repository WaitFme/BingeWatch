package com.anpe.bingewatch.data.dto.request

import com.anpe.bingewatch.data.dto.WatchDto
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Request(
    @Json(name = "data")
    val data: List<WatchDto> = listOf()
)
