package com.anpe.bingewatch.core.repository

import com.anpe.bingewatch.core.data.dto.request.Request
import com.anpe.bingewatch.core.network.NetApi
import com.anpe.bingewatch.core.data.dto.response.Response
import javax.inject.Inject

class NetRepositoryImpl @Inject constructor(private val api: NetApi): NetRepository {
    companion object { private const val TAG = "NetRepositoryImpl" }

    override suspend fun upload(url: String, request: Request) {
        api.upload(url, request)
    }

    override suspend fun sync(url: String): Response {
        return api.sync(url)
    }
}