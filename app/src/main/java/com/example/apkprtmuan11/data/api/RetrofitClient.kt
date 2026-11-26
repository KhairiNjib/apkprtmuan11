package com.example.apkprtmuan11.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Ganti dengan Base URL MockAPI Anda; URL harus diakhiri dengan "/".
    private const val BASE_URL = "https://6b0728d657fa1297bb56dc01.mockapi.io/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
