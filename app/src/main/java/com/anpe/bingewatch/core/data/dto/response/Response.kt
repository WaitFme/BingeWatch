package com.anpe.bingewatch.core.data.dto.response

import com.anpe.bingewatch.core.data.dto.conmon.WatchDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Response(
    @SerialName("data")
    val data: List<WatchDto> = listOf()
)
