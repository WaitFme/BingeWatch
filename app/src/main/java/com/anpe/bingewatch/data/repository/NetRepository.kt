package com.anpe.bingewatch.data.repository

import com.anpe.bingewatch.data.dto.request.Request
import com.anpe.bingewatch.data.dto.response.Response

interface NetRepository {
    suspend fun upload(url: String, request: Request)

    suspend fun sync(url: String): Response
}