package com.example.cloudnine.model.dataSource.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cloudnine.model.dataSource.local.alarm.AlarmDao
import com.example.cloudnine.model.dataSource.local.alarm.model.AlarmModel
import com.example.cloudnine.model.dataSource.local.favoriteCity.FavoriteCityDao
import com.example.cloudnine.model.dataSource.local.favoriteCity.model.FavoriteCity

@Database(entities = [FavoriteCity::class, AlarmModel::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun getFavoriteCityDao(): FavoriteCityDao
    abstract fun getAlarmDao(): AlarmDao

    companion object {
        @Volatile
        private var instance: WeatherDatabase? = null
        fun getInstance(context: Context): WeatherDatabase {
            return instance ?: synchronized(this) {
                val temp: WeatherDatabase = Room.databaseBuilder(
                    context,
                    WeatherDatabase::class.java,
                    "WeatherDatabase"
                ).build()
                instance = temp
                temp
            }
        }
    }
}