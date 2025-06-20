package com.anpe.bingewatch.data.dto.response

import com.anpe.bingewatch.data.dto.conmon.WatchDto
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Response(
    @Json(name = "data")
    val data: List<WatchDto> = listOf()
)
