package com.example.cloudnine.model.dataSource.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroFitHelper {

    private val retrofitInstance = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofitInstance.create(ApiService::class.java)
}