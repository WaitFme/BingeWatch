package com.anpe.bingewatch.core.network

import com.anpe.bingewatch.core.data.dto.request.Request
import com.anpe.bingewatch.core.data.dto.response.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface NetApi {
    @POST
    suspend fun upload(@Url url: String, @Body request: Request)

    @GET
    suspend fun sync(@Url url: String): Response
}