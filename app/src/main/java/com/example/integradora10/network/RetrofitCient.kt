package com.example.integradora10.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // AQUÍ pones la IP de la compu donde corre el Python
    // Ejemplo: "http://192.168.1.10:5000/"
    private const val BASE_URL = " http://192.168.1.72:5000"

    val apiService: PlantApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PlantApiService::class.java)
    }
}