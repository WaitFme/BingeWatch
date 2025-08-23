package com.anpe.bingewatch.data.dto.request

import com.anpe.bingewatch.data.dto.conmon.WatchDto
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Request(
    @SerialName("data")
    val data: List<WatchDto> = listOf()
)
