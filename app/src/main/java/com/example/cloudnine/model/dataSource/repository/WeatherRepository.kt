package com.example.cloudnine.model.dataSource.repository

import android.content.Context
import com.example.cloudnine.model.ForecastResponse
import com.example.cloudnine.model.Language
import com.example.cloudnine.model.TemperatureUnit
import com.example.cloudnine.model.WeatherResponse
import com.example.cloudnine.model.dataSource.local.WeatherLocalDataSource
import com.example.cloudnine.model.dataSource.local.alarm.model.AlarmModel
import com.example.cloudnine.model.dataSource.local.favoriteCity.model.FavoriteCity
import com.example.cloudnine.model.dataSource.remote.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow

class WeatherRepository private constructor(
    val remoteDataSource: WeatherRemoteDataSource, val LocalDataSource: WeatherLocalDataSource
) {

    suspend fun getRemoteCurrentDayWeather(
        lat: Double, lon: Double, temperatureUnit: TemperatureUnit, language: Language
    ): Flow<WeatherResponse?> {
        return remoteDataSource.getCurrentDayWeather(
            lat = lat, lon = lon, units = temperatureUnit, language = language
        )
    }

    suspend fun getRemoteDailyForecasts(
        lat: Double,
        lon: Double,
        temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
        language: Language = Language.ENGLISH
    ): Flow<ForecastResponse?> {
        return remoteDataSource.getDailyForecasts(
            lat = lat, lon = lon, units = temperatureUnit, language = language
        )
    }

    suspend fun getLocalAllFavoriteCities(): Flow<List<FavoriteCity>> {
        return LocalDataSource.getAllFavoriteCities()
    }

    suspend fun addLocalFavoriteCity(favoriteCity: FavoriteCity): Long {
        return LocalDataSource.addFavoriteCity(favoriteCity)
    }

    suspend fun deleteLocalFavoriteCity(favoriteCity: FavoriteCity): Int {
        return LocalDataSource.deleteFavoriteCity(favoriteCity)
    }

    fun getLocalAllAlarms(): Flow<List<AlarmModel>> {
        return LocalDataSource.getAllAlarms()
    }

    suspend fun insertLocalAlarm(alarm: AlarmModel): Long {
        return LocalDataSource.insertAlarm(alarm)
    }

    suspend fun deleteLocalAlarm(alarm: AlarmModel): Int {
        return LocalDataSource.deleteAlarm(alarm)
    }

    suspend fun deleteLocalAlarmById(alarmId: Int) {
        LocalDataSource.deleteAlarmById(alarmId)
    }

    suspend fun getLocalAlarmById(alarmId: Int): AlarmModel? {
        return LocalDataSource.getAlarmById(alarmId)
    }

    companion object {
        @Volatile
        private var instance: WeatherRepository? = null
        fun getInstance(context: Context): WeatherRepository {
            return instance ?: synchronized(this) {
                val temp = WeatherRepository(
                    WeatherRemoteDataSource.getInstance(),
                    WeatherLocalDataSource.getInstance(context)
                )
                instance = temp
                temp
            }
        }
    }
}