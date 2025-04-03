package com.example.cloudnine.model.dataSource.local

import android.content.Context
import com.example.cloudnine.model.dataSource.local.model.FavoriteCity
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource(val favoriteCityDao: FavoriteCityDao){
    suspend fun getAllFavoriteCities(): Flow<List<FavoriteCity>> {
        return favoriteCityDao.getAllCities()
    }

    suspend fun addFavoriteCity(favoriteCity: FavoriteCity) : Long{
        return favoriteCityDao.insertFavCity(favoriteCity)
    }

    suspend fun deleteFavoriteCity(favoriteCity: FavoriteCity) : Int {
        return favoriteCityDao.deleteFavCity(favoriteCity)
    }

    companion object {
        @Volatile
        private var instance: WeatherLocalDataSource? = null
        fun getInstance(context: Context): WeatherLocalDataSource {
            return instance ?: synchronized(this) {
                val temp = WeatherLocalDataSource(
                    WeatherDatabase.getInstance(context).getFavoriteCityDao(),
                )
                instance = temp
                temp
            }
        }
    }
}