package com.example.cloudnine.model.dataSource.local

import android.content.Context
import com.example.cloudnine.model.dataSource.local.alarm.AlarmDao
import com.example.cloudnine.model.dataSource.local.alarm.model.AlarmModel
import com.example.cloudnine.model.dataSource.local.favoriteCity.FavoriteCityDao
import com.example.cloudnine.model.dataSource.local.favoriteCity.model.FavoriteCity
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource(val favoriteCityDao: FavoriteCityDao, val alarmDao: AlarmDao){
    fun getAllFavoriteCities(): Flow<List<FavoriteCity>> {
        return favoriteCityDao.getAllCities()
    }

    suspend fun addFavoriteCity(favoriteCity: FavoriteCity) : Long{
        return favoriteCityDao.insertFavCity(favoriteCity)
    }

    suspend fun deleteFavoriteCity(favoriteCity: FavoriteCity) : Int {
        return favoriteCityDao.deleteFavCity(favoriteCity)
    }

    fun getAllAlarms(): Flow<List<AlarmModel>> {
        return alarmDao.getAllAlarms()
    }

    suspend fun insertAlarm(alarm: AlarmModel): Long {
        return alarmDao.insertAlarm(alarm)
    }

    suspend fun deleteAlarm(alarm: AlarmModel): Int {
        return alarmDao.deleteAlarm(alarm)
    }

    suspend fun deleteAlarmById(alarmId: Int) {
        alarmDao.deleteAlarmById(alarmId)
    }

    suspend fun getAlarmById(alarmId: Int): AlarmModel? {
        return alarmDao.getAlarmById(alarmId)
    }

    companion object {
        @Volatile
        private var instance: WeatherLocalDataSource? = null
        fun getInstance(context: Context): WeatherLocalDataSource {
            return instance ?: synchronized(this) {
                val db = WeatherDatabase.getInstance(context)
                val temp = WeatherLocalDataSource(db.getFavoriteCityDao(),
                    db.getAlarmDao())
                instance = temp
                temp
            }
        }
    }
}