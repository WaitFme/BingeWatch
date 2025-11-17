package com.anpe.bingewatch.core.repository

import com.anpe.bingewatch.core.data.dto.request.Request
import com.anpe.bingewatch.core.data.dto.response.Response

interface NetRepository {
    suspend fun upload(url: String, request: Request)

    suspend fun sync(url: String): Response
}