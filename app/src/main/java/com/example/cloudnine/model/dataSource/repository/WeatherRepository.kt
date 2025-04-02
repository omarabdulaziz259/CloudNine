package com.example.cloudnine.model.dataSource.repository

import android.content.Context
import com.example.cloudnine.model.ForecastResponse
import com.example.cloudnine.model.Language
import com.example.cloudnine.model.TemperatureUnit
import com.example.cloudnine.model.WeatherResponse
import com.example.cloudnine.model.dataSource.remote.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow

class WeatherRepository private constructor(val remoteDataSource: WeatherRemoteDataSource) {

    suspend fun getRemoteCurrentDayWeather(lat: Double, lon: Double, temperatureUnit: TemperatureUnit, language: Language): Flow<WeatherResponse?> {
        return remoteDataSource.getCurrentDayWeather(lat = lat , lon = lon , units = temperatureUnit , language = language)
    }

    suspend fun getRemoteDailyForecasts(lat: Double, lon: Double, temperatureUnit: TemperatureUnit, language: Language): Flow<ForecastResponse?> {
        return remoteDataSource.getDailyForecasts( lat = lat , lon = lon , units = temperatureUnit , language = language)
    }

    companion object{
        @Volatile
        private var instance : WeatherRepository?=null
        fun getInstance(context: Context): WeatherRepository{
            return instance?:synchronized(this) {
                val temp = WeatherRepository(WeatherRemoteDataSource.getInstance())
                instance = temp
                temp
            }
        }
    }
}