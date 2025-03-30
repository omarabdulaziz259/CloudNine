package com.example.cloudnine.model.dataSource.remote

import com.example.cloudnine.model.Language
import com.example.cloudnine.model.TemperatureUnit
import com.example.cloudnine.model.ForecastResponse
import com.example.cloudnine.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf



class WeatherRemoteDataSource private constructor(private val service: ApiService) {

    suspend fun getCurrentDayWeather(
        lon: Double, lat: Double, units: TemperatureUnit, language: Language
    ): Flow<WeatherResponse> {
        return flowOf(
            service.getCurrentDayWeather(
                lon = lon, lat = lat, units = units.apiValue, lang = language.apiValue
            )
        )
    }

    suspend fun getDailyForecasts(
        lon: Double, lat: Double, units: TemperatureUnit, language: Language
    ): Flow<ForecastResponse> {
        return flowOf(
            service.getDailyForecasts(
                lon = lon, lat = lat, units = units.apiValue, lang = language.apiValue
            )
        )
    }


    companion object {
        @Volatile
        private var INSTANCE: WeatherRemoteDataSource? = null

        fun getInstance(): WeatherRemoteDataSource {

            return INSTANCE ?: synchronized(this) {
                val service = RetroFitHelper.apiService
                val instance = WeatherRemoteDataSource(service)
                INSTANCE = instance
                instance
            }
        }
    }

}