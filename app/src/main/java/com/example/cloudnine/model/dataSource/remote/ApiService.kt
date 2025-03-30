package com.example.cloudnine.model.dataSource.remote

import com.example.cloudnine.BuildConfig
import com.example.cloudnine.model.Language
import com.example.cloudnine.model.TemperatureUnit
import com.example.cloudnine.model.ForecastResponse
import com.example.cloudnine.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

val apiKey = BuildConfig.API_KEY
interface ApiService {
    @GET("data/2.5/weather")
    suspend fun getCurrentDayWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apikey: String = apiKey,
        @Query("units") units: String? = TemperatureUnit.KELVIN.apiValue,
        @Query("lang") lang: String = Language.ENGLISH.apiValue
    ): WeatherResponse

    @GET("data/2.5/forecast")
    suspend fun getDailyForecasts(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apikey: String = apiKey,
        @Query("units") units: String? = TemperatureUnit.KELVIN.apiValue,
        @Query("lang") lang: String = Language.ENGLISH.apiValue
    ): ForecastResponse
}