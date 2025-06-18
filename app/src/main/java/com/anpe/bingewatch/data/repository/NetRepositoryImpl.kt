package com.anpe.bingewatch.data.repository

import com.anpe.bingewatch.data.dto.request.Request
import com.anpe.bingewatch.data.network.NetApi
import javax.inject.Inject

class NetRepositoryImpl @Inject constructor(private val api: NetApi): NetRepository {
    companion object { private const val TAG = "NetRepositoryImpl" }

    override suspend fun upload(url: String, request: Request) {
        api.upload(url, request)
    }

    override suspend fun sync(url: String): com.anpe.bingewatch.data.dto.response.Response {
        return api.sync(url)
    }
}