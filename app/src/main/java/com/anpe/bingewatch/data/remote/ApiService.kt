package com.anpe.bingewatch.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

interface ApiService {
    companion object {
        private const val BASE_URL = ""

        private var service: ApiService? = null

        fun getService(): ApiService {
            if (service == null) {
                val client = OkHttpClient.Builder().build()

                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()

                service = retrofit.create(ApiService::class.java)
            }

            return service as ApiService
        }
    }
}