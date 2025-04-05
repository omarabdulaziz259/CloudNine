package com.example.cloudnine.model.dataSource.local.favoriteCity

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cloudnine.model.dataSource.local.favoriteCity.model.FavoriteCity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCityDao {

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertFavCity(favoriteCity: FavoriteCity) : Long

    @Delete
    suspend fun deleteFavCity(favoriteCity: FavoriteCity) : Int

    @Query("SELECT * FROM favorite_cities")
    fun getAllCities(): Flow<List<FavoriteCity>>
}